package line.lineclient.ui.ab;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import line.lineclient.module.modules.util.AutoBuy;
import line.lineclient.utils.fonts.Fonts;
import line.lineclient.utils.render.RenderUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static line.lineclient.ui.clickgui.ClickGUI.ishover;
import static line.lineclient.utils.Wrapper.mc;

public class AutoBuyConfigGui extends Screen {
    private final AutoBuy autoBuy;
    private List<AutoBuyConfig> configs;
    private int x, y, w, h;
    private boolean leftButton, rightButton;
    private TextFieldWidget itemNameField;
    private TextFieldWidget priceField;
    private TextFieldWidget nameField;
    private Button addButton;
    private List<String> suggestions = new ArrayList<>();
    private boolean showSuggestions = false;
    private float scrollY = 0;
    private float maxScroll = 0;
    private float suggestionsScroll = 0;
    private static final int MAX_VISIBLE_SUGGESTIONS = 2;


    public AutoBuyConfigGui(AutoBuy autoBuy) {
        super(new StringTextComponent("AutoBuy"));
        this.autoBuy = autoBuy;
        this.configs = autoBuy.getConfigs();
    }




    @Override
    protected void init() {
        super.init();
        maxScroll = Math.max(0, autoBuy.getConfigs().size() * 35 - (h - 90));
        this.w = 400;
        this.h = 300;
        this.x = (this.width - w) / 2;
        this.y = (this.height - h) / 2;

        this.nameField = new TextFieldWidget(font, x + 10, y + h - 30, 200, 20, new StringTextComponent("Item Name"));
        this.priceField = new TextFieldWidget(font, x + 220, y + h - 30, 80, 20, new StringTextComponent("Price"));

        this.nameField.setMaxStringLength(32);
        this.priceField.setMaxStringLength(6);
        this.nameField.setEnabled(true);
        this.priceField.setEnabled(true);

        this.children.add(nameField);
        this.children.add(priceField);

        this.addButton = this.addButton(new Button(x + w - 90, y + h - 30, 80, 20,
                new StringTextComponent("Add"), button -> {
            if (!nameField.getText().isEmpty() && !priceField.getText().isEmpty()) {
                try {
                    int price = Integer.parseInt(priceField.getText());
                    autoBuy.getConfigs().add(new AutoBuyConfig(nameField.getText(), price, true, false));
                    nameField.setText("");
                    priceField.setText("");
                } catch (NumberFormatException ignored) {}
            }
        }));
    }



    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        RenderUtils.drawShadow(x - 5, y - 5, w + 10, h + 10, 10, new Color(38, 34, 34).getRGB());
        RenderUtils.drawRoundedRect(x, y, w, h, 5, new Color(30, 25, 25));
        Fonts.gilroyBold[25].drawString(matrixStack, "AutoBuy", x + 10, y + 10, -1);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        int scale = (int) mc.getMainWindow().getGuiScaleFactor();
        GL11.glScissor((x + 10) * scale, mc.getMainWindow().getHeight() - (y + h - 90) * scale, (w - 20) * scale, (h - 90) * scale);

        int yOffset = 40 - (int)scrollY;
        for (AutoBuyConfig config : autoBuy.getConfigs()) {
            if (y + yOffset + 30 > y + 40 && y + yOffset < y + h - 50) {
                RenderUtils.drawRoundedRect(x + 10, y + yOffset, w - 20, 30, 5, config.isEnabled() ? new Color(105, 105, 149) : new Color(38, 34, 34));
                try {
                    Item item = Registry.ITEM.getOrDefault(new ResourceLocation(config.getItemName()));
                    ItemStack itemStack = new ItemStack(item);
                    RenderSystem.pushMatrix();
                    RenderSystem.enableDepthTest();
                    mc.getItemRenderer().renderItemIntoGUI(itemStack, x + 15, y + yOffset + 7);
                    RenderSystem.popMatrix();
                } catch (Exception ignored) {}
                Fonts.gilroy[20].drawString(matrixStack, config.getItemName(), x + 40, y + yOffset + 5, -1);
                Fonts.gilroy[16].drawString(matrixStack, "Цена: $" + config.getMaxPrice(), x + 40, y + yOffset + 18,
                        new Color(162, 162, 162).getRGB());
                RenderUtils.drawRoundedRect(x + w - 50, y + yOffset + 5, 30, 20, 5,
                        config.isEnabled() ? new Color(105, 105, 149) : new Color(38, 34, 34));
                if (ishover(x + 10, y + yOffset, w - 20, 30, mouseX, mouseY) && rightButton) {
                    autoBuy.getConfigs().remove(config);
                    break;
                }
            }
            yOffset += 35;
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        RenderUtils.drawRoundedRect(x, y + h - 40, w, 40, 5, new Color(38, 34, 34));
        nameField.render(matrixStack, mouseX, mouseY, partialTicks);
        priceField.render(matrixStack, mouseX, mouseY, partialTicks);
        if (showSuggestions && !suggestions.isEmpty() && nameField.isFocused()) {
            int suggestY = y + h - 70;
            int boxHeight = 50;
            RenderUtils.drawRoundedRect(x + 10, suggestY - boxHeight, 200, boxHeight, 5, new Color(20, 20, 20));
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            int scale1 = (int) mc.getMainWindow().getGuiScaleFactor();
            GL11.glScissor((x + 10) * scale1, mc.getMainWindow().getHeight() - (suggestY) * scale1, 200 * scale1, boxHeight * scale1);

            int startIndex = -(int)(suggestionsScroll / 25);
            for (int i = startIndex; i < Math.min(startIndex + suggestions.size(), startIndex + 2); i++) {
                if (i < 0 || i >= suggestions.size()) continue;

                String suggestion = suggestions.get(i);
                int itemY = suggestY - boxHeight + (i - startIndex) * 25;
                if (ishover(x + 10, itemY, 200, 20, mouseX, mouseY)) {
                    RenderUtils.drawRoundedRect(x + 10, itemY, 200, 20, 5, new Color(40, 40, 40));
                }
                Item item = Registry.ITEM.getOrDefault(new ResourceLocation(suggestion));
                ItemStack itemStack = new ItemStack(item);
                RenderSystem.pushMatrix();
                RenderSystem.enableDepthTest();
                mc.getItemRenderer().renderItemIntoGUI(itemStack, x + 15, itemY + 2);
                RenderSystem.popMatrix();
                Fonts.gilroy[16].drawString(matrixStack, suggestion, x + 40, itemY + 6, -1);
            }

            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);

        if (leftButton) leftButton = false;
        if (rightButton) rightButton = false;
    }

    private void updateSuggestions(String input) {
        suggestions.clear();
        if (!input.isEmpty()) {
            showSuggestions = true;
            String lowercaseInput = input.toLowerCase();
            Set<String> existingItems = configs.stream().map(AutoBuyConfig::getItemName).collect(Collectors.toSet());
            getMinecraftItems().stream().filter(item -> !existingItems.contains(item)).filter(item -> item.toLowerCase().contains(lowercaseInput)).limit(5).forEach(suggestions::add);
        } else {
            showSuggestions = false;
        }
    }


    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        boolean result = super.charTyped(typedChar, keyCode);
        updateSuggestions(nameField.getText());
        return result;
    }


    private List<String> getMinecraftItems() {
        List<String> items = new ArrayList<>();
        Map<String, String> itemMap = new HashMap<>();

        // Мусор але нужный
        itemMap.put("Железная руда", "iron_ore");
        itemMap.put("Золотая руда", "gold_ore");
        itemMap.put("Алмазная руда", "diamond_ore");
        itemMap.put("Изумрудная руда", "emerald_ore");
        itemMap.put("Древние обломки", "ancient_debris");
        itemMap.put("Незеритовый лом", "netherite_scrap");
        itemMap.put("Железный слиток", "iron_ingot");
        itemMap.put("Золотой слиток", "gold_ingot");
        itemMap.put("Незеритовый слиток", "netherite_ingot");

        // Незер броня
        itemMap.put("Незеритовый шлем", "netherite_helmet");
        itemMap.put("Незеритовый нагрудник", "netherite_chestplate");
        itemMap.put("Незеритовые поножи", "netherite_leggings");
        itemMap.put("Незеритовые ботинки", "netherite_boots");

        // Незер вещи
        itemMap.put("Незеритовый меч", "netherite_sword");
        itemMap.put("Незеритовая кирка", "netherite_pickaxe");
        itemMap.put("Незеритовый топор", "netherite_axe");
        itemMap.put("Незеритовая лопата", "netherite_shovel");
        itemMap.put("Незеритовая мотыга", "netherite_hoe");

        // Алмазка
        itemMap.put("Алмазный шлем", "diamond_helmet");
        itemMap.put("Алмазный нагрудник", "diamond_chestplate");
        itemMap.put("Алмазные поножи", "diamond_leggings");
        itemMap.put("Алмазные ботинки", "diamond_boots");

        // алмазные
        itemMap.put("Алмазный меч", "diamond_sword");
        itemMap.put("Алмазная кирка", "diamond_pickaxe");
        itemMap.put("Алмазный топор", "diamond_axe");
        itemMap.put("Алмазная лопата", "diamond_shovel");
        itemMap.put("Алмазная мотыга", "diamond_hoe");

        // мусор
        itemMap.put("Лук", "bow");
        itemMap.put("Удочка", "fishing_rod");
        itemMap.put("Золотая морковь", "golden_carrot");
        itemMap.put("Тотем бессмертия", "totem_of_undying");
        itemMap.put("Зачарованное золотое яблоко", "enchanted_golden_apple");
        itemMap.put("Золотое яблоко", "golden_apple");
        items.addAll(itemMap.values());
        return items;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 1) {
            int yOffset = 40 - (int)scrollY;
            for (AutoBuyConfig config : new ArrayList<>(autoBuy.getConfigs())) {
                if (y + yOffset + 30 > y + 40 && y + yOffset < y + h - 50) {
                    if (ishover(x + 10, y + yOffset, w - 20, 30, mouseX, mouseY)) {
                        autoBuy.getConfigs().remove(config);
                        maxScroll = Math.max(0, autoBuy.getConfigs().size() * 35 - (h - 90));
                        return true;
                    }
                }
                yOffset += 35;
            }
        }

        if (button == 0 && showSuggestions) {
            int suggestY = y + h - 70;
            int boxHeight = 50;
            int startIndex = -(int)(suggestionsScroll / 25);

            for (int i = startIndex; i < Math.min(startIndex + 2, suggestions.size()); i++) {
                if (i < 0) continue;
                int itemY = suggestY - boxHeight + (i - startIndex) * 25;
                if (ishover(x + 10, itemY, 200, 20, mouseX, mouseY)) {
                    nameField.setText(suggestions.get(i));
                    showSuggestions = false;
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (showSuggestions && mouseX >= x + 10 && mouseX <= x + 210 &&
                mouseY >= y + h - 70 - MAX_VISIBLE_SUGGESTIONS * 25 && mouseY <= y + h - 70) {
            float maxScroll = Math.max(0, (suggestions.size() - MAX_VISIBLE_SUGGESTIONS) * 25);
            suggestionsScroll = (float) MathHelper.clamp(suggestionsScroll + delta * 16, -maxScroll, 0);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }
}


