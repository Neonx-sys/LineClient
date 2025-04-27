package line.lineclient.ui.clickgui.setting.settings;

import com.mojang.blaze3d.matrix.MatrixStack;
import line.lineclient.ui.clickgui.setting.Setting;
import line.lineclient.utils.fonts.Fonts;
import line.lineclient.utils.render.RenderUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ColorSetting extends Setting {
    private Color color;
    private float hue;
    private float saturation;
    private float brightness;
    private float alpha;
    private boolean dragging;
    private boolean hueDragging;
    private boolean colorSelectorDragging;
    private boolean hueSelectorDragging;
    private boolean alphaSelectorDragging;
    private boolean open;
    private float colorPickerX;
    private float colorPickerY;
    public float colorPickerWidth = 100;
    public float colorPickerHeight = 100;
    public boolean isDragging() { return dragging; }
    public void setDragging(boolean dragging) { this.dragging = dragging; }
    public boolean isHueDragging() { return hueDragging; }
    public void setHueDragging(boolean hueDragging) { this.hueDragging = hueDragging; }

    public ColorSetting(String name, Color color) {
        this.setName(name);
        this.color = color;
        this.setVisibility(() -> true);

        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        this.hue = hsb[0];
        this.saturation = hsb[1];
        this.brightness = hsb[2];
        this.alpha = color.getAlpha() / 255f;
    }

    public void drawColorPicker(MatrixStack matrixStack, float x, float y, float settingOffset, float animationProgress) {
        if (!open) {
            RenderUtils.drawShadow(x + 10, y + settingOffset, 40, 15, 10, new Color(0x696995).getRGB());
            RenderUtils.drawRoundedRect(x + 10, y + settingOffset, 40, 15, 2, color);
            Fonts.gilroy[14].drawString(matrixStack, getName(), x + 55, y + settingOffset + 4, new Color(255, 255, 255, (int)(255 * animationProgress)).getRGB());
            return;
        }
        float pickerY = y + settingOffset + 20;
        RenderUtils.drawShadow(x + 10, pickerY, colorPickerWidth + 40, colorPickerHeight + 30, 10, new Color(38, 34, 34).getRGB());
        RenderUtils.drawRoundedRect(x + 10, pickerY, colorPickerWidth + 40, colorPickerHeight + 30, 5, new Color(30, 25, 25));
        drawColorSquare(x + 15, pickerY + 5, colorPickerWidth, colorPickerHeight);
        float hueSliderX = x + 15 + colorPickerWidth + 10;
        drawHueSlider(hueSliderX, pickerY + 5, 15, colorPickerHeight);

        float alphaSliderX = hueSliderX + 20;
        drawAlphaSlider(alphaSliderX, pickerY + 5, 15, colorPickerHeight);
        drawColorSelector(x + 15 + saturation * colorPickerWidth,
                pickerY + 5 + (1 - brightness) * colorPickerHeight);
        drawHueSelector(hueSliderX, pickerY + 5 + (1 - hue) * colorPickerHeight);
        drawAlphaSelector(alphaSliderX, pickerY + 5 + (1 - alpha) * colorPickerHeight);
        RenderUtils.drawRoundedRect(x + 15, pickerY + colorPickerHeight + 10, 30, 15, 2, color);
        String hexColor = String.format("#%02x%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        Fonts.gilroy[14].drawString(matrixStack, hexColor, x + 50, pickerY + colorPickerHeight + 13, -1);
    }


    public void drawColorSquare(float x, float y, float width, float height) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);

        GL11.glBegin(GL11.GL_QUADS);
        Color baseColor = Color.getHSBColor(hue, 1.0f, 1.0f);

        GL11.glColor4f(1, 1, 1, 1);
        GL11.glVertex2f(x, y);

        GL11.glColor4f(baseColor.getRed()/255f, baseColor.getGreen()/255f, baseColor.getBlue()/255f, 1);
        GL11.glVertex2f(x + width, y);

        GL11.glColor4f(0, 0, 0, 1);
        GL11.glVertex2f(x + width, y + height);

        GL11.glColor4f(0, 0, 0, 1);
        GL11.glVertex2f(x, y + height);

        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public void drawHueSlider(float x, float y, float width, float height) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);

        GL11.glBegin(GL11.GL_QUADS);

        for (int i = 0; i < 6; i++) {
            float pos = i / 6.0f;
            float pos2 = (i + 1) / 6.0f;
            Color color = Color.getHSBColor(pos, 1.0f, 1.0f);
            Color color2 = Color.getHSBColor(pos2, 1.0f, 1.0f);

            GL11.glColor4f(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, 1);
            GL11.glVertex2f(x, y + height * pos);
            GL11.glVertex2f(x + width, y + height * pos);

            GL11.glColor4f(color2.getRed()/255f, color2.getGreen()/255f, color2.getBlue()/255f, 1);
            GL11.glVertex2f(x + width, y + height * pos2);
            GL11.glVertex2f(x, y + height * pos2);
        }

        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public void drawAlphaSlider(float x, float y, float width, float height) {
        drawCheckeredBackground(x, y, width, height);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);

        GL11.glBegin(GL11.GL_QUADS);

        Color color1 = new Color(color.getRed(), color.getGreen(), color.getBlue(), 255);
        Color color2 = new Color(color.getRed(), color.getGreen(), color.getBlue(), 0);

        GL11.glColor4f(color1.getRed()/255f, color1.getGreen()/255f, color1.getBlue()/255f, 1);
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x + width, y);

        GL11.glColor4f(color2.getRed()/255f, color2.getGreen()/255f, color2.getBlue()/255f, 0);
        GL11.glVertex2f(x + width, y + height);
        GL11.glVertex2f(x, y + height);

        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    private void drawCheckeredBackground(float x, float y, float width, float height) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        GL11.glBegin(GL11.GL_QUADS);

        float squareSize = 5;
        boolean white = true;

        for (float i = 0; i < width; i += squareSize) {
            for (float j = 0; j < height; j += squareSize) {
                if (white) {
                    GL11.glColor4f(1, 1, 1, 1);
                } else {
                    GL11.glColor4f(0.8f, 0.8f, 0.8f, 1);
                }

                GL11.glVertex2f(x + i, y + j);
                GL11.glVertex2f(x + i + squareSize, y + j);
                GL11.glVertex2f(x + i + squareSize, y + j + squareSize);
                GL11.glVertex2f(x + i, y + j + squareSize);

                white = !white;
            }
            white = !white;
        }

        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public void drawColorSelector(float x, float y) {
        float size = 4;
        RenderUtils.drawRoundedRect(x - size, y - size, size * 2, size * 2, 2, Color.WHITE);
        RenderUtils.drawRoundedRect(x - size + 1, y - size + 1, (size * 2) - 2, (size * 2) - 2, 2, Color.BLACK);
    }

    public void drawHueSelector(float x, float y) {
        float width = 15;
        float height = 3;
        RenderUtils.drawRoundedRect(x, y - height, width, height * 2, 2, Color.WHITE);
        RenderUtils.drawRoundedRect(x + 1, y - height + 1, width - 2, (height * 2) - 2, 2, Color.BLACK);
    }

    public void drawAlphaSelector(float x, float y) {
        float width = 15;
        float height = 3;
        RenderUtils.drawRoundedRect(x, y - height, width, height * 2, 2, Color.WHITE);
        RenderUtils.drawRoundedRect(x + 1, y - height + 1, width - 2, (height * 2) - 2, 2, Color.BLACK);
    }

    public void handleMouseClick(int mouseX, int mouseY, float x, float y, float settingOffset) {
        if (ishover(x + 10, y + settingOffset, 40, 15, mouseX, mouseY)) {
            open = !open;
            return;
        }

        if (!open) return;

        float pickerY = y + settingOffset + 20;
        if (ishover(x + 15, pickerY + 5, colorPickerWidth, colorPickerHeight, mouseX, mouseY)) {
            colorSelectorDragging = true;
            updateColorFromMouse(mouseX, mouseY);
        }
        float hueSliderX = x + 15 + colorPickerWidth + 10;
        if (ishover(hueSliderX, pickerY + 5, 15, colorPickerHeight, mouseX, mouseY)) {
            hueSelectorDragging = true;
            updateHueFromMouse(mouseY);
        }
        float alphaSliderX = hueSliderX + 20;
        if (ishover(alphaSliderX, pickerY + 5, 15, colorPickerHeight, mouseX, mouseY)) {
            alphaSelectorDragging = true;
            updateAlphaFromMouse(mouseY);
        }
    }

    private boolean ishover(float x, float y, float width, float height, int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    private boolean isWithinBounds(int mouseX, int mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public void handleMouseRelease() {
        colorSelectorDragging = false;
        hueSelectorDragging = false;
        alphaSelectorDragging = false;
    }

    public void handleMouseDrag(int mouseX, int mouseY) {
        if (colorSelectorDragging) {
            updateColorFromMouse(mouseX, mouseY);
        }
        if (hueSelectorDragging) {
            updateHueFromMouse(mouseY);
        }
        if (alphaSelectorDragging) {
            updateAlphaFromMouse(mouseY);
        }
    }
    private void updateColorFromMouse(int mouseX, int mouseY) {
        this.saturation = clamp((mouseX - colorPickerX) / colorPickerWidth, 0, 1);
        this.brightness = 1 - clamp((mouseY - colorPickerY) / colorPickerHeight, 0, 1);
        updateColor();
    }

    private void updateHueFromMouse(int mouseY) {
        this.hue = 1 - clamp((mouseY - colorPickerY) / colorPickerHeight, 0, 1);
        updateColor();
    }

    private void updateAlphaFromMouse(int mouseY) {
        this.alpha = 1 - clamp((mouseY - colorPickerY) / colorPickerHeight, 0, 1);
        updateColor();
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public void updateColor() {
        Color hsbColor = Color.getHSBColor(hue, saturation, brightness);
        this.color = new Color(hsbColor.getRed(), hsbColor.getGreen(), hsbColor.getBlue(), (int)(alpha * 255));
    }


    public float getHue() {
        return hue;
    }

    public void setHue(float hue) {
        this.hue = hue;
        updateColor();
    }

    public float getSaturation() {
        return saturation;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
        updateColor();
    }

    public float getBrightness() {
        return brightness;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
        updateColor();
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        updateColor();
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        this.hue = hsb[0];
        this.saturation = hsb[1];
        this.brightness = hsb[2];
        this.alpha = color.getAlpha() / 255f;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isColorSelectorDragging() {
        return colorSelectorDragging;
    }

    public void setColorSelectorDragging(boolean colorSelectorDragging) {
        this.colorSelectorDragging = colorSelectorDragging;
    }

    public boolean isHueSelectorDragging() {
        return hueSelectorDragging;
    }

    public void setHueSelectorDragging(boolean hueSelectorDragging) {
        this.hueSelectorDragging = hueSelectorDragging;
    }


















    //theme





}