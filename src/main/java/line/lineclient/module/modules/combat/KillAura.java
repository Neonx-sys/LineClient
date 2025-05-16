package line.lineclient.module.modules.combat;

import line.lineclient.event.Event;
import line.lineclient.event.events.EventRender;
import line.lineclient.event.events.EventTick;
import line.lineclient.module.Category;
import line.lineclient.module.Module;
import line.lineclient.ui.clickgui.ClickGUI;
import line.lineclient.ui.clickgui.setting.settings.ModulePanelManager;
import line.lineclient.ui.clickgui.setting.settings.ModeSetting;
import line.lineclient.ui.clickgui.setting.settings.ValueSetting;
import line.lineclient.utils.render.RenderUtils;
import line.lineclient.utils.fonts.Fonts;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import java.awt.Color;
import line.lineclient.module.modules.util.FriendManager;

import static line.lineclient.utils.Wrapper.mc;

public class KillAura extends Module {
    private final ModeSetting mode = new ModeSetting("FunTime");
    private final ValueSetting range = new ValueSetting("Range", 4.0f, 1.0f, 6.0f, 0.1f);
    private final ValueSetting delay = new ValueSetting("Delay", 300f, 100f, 1000f, 10f);
    private long lastAttackTime = 0;

    public KillAura() {
        super("KillAura", Category.COMBAT, "Атакує найближчу ціль", -1, 1);
        addSetting(mode);
        addSetting(range);
        addSetting(delay);
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
            handleAttack();
        } else if (e instanceof EventRender) {
            renderModule((EventRender) e);
        }
    }

    private void handleAttack() {
        if (mc != null && mc.player != null && mc.world != null) {
            if (mode.is("FunTime")) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastAttackTime < delay.getValue()) return;

                for (Entity entity : mc.world.getAllEntities()) {
                    if (entity instanceof LivingEntity) {
                        LivingEntity target = (LivingEntity) entity;

                        if (target != mc.player &&
                                target.isAlive() &&
                                mc.player.getDistance(target) <= range.getValue() &&
                                !FriendManager.isFriend(target.getName().getString())) {  // Додана перевірка

                            mc.playerController.attackEntity(mc.player, target);
                            mc.player.swingArm(Hand.MAIN_HAND);
                            lastAttackTime = currentTime;
                            break;
                        }
                    }
                }
            }
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
