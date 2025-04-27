package line.lineclient.module.modules.render;

import line.lineclient.event.Event;
import line.lineclient.event.events.EventRender;
import line.lineclient.module.Category;
import line.lineclient.module.Module;
import line.lineclient.ui.clickgui.ClickGUI;
import line.lineclient.ui.clickgui.setting.settings.CheckBoxSetting;
import line.lineclient.utils.fonts.Fonts;
import line.lineclient.utils.render.RenderUtils;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

import static line.lineclient.Client.userData;
import static line.lineclient.utils.Wrapper.mc;

public class HUD extends Module {
    private float animation = 0f;
    private float lastFPS = 0f;
    public float watermarkX = 5f;
    public float watermarkY;
    public float bpsX = 5f;
    public float bpsY;
    private boolean isDraggingWatermark;
    private boolean isDraggingBps;
    private float dragX, dragY;
    private float totalWidth;
    private float watermarkAlpha = 1.0f;
    private float bpsAlpha = 1.0f;

    public CheckBoxSetting watermark = new CheckBoxSetting("Watermark", true);
    public CheckBoxSetting bps = new CheckBoxSetting("BPS", true);

    public HUD() {
        super("HUD", Category.RENDER, "Отображение худа", -1, 0);
        addSetting(watermark, bps);
        watermarkX = 5;
        watermarkY = 5;
        bpsX = 5;
        bpsY = 25;
    }

    @Override
    public void event(Event e) {
        if (e instanceof EventRender) {
            if (mc.currentScreen != null) {
                double mouseX = mc.mouseHelper.getMouseX() * mc.getMainWindow().getScaledWidth() / mc.getMainWindow().getWidth();
                double mouseY = mc.mouseHelper.getMouseY() * mc.getMainWindow().getScaledHeight() / mc.getMainWindow().getHeight();
                handleDrag((int)mouseX, (int)mouseY);
            }

            if (watermark.get()) {
                String login = userData.getLogin();
                String uid = String.valueOf("UID: " + userData.getUid());
                lastFPS = RenderUtils.animate(mc.debugFPS, lastFPS, 0.1f);
                String fps = String.valueOf(Math.round(lastFPS) + "fps");
                animation = RenderUtils.animate(1f, animation, 0.1f);
                float nobyWidth = Fonts.gilroyBold[20].getWidth("LineClient ");
                float separatorWidth = Fonts.gilroy[18].getWidth("| ");
                float iconWidth = Fonts.icons[18].getWidth("W");
                float icon2Width = Fonts.icons[18].getWidth("F");
                float icon3Width = Fonts.icons[18].getWidth("A");
                float loginWidth = Fonts.gilroy[15].getWidth(login);
                float uidWidth = Fonts.gilroy[15].getWidth(uid);
                float fpsWidth = Fonts.gilroy[15].getWidth(fps);
                totalWidth = nobyWidth + separatorWidth + iconWidth + loginWidth + separatorWidth + icon2Width + uidWidth + separatorWidth + icon3Width + fpsWidth + 20;
                float animatedWidth = totalWidth * animation;
                RenderUtils.drawRoundedRect(watermarkX, watermarkY, animatedWidth, 15 * animation, 5, new Color(ClickGUI.getThemeColors()[3].getColor().getRed(), ClickGUI.getThemeColors()[3].getColor().getGreen(), ClickGUI.getThemeColors()[3].getColor().getBlue(), (int)(watermarkAlpha * 255)));
                RenderUtils.drawShadow(watermarkX - 2, watermarkY - 3, animatedWidth + 5, 20 * animation, 12, new Color(ClickGUI.getThemeColors()[1].getColor().getRed(), ClickGUI.getThemeColors()[1].getColor().getGreen(), ClickGUI.getThemeColors()[1].getColor().getBlue()).getRGB());
                float currentX = watermarkX + 5;

                Fonts.gilroyBold[20].drawString(((EventRender) e).getMatrixStack(), "LineClient ", currentX, watermarkY + 4.5f, new Color(ClickGUI.getThemeColors()[4].getColor().getRed(), ClickGUI.getThemeColors()[4].getColor().getGreen(), ClickGUI.getThemeColors()[4].getColor().getBlue(), (int)(watermarkAlpha * 255)).getRGB());
                currentX += nobyWidth;

                Fonts.gilroy[18].drawString(((EventRender) e).getMatrixStack(), "| ", currentX, watermarkY + 5, new Color(ClickGUI.getThemeColors()[2].getColor().getRed(), ClickGUI.getThemeColors()[2].getColor().getGreen(), ClickGUI.getThemeColors()[2].getColor().getBlue(), (int)(watermarkAlpha * 255)).getRGB());
                currentX += separatorWidth;

                Fonts.icons[18].drawString(((EventRender) e).getMatrixStack(), "W", currentX, watermarkY + 6, new Color(ClickGUI.getThemeColors()[4].getColor().getRed(), ClickGUI.getThemeColors()[4].getColor().getGreen(), ClickGUI.getThemeColors()[4].getColor().getBlue(), (int)(watermarkAlpha * 255)).getRGB());
                currentX += iconWidth + 2;

                Fonts.gilroy[15].drawString(((EventRender) e).getMatrixStack(), login, currentX, watermarkY + 6, new Color(ClickGUI.getThemeColors()[2].getColor().getRed(), ClickGUI.getThemeColors()[2].getColor().getGreen(), ClickGUI.getThemeColors()[2].getColor().getBlue(), (int)(watermarkAlpha * 255)).getRGB());
                currentX += loginWidth + 2;

                Fonts.gilroy[18].drawString(((EventRender) e).getMatrixStack(), "| ", currentX, watermarkY + 5, new Color(ClickGUI.getThemeColors()[2].getColor().getRed(), ClickGUI.getThemeColors()[2].getColor().getGreen(), ClickGUI.getThemeColors()[2].getColor().getBlue(), (int)(watermarkAlpha * 255)).getRGB());
                currentX += separatorWidth;

                Fonts.icons[18].drawString(((EventRender) e).getMatrixStack(), "F", currentX, watermarkY + 6, new Color(ClickGUI.getThemeColors()[4].getColor().getRed(), ClickGUI.getThemeColors()[4].getColor().getGreen(), ClickGUI.getThemeColors()[4].getColor().getBlue(), (int)(watermarkAlpha * 255)).getRGB());
                currentX += icon2Width + 2;

                Fonts.gilroy[15].drawString(((EventRender) e).getMatrixStack(), uid, currentX, watermarkY + 6, new Color(ClickGUI.getThemeColors()[2].getColor().getRed(), ClickGUI.getThemeColors()[2].getColor().getGreen(), ClickGUI.getThemeColors()[2].getColor().getBlue(), (int)(watermarkAlpha * 255)).getRGB());
                currentX += uidWidth + 2;

                Fonts.gilroy[18].drawString(((EventRender) e).getMatrixStack(), "| ", currentX, watermarkY + 5, new Color(ClickGUI.getThemeColors()[2].getColor().getRed(), ClickGUI.getThemeColors()[2].getColor().getGreen(), ClickGUI.getThemeColors()[2].getColor().getBlue(), (int)(watermarkAlpha * 255)).getRGB());
                currentX += separatorWidth;

                Fonts.icons[18].drawString(((EventRender) e).getMatrixStack(), "A", currentX, watermarkY + 6, new Color(ClickGUI.getThemeColors()[4].getColor().getRed(), ClickGUI.getThemeColors()[4].getColor().getGreen(), ClickGUI.getThemeColors()[4].getColor().getBlue(), (int)(watermarkAlpha * 255)).getRGB());
                currentX += icon3Width + 2;

                Fonts.gilroy[15].drawString(((EventRender) e).getMatrixStack(), fps, currentX, watermarkY + 6, new Color(ClickGUI.getThemeColors()[2].getColor().getRed(), ClickGUI.getThemeColors()[2].getColor().getGreen(), ClickGUI.getThemeColors()[2].getColor().getBlue(), (int)(watermarkAlpha * 255)).getRGB());
            }

            if (bps.get()) {
                float textWidth = Fonts.icons[20].getWidth("#") + Fonts.gilroy[20].getWidth("BPS: " + getPlayerSpeed());
                Color bpsColor = new Color(ClickGUI.getThemeColors()[3].getColor().getRed(), ClickGUI.getThemeColors()[3].getColor().getGreen(), ClickGUI.getThemeColors()[3].getColor().getBlue(), (int)(bpsAlpha * 255));
                Color shadowColor = new Color(ClickGUI.getThemeColors()[1].getColor().getRed(), ClickGUI.getThemeColors()[1].getColor().getGreen(), ClickGUI.getThemeColors()[1].getColor().getBlue(), (int)(bpsAlpha * 255));

                RenderUtils.drawRoundedRect(bpsX, bpsY, textWidth + 10, 15, 5, bpsColor);

                RenderUtils.drawShadow(bpsX, bpsY, textWidth + 10, 15, 6, shadowColor.getRGB());

                Fonts.gilroy[20].drawString(((EventRender) e).getMatrixStack(), "BPS: " + getPlayerSpeed(), bpsX + 15, bpsY + 4, new Color(ClickGUI.getThemeColors()[4].getColor().getRed(), ClickGUI.getThemeColors()[4].getColor().getGreen(), ClickGUI.getThemeColors()[4].getColor().getBlue()).getRGB());

                Fonts.icons[17].drawString(((EventRender) e).getMatrixStack(), "#", bpsX + 3, bpsY + 5.5f, new Color(ClickGUI.getThemeColors()[4].getColor().getRed(), ClickGUI.getThemeColors()[4].getColor().getGreen(), ClickGUI.getThemeColors()[4].getColor().getBlue()).getRGB());
            }
        }
    }

    private String getPlayerSpeed() {
        double deltaX = mc.player.getPosX() - mc.player.prevPosX;
        double deltaZ = mc.player.getPosZ() - mc.player.prevPosZ;
        double speed = Math.hypot(deltaX, deltaZ) * 20;

        double deltaY = mc.player.getPosY() - mc.player.prevPosY;
        double verticalSpeed = Math.abs(deltaY) * 20;

        return String.format("%.2f", speed);
    }

    private void handleDrag(int mouseX, int mouseY) {
        if (watermark.get()) {
            if (isHovered(watermarkX, watermarkY, totalWidth, 15, mouseX, mouseY)) {
                if (GLFW.glfwGetMouseButton(mc.getMainWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS) {
                    if (!isDraggingWatermark) {
                        dragX = mouseX - watermarkX;
                        dragY = mouseY - watermarkY;
                        isDraggingWatermark = true;
                    }
                }
            }

            if (isDraggingWatermark) {
                watermarkAlpha = lerp(watermarkAlpha, 0.5f, 0.1f);
                float targetX = mouseX - dragX;
                float targetY = mouseY - dragY;
                watermarkX = lerp(watermarkX, targetX, 0.2f);
                watermarkY = lerp(watermarkY, targetY, 0.2f);
            } else {
                watermarkAlpha = lerp(watermarkAlpha, 1.0f, 0.1f);
            }
        }

        if (bps.get()) {
            float bpsWidth = Fonts.icons[20].getWidth("#") + Fonts.gilroy[20].getWidth("BPS: " + getPlayerSpeed()) + 10;
            if (isHovered(bpsX, bpsY, bpsWidth, 15, mouseX, mouseY)) {
                if (GLFW.glfwGetMouseButton(mc.getMainWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS) {
                    if (!isDraggingBps) {
                        dragX = mouseX - bpsX;
                        dragY = mouseY - bpsY;
                        isDraggingBps = true;
                    }
                }
            }

            if (isDraggingBps) {
                bpsAlpha = lerp(bpsAlpha, 0.5f, 0.1f);
                float targetX = mouseX - dragX;
                float targetY = mouseY - dragY;
                bpsX = lerp(bpsX, targetX, 0.2f);
                bpsY = lerp(bpsY, targetY, 0.2f);
            } else {
                bpsAlpha = lerp(bpsAlpha, 1.0f, 0.1f);
            }
        }

        if (GLFW.glfwGetMouseButton(mc.getMainWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) != GLFW.GLFW_PRESS) {
            isDraggingWatermark = false;
            isDraggingBps = false;
        }
    }

    private float lerp(float start, float end, float alpha) {
        return start + alpha * (end - start);
    }

    private boolean isHovered(float x, float y, float width, float height, int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}
