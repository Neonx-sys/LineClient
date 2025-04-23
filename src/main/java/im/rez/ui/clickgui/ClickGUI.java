package im.rez.ui.clickgui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import im.rez.module.Category;
import im.rez.module.Module;
import im.rez.module.modules.ModuleManager;
import im.rez.ui.clickgui.setting.Setting;
import im.rez.ui.clickgui.setting.settings.CheckBoxSetting;
import im.rez.ui.clickgui.setting.settings.ColorSetting;
import im.rez.ui.clickgui.setting.settings.ValueSetting;
import im.rez.utils.fonts.Fonts;
import im.rez.utils.render.RenderUtils;
import im.rez.utils.scroll.ScrollUtils;

import java.awt.*;

import static im.rez.utils.Wrapper.mc;

public class ClickGUI extends Screen {
    private static ColorSetting lastOpenedColor = null;
    boolean isFullMenu;
    private Module activeModule = null;
    private float animationProgress = 0;
    public int x,y,w,h,offset,settOffset;
    public Category currentCat = Category.COMBAT;
    int settingsCount = 0;
    public boolean leftButton, rightButton, middleButton;
    private boolean themeMenuOpen = false;
    private static Category lastCategory = Category.COMBAT;
    private static String savedOpenColorName = null;
    private static Color[] savedColors = new Color[6];
    private static boolean savedThemePanelState = false;
    private float themeAnimationProgress = 0f;
    private final float COLLAPSED_HEIGHT = 40f;
    private final float EXPANDED_HEIGHT = 300f;



    public ClickGUI(boolean isFullMenu) {
        super(isFullMenu ? new TranslationTextComponent("menu.game") : new TranslationTextComponent("menu.paused"));
        this.isFullMenu = isFullMenu;
        this.currentCat = lastCategory;

        currentCat = lastCategory;
        themeMenuOpen = savedThemePanelState;
        if (savedOpenColorName != null) {
            for (ColorSetting color : themeColors) {
                if (color.getName().equals(savedOpenColorName)) {
                    color.setOpen(true);
                    break;
                }
            }
        }
    }

    public static ColorSetting[] getThemeColors() {
        return themeColors;
    }

    private static final ColorSetting[] themeColors = new ColorSetting[] {
            new ColorSetting("MainColor", new Color(0x4A4AA1)),
            new ColorSetting("ShadowColor", new Color(31, 33, 41)),
            new ColorSetting("TextColor", new Color(65, 80, 110)),
            new ColorSetting("BackgroundColor", new Color(25, 26, 30)),
            new ColorSetting("FontsColor", new Color(150, 156, 212))
    };

    private static final ThemePreset[] THEME_PRESETS = {
            new ThemePreset("Fatal", new Color[]{
                    new Color(0xFF4A4A),
                    new Color(35, 25, 25),
                    new Color(255, 0, 0),
                    new Color(30, 22, 22),
                    new Color(255, 0, 0)
            }),
            new ThemePreset("Umbrl", new Color[]{
                    new Color(0xFF0000),
                    new Color(33, 31, 41),
                    new Color(255, 0, 0),
                    new Color(255, 0, 0),
                    new Color(255, 0, 0)
            }),
            new ThemePreset("Never", new Color[]{
                    new Color(0xFF0000),
                    new Color(31, 33, 41),
                    new Color(65, 80, 110),
                    new Color(25, 26, 30),
                    new Color(150, 156, 212)
            }),
            new ThemePreset("White", new Color[]{
                    new Color(0x1ABC9C),
                    new Color(225, 235, 230),
                    new Color(15, 16, 15),
                    new Color(247, 250, 248),
                    new Color(23, 25, 23)
            })
    };

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        x = mc.getMainWindow().getWidth() / 4 - w / 2;
        y = mc.getMainWindow().getHeight() / 4 - h / 2;
        w = 340;
        h = 300;
        settOffset = 0;
        offset = 40;
        RenderUtils.drawShadow(x - 5, y - 5, w + 10, h + 10, 10, themeColors[1].getColor().getRGB());
        RenderUtils.drawRoundedRect(x, y, w, h, 5, themeColors[3].getColor());
        RenderUtils.drawRoundedRect(x, y, w, h, 5, themeColors[3].getColor());
        float textWidth = Fonts.gilroyBold[40].getWidth("YOUGAME");
        RenderUtils.drawShadow(x + 8, y + 12, textWidth + 5, 18, 36, themeColors[2].getColor().getRGB());
        Fonts.gilroyBold[40].drawString(matrixStack, "YOUGAME", x + 10, y + 12, themeColors[2].getColor().getRGB());
        float aboutWidth = Fonts.gilroyBold[20].getWidth("About our base");
        RenderUtils.drawRoundedRect(x - 185, y + 5, aboutWidth + 90, 100, 5, themeColors[1].getColor());
        RenderUtils.drawShadow(x - 190, y + 2, aboutWidth + 100, 110, 10, themeColors[3].getColor().getRGB());
        RenderUtils.drawShadow(x - 142, y + 7, aboutWidth + 5, 15, 12, themeColors[1].getColor().getRGB());
        Fonts.gilroyBold[20].drawString(matrixStack, "About our base", x - 140, y + 12, themeColors[2].getColor().getRGB());
        float themeX = x + w + 10;
        float themeY = y;
        float themeW = 200;

        if (themeMenuOpen) {
            themeAnimationProgress = RenderUtils.animate(1f, themeAnimationProgress, 0.1f);
        } else {
            themeAnimationProgress = RenderUtils.animate(0f, themeAnimationProgress, 0.1f);
        }

        float themeH = COLLAPSED_HEIGHT + (EXPANDED_HEIGHT - COLLAPSED_HEIGHT) * themeAnimationProgress;

        RenderUtils.drawShadow(themeX - 5, themeY - 5, themeW + 10, themeH + 10, 10, themeColors[1].getColor().getRGB());
        RenderUtils.drawRoundedRect(themeX, themeY, themeW, themeH, 5, themeColors[3].getColor());

        Fonts.gilroyBold[25].drawString(matrixStack, "Theme Settings", themeX + 10, themeY + 10, themeColors[2].getColor().getRGB());
        float presetY = themeY + 24;
        float presetSize = 8;
        float spacing = 15;
        float innerSize = 4;

        float currentX = themeX + 13;
        for (ThemePreset preset : THEME_PRESETS) {
            RenderUtils.drawShadow(currentX, presetY, presetSize, presetSize, 10, themeColors[1].getColor().getRGB());
            RenderUtils.drawRoundedRect(currentX, presetY, presetSize, presetSize, presetSize/2, preset.colors[0]);
            float innerX = currentX + (presetSize - innerSize)/2;
            float innerY = presetY + (presetSize - innerSize)/2;
            RenderUtils.drawRoundedRect(innerX, innerY, innerSize, innerSize, innerSize/2, preset.colors[3]);
            Fonts.gilroyBold[12].drawCenteredString(matrixStack, preset.name, currentX + presetSize/2, presetY + presetSize + 2, themeColors[2].getColor().getRGB());

            currentX += presetSize + spacing;
        }

        String toggleIcon = themeMenuOpen ? "▼" : "▶";
        float toggleX = themeX + themeW - 30;
        float toggleY = themeY + 10;
        RenderUtils.drawShadow(toggleX, toggleY, 20, 20, 5, themeColors[0].getColor().getRGB());
        RenderUtils.drawRoundedRect(toggleX, toggleY, 20, 20, 5, themeColors[0].getColor());
        Fonts.gilroyBold[20].drawString(matrixStack, toggleIcon, toggleX + 5, toggleY + 2, themeColors[2].getColor().getRGB());

        if (ishover(toggleX, toggleY, 20, 20, mouseX, mouseY) && leftButton) {
            themeMenuOpen = !themeMenuOpen;
            savedThemePanelState = themeMenuOpen;
        }

        if (themeAnimationProgress > 0) {
            float settingOffset = 50;
            int alpha = (int)(255 * themeAnimationProgress);

            for (ColorSetting color : themeColors) {
                float colorX = themeX + 10;
                float colorY = themeY + settingOffset;
                RenderUtils.drawShadow(colorX, colorY, 40, 15, 10, themeColors[0].getColor().getRGB());
                RenderUtils.drawRoundedRect(colorX, colorY, 40, 15, 2, color.getColor());
                Fonts.gilroy[14].drawString(matrixStack, color.getName(), colorX + 45, colorY + 4,
                        new Color(themeColors[2].getColor().getRed(),
                                themeColors[2].getColor().getGreen(),
                                themeColors[2].getColor().getBlue(),
                                alpha).getRGB());
                if (ishover(colorX, colorY, 40, 15, mouseX, mouseY) && leftButton) {
                    boolean wasOpen = color.isOpen();
                    for (ColorSetting otherColor : themeColors) {
                        otherColor.setOpen(false);
                    }
                    color.setOpen(!wasOpen);
                    lastOpenedColor = color.isOpen() ? color : null;
                    savedOpenColorName = color.isOpen() ? color.getName() : null;
                }
                
                if (color.isOpen()) {
                    float pickerX = colorX;
                    float pickerY = colorY + 20;
                    float gradientSize = 100;
                    RenderUtils.drawShadow(pickerX, pickerY, 140, 110, 10, new Color(38, 34, 34, alpha).getRGB());
                    RenderUtils.drawRoundedRect(pickerX, pickerY, 140, 110, 5, new Color(
                            themeColors[3].getColor().getRed(),
                            themeColors[3].getColor().getGreen(),
                            themeColors[3].getColor().getBlue(),
                            alpha));
                    float gradientX = pickerX + 5;
                    float gradientY = pickerY + 5;

                    for (int i = 0; i < 25; i++) {
                        for (int j = 0; j < 25; j++) {
                            float saturation = i / 25f;
                            float brightness = 1.0f - (j / 25f);
                            Color gradientColor = Color.getHSBColor(color.getHue(), saturation, brightness);
                            RenderUtils.drawRoundedRect(
                                    gradientX + (i * 4),
                                    gradientY + (j * 4),
                                    5, 5, 0,
                                    new Color(gradientColor.getRed(), gradientColor.getGreen(),
                                            gradientColor.getBlue(), alpha)
                            );
                        }
                    }

                    float hueX = gradientX + gradientSize + 10;
                    for (int i = 0; i < 40; i++) {
                        float hue = 1.0f - (i / 40f);
                        Color hueColor = Color.getHSBColor(hue, 1.0f, 1.0f);
                        RenderUtils.drawRoundedRect(
                                hueX,
                                gradientY + (i * 2.5f),
                                15,
                                3.5f,
                                0,
                                new Color(hueColor.getRed(), hueColor.getGreen(),
                                        hueColor.getBlue(), alpha)
                        );
                    }

                    if (color.isDragging() && mouseX >= gradientX && mouseX <= gradientX + gradientSize &&
                            mouseY >= gradientY && mouseY <= gradientY + gradientSize) {
                        float saturation = MathHelper.clamp((mouseX - gradientX) / gradientSize, 0, 1);
                        float brightness = MathHelper.clamp(1.0f - (mouseY - gradientY) / gradientSize, 0, 1);
                        color.setSaturation(saturation);
                        color.setBrightness(brightness);
                        color.updateColor();
                    }

                    if (color.isHueDragging() && mouseX >= hueX && mouseX <= hueX + 15 &&
                            mouseY >= gradientY && mouseY <= gradientY + gradientSize) {
                        float hue = MathHelper.clamp(1.0f - (mouseY - gradientY) / gradientSize, 0, 1);
                        color.setHue(hue);
                        color.updateColor();
                    }

                    float pickerPosX = gradientX + (color.getSaturation() * gradientSize);
                    float pickerPosY = gradientY + ((1.0f - color.getBrightness()) * gradientSize);
                    float huePickerY = gradientY + ((1.0f - color.getHue()) * gradientSize);

                    RenderUtils.drawRoundedRect(pickerPosX - 2, pickerPosY - 2, 4, 4, 1, new Color(255, 255, 255, alpha));
                    RenderUtils.drawRoundedRect(hueX - 2, huePickerY - 2, 19, 4, 1, new Color(255, 255, 255, alpha));

                    settingOffset += 145;
                } else {
                    settingOffset += 25;
                }
            }
        }

        if (leftButton) leftButton = false;
        if (rightButton) rightButton = false;
        if (middleButton) middleButton = false;

        Fonts.icons[20].drawString(matrixStack, "A", x - 180, y + 33, themeColors[2].getColor().getRGB());
        Fonts.gilroy[20].drawString(matrixStack, "Latest update: 27.11.2024", x - 165, y + 32.5f, themeColors[2].getColor().getRGB());
        Fonts.icons[20].drawString(matrixStack, "W", x - 180, y + 48, themeColors[2].getColor().getRGB());
        Fonts.gilroy[20].drawString(matrixStack, "Coders: r3z (rez) and exlenty", x - 165, y + 47.5f, themeColors[2].getColor().getRGB());
        Fonts.gilroyBold[15].drawString(matrixStack, "Основные", x + 5, y + 40, themeColors[2].getColor().getRGB());
        Fonts.gilroyBold[15].drawString(matrixStack, "Другие", x + 5, y + 40 + 22 + 22 + 22 + 22 + 14, themeColors[2].getColor().getRGB());
        for (Category cat : Category.values()) {
            RenderUtils.drawShadow(x + 10, y + 10 + offset, 85, 20, 10, cat == currentCat ? themeColors[0].getColor().getRGB() : themeColors[1].getColor().getRGB());
            RenderUtils.drawRoundedRect(x + 10, y + 10 + offset, 85, 20, 4, cat == currentCat ? themeColors[0].getColor()
                    : themeColors[1].getColor());
            Fonts.gilroyBold[15].drawString(matrixStack, cat.name(), x + 15, y + 15 + offset, -1);
            if (ishover(x + 5, y + 10 + offset, 85, 20, mouseX, mouseY) && leftButton) {
                currentCat = cat;
                lastCategory = cat;
                for (Module m : ModuleManager.getModules()) {
                    m.setting = false;
                }
            }
            if (cat.name().equals("UTIL")) {
                offset += 37;
            } else {
                offset += 22;
            }

        }

        int offsetmodule = 5;
        ScrollUtils.StartScissor(x, y + 1, w, h);
        for (Module m : ModuleManager.getModules()) {
            if (m.getCategory() == currentCat) {
                RenderUtils.drawShadow(x + 130, y + offsetmodule + 10 + m.getScrole().getAnim(), 135, 30, 10, m.isEnable() ? themeColors[0].getColor().getRGB() : themeColors[1].getColor().getRGB());
                RenderUtils.drawRoundedRect(x + 130, y + 10 + offsetmodule + m.getScrole().getAnim(), 135, 30, 4, m.isEnable() ? themeColors[0].getColor() : themeColors[1].getColor());
                String serverName = getServerName(m.server);
                Fonts.gilroy[20].drawString(matrixStack, m.isBinding() ? "Бинжу..." : m.getName() + " [" + serverName + "]",
                        x + 135, y + 15 + offsetmodule + m.getScrole().getAnim(), -1);
                Fonts.gilroy[16].drawString(matrixStack, m.getDescription(), x + 135, y + 28 + offsetmodule + m.getScrole().getAnim(), themeColors[2].getColor().getRGB());
                if (ishover(x + 120, y + 10 + offsetmodule + m.getScrole().getAnim(), 130, 30, mouseX, mouseY) && leftButton) {
                    m.enable = !m.isEnable();
                }
                if (ishover(x + 120, y + 10 + offsetmodule + m.getScrole().getAnim(), 130, 30, mouseX, mouseY) && rightButton) {
                    for (Module m2 : ModuleManager.getModules()) {
                        m2.setting = false;
                    }
                    m.setting = !m.isSetting();
                }
                if (ishover(x + 120, y + 10 + offsetmodule + m.getScrole().getAnim(), 130, 30, mouseX, mouseY) && middleButton) {
                    m.setBinding(true);
                }

                offsetmodule += 38;
            }
        }

        ScrollUtils.stopScissor();

        for (Module m : ModuleManager.getModules()) {
            if (m.isSetting()) {
                if (activeModule != m) {
                    activeModule = m;
                    animationProgress = 0;
                }

                settingsCount = m.getSettings().size();

                if (settingsCount > 0) {
                    animationProgress = RenderUtils.animate(1, animationProgress, 0.035f);
                    int alpha = (int) (255 * animationProgress);

                    Fonts.gilroyBold[15].drawString(matrixStack, "Настройки", x + 5, y + w - 130,
                            new Color(162, 162, 162, alpha).getRGB());

                    for (Setting e : m.getSettings()) {
                        float settingOffset = settOffset * animationProgress;

                        if (e instanceof CheckBoxSetting) {
                            RenderUtils.drawShadow(x + 10, y + w - 121 + settingOffset, 10, 10, 10,
                                    ((CheckBoxSetting) e).isValue() ? themeColors[0].getColor().getRGB() : themeColors[1].getColor().getRGB());
                            RenderUtils.drawRoundedRect(x + 10, y + w - 121 + settingOffset, 10, 10, 5,
                                    ((CheckBoxSetting) e).get() ? themeColors[0].getColor() : themeColors[1].getColor());
                            Fonts.gilroy[14].drawString(matrixStack, e.getName(), x + 25, y + w - 118 + settingOffset,
                                    new Color(255, 255, 255, alpha).getRGB());

                            if (ishover(x + 10, y + w - 121 + settingOffset, 10, 10, mouseX, mouseY) && leftButton) {
                                ((CheckBoxSetting) e).setValue(!((CheckBoxSetting) e).get());
                            }
                            settOffset += 15;

                        } else if (e instanceof ValueSetting) {
                            RenderUtils.drawShadow(x + 10, y + w - 121 + settingOffset, 10, 10, 10, themeColors[0].getColor().getRGB());
                            RenderUtils.drawRoundedRect(x + 10, y + w - 121 + settingOffset, 10, 10, 5, themeColors[0].getColor());
                            RenderUtils.drawShadow(x + 10 + 30, y + w - 121 + settingOffset, 10, 10, 10, themeColors[0].getColor().getRGB());
                            RenderUtils.drawRoundedRect(x + 10 + 30, y + w - 121 + settingOffset, 10, 10, 5, themeColors[0].getColor());

                            Fonts.gilroy[14].drawString(matrixStack, "+", x + 13, y + w - 117 + settingOffset,
                                    new Color(255, 255, 255, alpha).getRGB());
                            Fonts.gilroy[14].drawString(matrixStack, "-", x + 13 + 30, y + w - 117 + settingOffset,
                                    new Color(255, 255, 255, alpha).getRGB());
                            Fonts.gilroy[14].drawString(matrixStack, "" + ((ValueSetting) e).getValue(), x + 13 + 23 / 2 + 1,
                                    y + w - 117 + settingOffset, new Color(255, 255, 255, alpha).getRGB());
                            Fonts.gilroy[14].drawCenteredString(matrixStack, ((ValueSetting) e).getName(), x + 13 + 23 / 2 + 6,
                                    y + w - 117 + settingOffset + 10, new Color(255, 255, 255, alpha).getRGB());

                            if (ishover(x + 10, y + w - 121 + settingOffset, 10, 10, mouseX, mouseY) && leftButton) {
                                ((ValueSetting) e).setValue(((ValueSetting) e).getValue() + ((ValueSetting) e).getIncrement());
                            }
                            if (ishover(x + 10 + 30, y + w - 121 + settingOffset, 10, 10, mouseX, mouseY) && leftButton) {
                                ((ValueSetting) e).setValue(((ValueSetting) e).getValue() - ((ValueSetting) e).getIncrement());
                            }
                            settOffset += 25;

                        } else if (e instanceof ColorSetting) {
                            ColorSetting colorSetting = (ColorSetting) e;
                            float colorX = x + 10;
                            float colorY = y + w - 121 + settingOffset;

                            RenderUtils.drawShadow(colorX, colorY, 40, 15, 10, themeColors[0].getColor().getRGB());
                            RenderUtils.drawRoundedRect(colorX, colorY, 40, 15, 2, colorSetting.getColor());
                            Fonts.gilroy[14].drawString(matrixStack, colorSetting.getName(), colorX + 45, colorY + 4,
                                    new Color(255, 255, 255, alpha).getRGB());

                            if (colorSetting.isOpen()) {
                                float pickerX = colorX;
                                float pickerY = colorY + 20;

                                RenderUtils.drawShadow(pickerX, pickerY, 140, 110, 10, new Color(38, 34, 34).getRGB());
                                RenderUtils.drawRoundedRect(pickerX, pickerY, 140, 110, 5, themeColors[3].getColor());

                                float gradientX = pickerX + 5;
                                float gradientY = pickerY + 5;
                                float gradientSize = 100;

                                int segments = 25;
                                float segmentSize = gradientSize / segments;

                                for (int i = 0; i < segments; i++) {
                                    for (int j = 0; j < segments; j++) {
                                        float saturation = i / (float)segments;
                                        float brightness = 1.0f - (j / (float)segments);
                                        Color color = Color.getHSBColor(colorSetting.getHue(), saturation, brightness);
                                        RenderUtils.drawRoundedRect(
                                                gradientX + (i * segmentSize),
                                                gradientY + (j * segmentSize),
                                                segmentSize + 1,
                                                segmentSize + 1,
                                                0,
                                                color
                                        );
                                    }
                                }

                                float hueX = gradientX + gradientSize + 10;
                                int hueSegments = 40;
                                float hueSegmentSize = gradientSize / hueSegments;

                                for (int i = 0; i < hueSegments; i++) {
                                    float hue = 1.0f - (i / (float)hueSegments);
                                    Color hueColor = Color.getHSBColor(hue, 1.0f, 1.0f);
                                    RenderUtils.drawRoundedRect(
                                            hueX,
                                            gradientY + (i * hueSegmentSize),
                                            15,
                                            hueSegmentSize + 1,
                                            0,
                                            hueColor
                                    );
                                }
                                if (ishover(gradientX, gradientY, gradientSize, gradientSize, mouseX, mouseY)) {
                                    if (leftButton || colorSetting.isDragging()) {
                                        colorSetting.setDragging(true);
                                        float saturation = (mouseX - gradientX) / gradientSize;
                                        float brightness = 1.0f - (mouseY - gradientY) / gradientSize;
                                        colorSetting.setSaturation(Math.max(0, Math.min(1, saturation)));
                                        colorSetting.setBrightness(Math.max(0, Math.min(1, brightness)));
                                        colorSetting.updateColor();
                                    }
                                }

                                if (ishover(hueX, gradientY, 15, gradientSize, mouseX, mouseY)) {
                                    if (leftButton || colorSetting.isHueDragging()) {
                                        colorSetting.setHueDragging(true);
                                        float hue = 1.0f - (mouseY - gradientY) / gradientSize;
                                        colorSetting.setHue(Math.max(0, Math.min(1, hue)));
                                        colorSetting.updateColor();
                                    }
                                }

                                if (!leftButton) {
                                    colorSetting.setDragging(false);
                                    colorSetting.setHueDragging(false);
                                }

                                float pickerPosX = gradientX + (colorSetting.getSaturation() * gradientSize);
                                float pickerPosY = gradientY + ((1.0f - colorSetting.getBrightness()) * gradientSize);
                                RenderUtils.drawRoundedRect(pickerPosX - 2, pickerPosY - 2, 4, 4, 1, Color.WHITE);

                                float huePickerY = gradientY + ((1.0f - colorSetting.getHue()) * gradientSize);
                                RenderUtils.drawRoundedRect(hueX - 2, huePickerY - 2, 19, 4, 1, Color.WHITE);

                                if (ishover(colorX, colorY, 40, 15, mouseX, mouseY) && leftButton) {
                                    colorSetting.setOpen(true);
                                }

                                settOffset += colorSetting.isOpen() ? 165 : 25;
                            } else {
                                if (ishover(colorX, colorY, 40, 15, mouseX, mouseY) && leftButton) {
                                    colorSetting.setOpen(true);
                                }
                                settOffset += 25;
                            }
                        }
                    }
                }
            }
        }

        if (leftButton) leftButton = false;
        if (rightButton) rightButton = false;
        if (middleButton) middleButton = false;
    }

    private String getServerName(int server) {
        switch (server) {
            case 0:
                return "ALL";
            case 1:
                return "FT";
            case 2:
                return "HW";
            case 3:
                return "RW";
            default:
                return "NULL";
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (Module m : ModuleManager.getModules()) {
            if (m.isBinding()) {
                if (keyCode == 256) {
                    m.setBind(0);
                    m.setBinding(false);
                }

                m.setBind(keyCode);
                m.setBinding(false);
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    @Override
    public boolean mouseScrolled(double p_231043_1_, double p_231043_3_, double p_231043_5_) {

        ModuleManager.getModules().stream()
                .filter(guiCategory -> {
                    return guiCategory.getCategory() == currentCat;
                })
                .forEach(guiCategory -> {
                    guiCategory.Scrol(p_231043_5_);
                });

        return super.mouseScrolled(p_231043_1_, p_231043_3_, p_231043_5_);
    }
    public static boolean ishover(float xx, float yy, float width, float height, double mouseX, double mouseY) {
        if (mouseX > xx && mouseX < width + xx && mouseY > yy && mouseY < yy + height) {
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        x = mc.getMainWindow().getWidth()/4 - w/2;
        y = mc.getMainWindow().getHeight()/4 - h/2;
        float themeX = x + w + 10;
        float themeY = y;
        float toggleX = x + w + 210 - 30;
        float toggleY = y + 10;
        if (ishover(toggleX, toggleY, 20, 20, mouseX, mouseY) && button == 0) {
            themeMenuOpen = !themeMenuOpen;
            savedThemePanelState = themeMenuOpen;
            return true;
        }
        float presetY = themeY + 24;
        float presetSize = 8;
        float spacing = 15;
        float currentX = themeX + 13;
        for (ThemePreset preset : THEME_PRESETS) {
            if (ishover(currentX, presetY, presetSize, presetSize, mouseX, mouseY) && button == 0) {
                for (int i = 0; i < themeColors.length; i++) {
                    themeColors[i].setColor(preset.colors[i]);
                }
                return true;
            }
            currentX += presetSize + spacing;
        }
        if (themeMenuOpen && button == 0) {
            float settingY = themeY + 120;
            for (ColorSetting color : themeColors) {
                float colorX = themeX + 10;
                float colorY = settingY;
                if (ishover(colorX, colorY, 40, 15, mouseX, mouseY)) {
                    for (ColorSetting otherColor : themeColors) {
                        if (otherColor != color) {
                            otherColor.setOpen(false);
                        }
                    }
                    color.setOpen(!color.isOpen());
                    lastOpenedColor = color.isOpen() ? color : null;
                    savedOpenColorName = color.isOpen() ? color.getName() : null;
                    return true;
                }
                if (color.isOpen()) {
                    float pickerX = colorX;
                    float pickerY = colorY + 20;
                    float gradientX = pickerX + 5;
                    float gradientY = pickerY + 5;
                    float gradientSize = 100;
                    float hueX = gradientX + gradientSize + 10;

                    if (ishover(gradientX, gradientY, gradientSize, gradientSize, mouseX, mouseY)) {
                        color.setDragging(true);
                        return true;
                    }

                    if (ishover(hueX, gradientY, 15, gradientSize, mouseX, mouseY)) {
                        color.setHueDragging(true);
                        return true;
                    }

                    settingY += 140;
                }
                settingY += 25;
            }
        }

        if (button == 0) {
            leftButton = true;
            int offsetmodule = 5;
            for (Module m : ModuleManager.getModules()) {
                if (m.getCategory() == currentCat) {
                    if (ishover(x + 130, y + offsetmodule + 10 + m.getScrole().getAnim(), 135, 30, mouseX, mouseY)) {
                        m.enable = !m.isEnable();
                        return true;
                    }
                    offsetmodule += 38;
                }
            }
            
            offset = 40;
            for (Category cat : Category.values()) {
                if (ishover(x + 10, y + 10 + offset, 85, 20, mouseX, mouseY)) {
                    currentCat = cat;
                    lastCategory = cat;
                    for (Module m : ModuleManager.getModules()) {
                        m.setting = false;
                    }
                    return true;
                }
                if (cat.name().equals("UTIL")) {
                    offset += 37;
                } else {
                    offset += 22;
                }
            }
            for (Module m : ModuleManager.getModules()) {
                if (m.isSetting()) {
                    settOffset = 0;
                    for (Setting e : m.getSettings()) {
                        if (e instanceof ColorSetting) {
                            ColorSetting colorSetting = (ColorSetting) e;
                            float colorX = x + 10;
                            float colorY = y + w - 121 + settOffset;

                            if (colorSetting.isOpen()) {
                                float pickerX = colorX;
                                float pickerY = colorY + 20;
                                float gradientX = pickerX + 5;
                                float gradientY = pickerY + 5;
                                float gradientSize = 100;
                                float hueX = gradientX + gradientSize + 10;

                                if (ishover(gradientX, gradientY, gradientSize, gradientSize, mouseX, mouseY)) {
                                    colorSetting.setDragging(true);
                                    return true;
                                }

                                if (ishover(hueX, gradientY, 15, gradientSize, mouseX, mouseY)) {
                                    colorSetting.setHueDragging(true);
                                    return true;
                                }

                                if (ishover(colorX, colorY, 40, 15, mouseX, mouseY)) {
                                    colorSetting.setOpen(false);
                                    return true;
                                }
                                settOffset += 140;
                            } else {
                                if (ishover(colorX, colorY, 40, 15, mouseX, mouseY)) {
                                    colorSetting.setOpen(true);
                                    return true;
                                }
                                settOffset += 25;
                            }
                        }
                        if (e instanceof CheckBoxSetting) {
                            if (ishover(x + 10, y + w - 121 + settOffset, 10, 10, mouseX, mouseY)) {
                                ((CheckBoxSetting) e).setValue(!((CheckBoxSetting) e).get());
                                return true;
                            }
                            settOffset += 15;
                        } else if (e instanceof ValueSetting) {
                            if (ishover(x + 10, y + w - 121 + settOffset, 10, 10, mouseX, mouseY)) {
                                ((ValueSetting) e).setValue(((ValueSetting) e).getValue() + ((ValueSetting) e).getIncrement());
                                return true;
                            }
                            if (ishover(x + 10 + 30, y + w - 121 + settOffset, 10, 10, mouseX, mouseY)) {
                                ((ValueSetting) e).setValue(((ValueSetting) e).getValue() - ((ValueSetting) e).getIncrement());
                                return true;
                            }
                            settOffset += 25;
                        }
                    }
                }
            }

        } else if (button == 1) {
            rightButton = true;
            int offsetmodule = 5;
            for (Module m : ModuleManager.getModules()) {
                if (m.getCategory() == currentCat) {
                    if (ishover(x + 130, y + offsetmodule + 10 + m.getScrole().getAnim(), 135, 30, mouseX, mouseY)) {
                        for (Module m2 : ModuleManager.getModules()) {
                            m2.setting = false;
                        }
                        m.setting = !m.isSetting();
                        return true;
                    }
                    offsetmodule += 38;
                }
            }
        } else if (button == 2) {
            middleButton = true;
            int offsetmodule = 5;
            for (Module m : ModuleManager.getModules()) {
                if (m.getCategory() == currentCat) {
                    if (ishover(x + 130, y + offsetmodule + 10 + m.getScrole().getAnim(), 135, 30, mouseX, mouseY)) {
                        m.setBinding(true);
                        return true;
                    }
                    offsetmodule += 38;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (ColorSetting color : themeColors) {
            color.setDragging(false);
            color.setHueDragging(false);
        }
        for (Module m : ModuleManager.getModules()) {
            if (m.isSetting()) {
                for (Setting e : m.getSettings()) {
                    if (e instanceof ColorSetting) {
                        ((ColorSetting) e).setDragging(false);
                        ((ColorSetting) e).setHueDragging(false);
                    }
                }
            }
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }



    private void saveColors() {
        for (int i = 0; i < themeColors.length; i++) {
            savedColors[i] = themeColors[i].getColor();
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        for (ColorSetting color : themeColors) {
            if (color.isOpen()) {
                float themeX = x + w + 10;
                float themeY = y + 50;
                float colorX = themeX + 10;
                float pickerX = colorX;
                float pickerY = themeY + 20;
                float gradientX = pickerX + 5;
                float gradientY = pickerY + 5;
                float gradientSize = 100;
                float hueX = gradientX + gradientSize + 10;

                if (color.isDragging()) {
                    float saturation = (float)(mouseX - gradientX) / gradientSize;
                    float brightness = 1.0f - (float)(mouseY - gradientY) / gradientSize;
                    saturation = Math.max(0, Math.min(1, saturation));
                    brightness = Math.max(0, Math.min(1, brightness));
                    color.setSaturation(saturation);
                    color.setBrightness(brightness);
                    color.updateColor();
                    return true;
                }

                if (color.isHueDragging()) {
                    float hue = 1.0f - (float)(mouseY - gradientY) / gradientSize;
                    hue = Math.max(0, Math.min(1, hue));
                    color.setHue(hue);
                    color.updateColor();
                    return true;
                }
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }
    
    private static class ThemePreset {
        String name;
        Color[] colors;

        ThemePreset(String name, Color[] colors) {
            this.name = name;
            this.colors = colors;
        }
    }
}