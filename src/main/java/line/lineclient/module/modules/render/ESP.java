package line.lineclient.module.modules.render;

import line.lineclient.event.Event;
import line.lineclient.event.events.EventRender;
import line.lineclient.module.Category;
import line.lineclient.module.Module;
import line.lineclient.ui.clickgui.ClickGUI;
import line.lineclient.ui.clickgui.setting.settings.ModulePanelManager;
import line.lineclient.utils.fonts.Fonts;
import line.lineclient.utils.render.RenderUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;

import java.awt.Color;

import static line.lineclient.utils.Wrapper.mc;

public class ESP extends Module {
    
    public ESP() {
        super("ESP", Category.RENDER, "Показує здоров'я гравців", -1, 2);
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
        if (e instanceof EventRender) {
            EventRender er = (EventRender) e;
            
            if (mc.world == null || mc.player == null) return;

            // Малюємо здоров'я для кожного гравця
            renderPlayerHealth(er);
            
            // Малюємо інтерфейс модуля
            renderModule(er);
        }
    }

    private void renderPlayerHealth(EventRender er) {
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || player.isInvisible()) continue;

            // Отримуємо позицію гравця
            double x = player.lastTickPosX + (player.getPosX() - player.lastTickPosX) * er.getPartialTicks();
            double y = player.lastTickPosY + (player.getPosY() - player.lastTickPosY) * er.getPartialTicks();
            double z = player.lastTickPosZ + (player.getPosZ() - player.lastTickPosZ) * er.getPartialTicks();

            // Позиція камери
            Vector3d camera = mc.gameRenderer.getActiveRenderInfo().getProjectedView();

            // Зберігаємо матрицю
            er.getMatrixStack().push();
            
            // Переміщуємо до позиції гравця
            er.getMatrixStack().translate(
                x - camera.x,
                y + player.getHeight() + 0.5f - camera.y,
                z - camera.z
            );

            // Повертаємо до камери
            er.getMatrixStack().rotate(mc.getRenderManager().getCameraOrientation());

            // Масштабуємо в залежності від відстані
            float distance = (float) mc.player.getDistance(player);
            float scale = Math.max(distance * 0.15f, 1.5f);
            er.getMatrixStack().scale(-0.025f * scale, -0.025f * scale, 0.025f * scale);

            // Текст здоров'я
            String text = Math.round(player.getHealth()) + "❤";

            // Центруємо текст
            float width = Fonts.gilroy[18].getWidth(text) / 2;

            // Колір тексту в залежності від здоров'я
            int color = getHealthColor(player.getHealth(), player.getMaxHealth());
            
            // Малюємо текст
            Fonts.gilroy[18].drawString(
                er.getMatrixStack(),
                text,
                -width,
                0,
                color
            );

            er.getMatrixStack().pop();
        }
    }

    private int getHealthColor(float health, float maxHealth) {
        float percentage = health / maxHealth;
        if (percentage > 0.75f) {
            return 0xFF00FF00; // Зелений
        } else if (percentage > 0.5f) {
            return 0xFFFFFF00; // Жовтий
        } else if (percentage > 0.25f) {
            return 0xFFFF8C00; // Оранжевий
        } else {
            return 0xFFFF0000; // Червоний
        }
    }

    private void renderModule(EventRender e) {
        if (mc == null || mc.getMainWindow() == null) return;

        int yPos = ModulePanelManager.getModulePosition(getName());
        int screenWidth = mc.getMainWindow().getScaledWidth();

        Color backgroundColor = isEnable() 
            ? new Color(ClickGUI.getThemeColors()[3].getColor().getRed(),
                       ClickGUI.getThemeColors()[3].getColor().getGreen(),
                       ClickGUI.getThemeColors()[3].getColor().getBlue(), 150)
            : new Color(ClickGUI.getThemeColors()[1].getColor().getRed(),
                       ClickGUI.getThemeColors()[1].getColor().getGreen(),
                       ClickGUI.getThemeColors()[1].getColor().getBlue(), 150);

        // Фон
        RenderUtils.drawRoundedRect(
            screenWidth - 105, yPos,
            100, 20, 3,
            backgroundColor
        );

        // Тінь
        RenderUtils.drawShadow(
            screenWidth - 105, yPos,
            100, 20, 6,
            new Color(ClickGUI.getThemeColors()[1].getColor().getRed(),
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
            new Color(ClickGUI.getThemeColors()[4].getColor().getRed(),
                     ClickGUI.getThemeColors()[4].getColor().getGreen(),
                     ClickGUI.getThemeColors()[4].getColor().getBlue()
            ).getRGB()
        );
    }
}