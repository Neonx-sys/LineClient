package line.lineclient.module.modules.util;

import line.lineclient.event.Event;
import line.lineclient.event.events.EventRender;
import line.lineclient.event.events.EventTick;
import line.lineclient.module.Category;
import line.lineclient.module.Module;
import line.lineclient.ui.clickgui.ClickGUI;
import line.lineclient.ui.clickgui.setting.settings.ModulePanelManager;
import line.lineclient.utils.render.RenderUtils;
import line.lineclient.utils.fonts.Fonts;

import java.awt.Color;
import java.lang.reflect.Field;

import static line.lineclient.utils.Wrapper.mc;

public class FastBreak extends Module {
    public FastBreak() {
        super("FastBreak", Category.UTIL, "Breaks blocks faster", -1, 1);
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
            resetBlockHitDelay();
        } else if (e instanceof EventRender) {
            renderModule((EventRender) e);
        }
    }

    private void resetBlockHitDelay() {
        try {
            Field delayField = mc.playerController.getClass().getDeclaredField("blockHitDelay");
            delayField.setAccessible(true);
            delayField.setInt(mc.playerController, 0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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