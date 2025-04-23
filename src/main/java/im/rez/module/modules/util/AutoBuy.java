package im.rez.module.modules.util;

import lombok.Getter;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.client.util.InputMappings;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.StringTextComponent;
import im.rez.event.Event;
import im.rez.event.events.EventTick;
import im.rez.module.Category;
import im.rez.module.Module;
import im.rez.ui.ab.AutoBuyConfig;
import im.rez.ui.ab.AutoBuyConfigGui;

import java.util.ArrayList;
import java.util.List;

import static im.rez.utils.Wrapper.mc;

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
    @Getter private final List<AutoBuyConfig> configs = new ArrayList<>();

    public AutoBuy() {
        super("AutoBuy", Category.UTIL, "Ловит предметы", -1, 0);
    }

    public void openConfig() {
        mc.displayGuiScreen(new AutoBuyConfigGui(this));
    }

    @Override
    public void event(Event e) {
        if(e instanceof EventTick) {
            if (InputMappings.isKeyDown(mc.getMainWindow().getHandle(), 342)) {
                mc.displayGuiScreen(new AutoBuyConfigGui(this));
            }

            if(mc.currentScreen instanceof ChestScreen) {
                ChestScreen screen = (ChestScreen) mc.currentScreen;
                ChestContainer container = (ChestContainer) mc.player.openContainer;

                boolean isSearchMenu = screen.getTitle().getString().contains("Поиск") || screen.getTitle().getString().contains("Аукцион");

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

                IInventory inv = container.getLowerChestInventory();
                boolean foundPurchaseableItem = false;

                for (int i = 0; i < inv.getSizeInventory(); i++) {
                    ItemStack stack = inv.getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        String itemName = stack.getDisplayName().getString();
                        int price = extractPriceFromStack(stack);

                        if (itemName.contains("[Кyпить]") || (price != -1 && shouldBuyItem(stack, price))) {
                            foundPurchaseableItem = true;
                            if (System.currentTimeMillis() - lastStealTime >= (i == lastAttemptedSlot ? RETRY_DELAY : CLICK_DELAY)) {
                                if (i == lastAttemptedSlot && purchaseAttempts < MAX_ATTEMPTS) {
                                    purchaseAttempts++;
                                    mc.playerController.windowClick(container.windowId, i, 0, ClickType.QUICK_MOVE, mc.player);
                                    lastStealTime = System.currentTimeMillis();
                                    mc.player.sendMessage(new StringTextComponent("§a[AutoBuy] Попытка " + purchaseAttempts + " купить предмет: " + itemName + (price != -1 ? " §7| §fЦена: " + price : "")), mc.player.getUniqueID());
                                } else if (i != lastAttemptedSlot) {
                                    purchaseAttempts = 1;
                                    lastAttemptedSlot = i;
                                    mc.playerController.windowClick(container.windowId, i, 0, ClickType.QUICK_MOVE, mc.player);
                                    lastStealTime = System.currentTimeMillis();
                                    mc.player.sendMessage(new StringTextComponent("§a[AutoBuy] Кликнул на предмет: " + itemName + (price != -1 ? " §7| §fЦена: " + price : "")), mc.player.getUniqueID());
                                }
                            }
                            return;
                        }
                    }
                }

                if (!foundPurchaseableItem && isSearchMenu) {
                    new Thread(() -> {
                        try {
                            mc.player.closeScreen();
                            Thread.sleep(1);
                            mc.player.sendChatMessage("/ah");
                            purchaseAttempts = 0;
                            lastAttemptedSlot = -1;
                        } catch (InterruptedException ignored) {}
                    }).start();
                    return;
                }
            } else {
                wasInSearchMenu = false;
                lastGuiOpenTime = 0;
                purchaseAttempts = 0;
                lastAttemptedSlot = -1;
            }
        }
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
        if (stack.hasTag()) {
            CompoundNBT tag = stack.getTag();
            if (tag.contains("display", 10)) {
                CompoundNBT display = tag.getCompound("display");
                if (display.contains("Lore", 9)) {
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
                }
            }
        }
        return -1;
    }
}
