package line.lineclient.module.modules.util;

import com.google.common.collect.Lists;
import line.lineclient.event.Event;
import line.lineclient.event.events.EventRender;
import line.lineclient.event.events.EventTick;
import line.lineclient.module.Category;
import line.lineclient.module.Module;
import line.lineclient.ui.clickgui.setting.settings.ModulePanelManager;
import line.lineclient.ui.ab.AutoBuyConfig;
import line.lineclient.ui.ab.AutoBuyConfigGui;
import line.lineclient.ui.clickgui.ClickGUI;
import line.lineclient.utils.render.RenderUtils;
import line.lineclient.utils.fonts.Fonts;
import lombok.Getter;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.client.util.InputMappings;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import static line.lineclient.utils.Wrapper.mc;

public class AutoBuy extends Module {
    private long lastStealTime = 0;
    private long lastGuiOpenTime = 0;
    private boolean wasInSearchMenu = false;
    private int purchaseAttempts = 0;
    private int lastAttemptedSlot = -1;

    private static final int MAX_ATTEMPTS = 5;
    private static final long CLICK_DELAY = 50;
    private static final long RETRY_DELAY = 75;

    @Getter private final List<AutoBuyConfig> itemConfigs = new ArrayList<>();
    @Getter private final List<AutoBuyConfig> configs = Lists.newArrayList();

    public AutoBuy() {
        super("AutoBuy", Category.UTIL, "Автоматично купує предмети з аукціону", -1, 0);
    }

    @Override
    public void onEnable() {
        ModulePanelManager.addModule(getName());
    }

    @Override
    public void onDisable() {
        ModulePanelManager.removeModule(getName());
    }

    public void openConfig() {
        mc.displayGuiScreen(new AutoBuyConfigGui(this));
    }

    @Override
    public void event(Event e) {
        if (e instanceof EventRender) {
            renderModule((EventRender) e);
        } else if (e instanceof EventTick) {
            handleTick();
        }
    }

    private void handleTick() {
        if (InputMappings.isKeyDown(mc.getMainWindow().getHandle(), 342)) { // клавіша ALT
            mc.displayGuiScreen(new AutoBuyConfigGui(this));
        }

        if (!(mc.currentScreen instanceof ChestScreen)) {
            wasInSearchMenu = false;
            lastGuiOpenTime = 0;
            purchaseAttempts = 0;
            lastAttemptedSlot = -1;
            return;
        }

        ChestScreen screen = (ChestScreen) mc.currentScreen;
        ChestContainer container = (ChestContainer) mc.player.openContainer;
        boolean isSearchMenu = screen.getTitle().getString().contains("Поиск") ||
                screen.getTitle().getString().contains("Аукцион");

        if (isSearchMenu && !wasInSearchMenu) {
            lastGuiOpenTime = System.currentTimeMillis();
            wasInSearchMenu = true;
            purchaseAttempts = 0;
            lastAttemptedSlot = -1;
            return;
        }

        if (isSearchMenu && System.currentTimeMillis() - lastGuiOpenTime < 1500) {
            return;
        }

        handleInventorySearch(container, isSearchMenu);
    }

    private void handleInventorySearch(ChestContainer container, boolean isSearchMenu) {
        IInventory inv = container.getLowerChestInventory();
        boolean foundPurchaseableItem = false;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack.isEmpty()) continue;

            String itemName = stack.getDisplayName().getString();
            int price = extractPriceFromStack(stack);

            if (itemName.contains("[Кyпить]") || (price != -1 && shouldBuyItem(stack, price))) {
                foundPurchaseableItem = true;
                if (handlePurchase(container, i, itemName, price)) {
                    return;
                }
            }
        }

        if (!foundPurchaseableItem && isSearchMenu) {
            refreshAuctionHouse();
        }
    }

    private boolean handlePurchase(ChestContainer container, int slot, String itemName, int price) {
        long currentTime = System.currentTimeMillis();
        boolean isRetry = slot == lastAttemptedSlot;
        long requiredDelay = isRetry ? RETRY_DELAY : CLICK_DELAY;

        if (currentTime - lastStealTime >= requiredDelay) {
            if (isRetry && purchaseAttempts < MAX_ATTEMPTS) {
                purchaseAttempts++;
                performPurchase(container, slot, itemName, price, true);
                return true;
            } else if (!isRetry) {
                purchaseAttempts = 1;
                lastAttemptedSlot = slot;
                performPurchase(container, slot, itemName, price, false);
                return true;
            }
        }
        return false;
    }

    private void performPurchase(ChestContainer container, int slot, String itemName, int price, boolean isRetry) {
        mc.playerController.windowClick(container.windowId, slot, 0, ClickType.QUICK_MOVE, mc.player);
        lastStealTime = System.currentTimeMillis();

        String message = String.format("§a[AutoBuy] %s предмет: %s%s",
                isRetry ? "Спроба " + purchaseAttempts + " купити" : "Клікнув на",
                itemName,
                price != -1 ? " §7| §fЦіна: " + price : "");

        mc.player.sendMessage(new StringTextComponent(message), mc.player.getUniqueID());
    }

    private void refreshAuctionHouse() {
        new Thread(() -> {
            try {
                mc.player.closeScreen();
                Thread.sleep(1);
                mc.player.sendChatMessage("/ah");
                purchaseAttempts = 0;
                lastAttemptedSlot = -1;
            } catch (InterruptedException ignored) {}
        }).start();
    }

    private boolean shouldBuyItem(ItemStack stack, int price) {
        if (configs.isEmpty()) {
            return price <= 500;
        }

        String itemName = stack.getDisplayName().getString().toLowerCase();
        for (AutoBuyConfig config : configs) {
            String configItemName = config.getItemName().toLowerCase().replace("_", " ");
            String stackItemName = itemName.replace("_", " ");

            if (config.isEnabled() && stackItemName.contains(configItemName) && price <= config.getMaxPrice()) {
                return true;
            }
        }
        return false;
    }

    protected int extractPriceFromStack(ItemStack stack) {
        if (!stack.hasTag()) return -1;

        CompoundNBT tag = stack.getTag();
        if (!tag.contains("display", 10)) return -1;

        CompoundNBT display = tag.getCompound("display");
        if (!display.contains("Lore", 9)) return -1;

        ListNBT lore = display.getList("Lore", 8);
        for (int i = 0; i < lore.size(); i++) {
            String loreLine = lore.getString(i);
            if (loreLine.contains("Ценa")) {
                String priceText = loreLine.replaceAll(".*\\$", "").replaceAll("[^0-9]", "");
                try {
                    return Integer.parseInt(priceText);
                } catch (NumberFormatException e) {
                    continue;
                }
            }
        }
        return -1;
    }

    private void renderModule(EventRender e) {
        if (mc == null || mc.getMainWindow() == null) return;

        int yPos = ModulePanelManager.getModulePosition(getName());
        int screenWidth = mc.getMainWindow().getScaledWidth();

        Color backgroundColor = isEnable()
                ? new Color(
                ClickGUI.getThemeColors()[3].getColor().getRed(),
                ClickGUI.getThemeColors()[3].getColor().getGreen(),
                ClickGUI.getThemeColors()[3].getColor().getBlue(),
                150)
                : new Color(
                ClickGUI.getThemeColors()[1].getColor().getRed(),
                ClickGUI.getThemeColors()[1].getColor().getGreen(),
                ClickGUI.getThemeColors()[1].getColor().getBlue(),
                150);

        // Фон
        RenderUtils.drawRoundedRect(
                screenWidth - 105, yPos,
                100, 20, 3,
                backgroundColor
        );

        // Тінь
        RenderUtils.drawShadow(
                screenWidth - 105, yPos,
                100, 20,
                6,
                new Color(
                        ClickGUI.getThemeColors()[1].getColor().getRed(),
                        ClickGUI.getThemeColors()[1].getColor().getGreen(),
                        ClickGUI.getThemeColors()[1].getColor().getBlue()
                ).getRGB()
        );

        // Текст
        Fonts.gilroy[15].drawString(
                e.getMatrixStack(),
                getName() + (isEnable() ? " ON" : " OFF"),
                screenWidth - 100,
                yPos + 5,
                new Color(
                        ClickGUI.getThemeColors()[4].getColor().getRed(),
                        ClickGUI.getThemeColors()[4].getColor().getGreen(),
                        ClickGUI.getThemeColors()[4].getColor().getBlue()
                ).getRGB()
        );
    }
}