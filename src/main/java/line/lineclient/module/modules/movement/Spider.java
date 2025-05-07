package line.lineclient.module.modules.movement;

import line.lineclient.event.Event;
import line.lineclient.event.events.EventRender;
import line.lineclient.event.events.EventTick;
import line.lineclient.module.Category;
import line.lineclient.module.Module;
import line.lineclient.ui.clickgui.setting.settings.ModulePanelManager;
import line.lineclient.ui.clickgui.ClickGUI;
import line.lineclient.utils.render.RenderUtils;
import line.lineclient.utils.fonts.Fonts;
import line.lineclient.ui.clickgui.setting.settings.ValueSetting;

import java.awt.Color;

import static line.lineclient.utils.Wrapper.mc;

public class Spider extends Module {
    private final ValueSetting climbSpeed = new ValueSetting("Speed", 0.2f, 0.1f, 1.0f, 0.1f);

    public Spider() {
        super("Spider", Category.MOVEMENT, "Can wall climbing", -1, 1);
        addSetting(climbSpeed);
    }

    @Override
    public void onEnable() {
        ModulePanelManager.addModule(getName());
    }

    @Override
    public void onDisable() {
        ModulePanelManager.removeModule(getName());
    }

    @Override
    public void event(Event e) {
        if (e instanceof EventTick) {
            handleClimbing();
        } else if (e instanceof EventRender) {
            renderModule((EventRender) e);
        }
    }

    private void handleClimbing() {
        if (mc.player != null && mc.player.collidedHorizontally) {
            // Перевіряємо, чи гравець не в воді і не в лаві
            if (!mc.player.isInWater() && !mc.player.isInLava()) {
                // Перевіряємо, чи гравець не присів
                if (!mc.player.isCrouching()) {
                    // Встановлюємо вертикальну швидкість
                    mc.player.setVelocity(
                        mc.player.getMotion().x,
                        climbSpeed.getValue(),
                        mc.player.getMotion().z
                    );

                    // Скидаємо падіння
                    mc.player.fallDistance = 0f;
                }
            }
        }
    }

    private void renderModule(EventRender e) {
        if (mc == null || mc.getMainWindow() == null) return;

        int yPos = ModulePanelManager.getModulePosition(getName());
        int screenWidth = mc.getMainWindow().getScaledWidth();

        // Основний колір фону
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
        String displayText = getName() + " " + 
                           (isEnable() ? "ON" : "OFF") + 
                           String.format(" %.1f", climbSpeed.getValue());

        Fonts.gilroy[15].drawString(
            e.getMatrixStack(),
            displayText,
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