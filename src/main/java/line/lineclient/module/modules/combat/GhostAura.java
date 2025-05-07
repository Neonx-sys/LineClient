package line.lineclient.module.modules.combat;

import line.lineclient.event.Event;
import line.lineclient.event.events.EventRender;
import line.lineclient.event.events.EventTick;
import line.lineclient.module.Category;
import line.lineclient.module.Module;
import line.lineclient.ui.clickgui.ClickGUI;
import line.lineclient.ui.clickgui.setting.settings.ModulePanelManager;
import line.lineclient.ui.clickgui.setting.settings.ModeSetting;
import line.lineclient.utils.render.RenderUtils;
import line.lineclient.utils.fonts.Fonts;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.Hand;

import java.awt.Color;

import static line.lineclient.utils.Wrapper.mc;

public class GhostAura extends Module {
    private final ModeSetting mode = new ModeSetting("FunTime");
    private long lastAttackTime = 0;
    private final long attackDelay = 10;

    public GhostAura() {
        super("GhostAura", Category.COMBAT, "Tp to nearby enemy and attacks", -1, 1);
        addSetting(mode);
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
            if (mc != null && mc.player != null && mc.world != null) {
                if (mode.is("FunTime")) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastAttackTime < attackDelay) {
                        return;
                    }

                    LivingEntity closestTarget = null;
                    double closestDistance = Double.MAX_VALUE;

                    for (Entity entity : mc.world.getAllEntities()) {
                        if (entity instanceof LivingEntity) {
                            LivingEntity target = (LivingEntity) entity;

                            if (target != mc.player && target.isAlive()) {
                                double distance = mc.player.getDistance(target);
                                if (distance <= 10.0f && distance < closestDistance) {
                                    closestDistance = distance;
                                    closestTarget = target;
                                }
                            }
                        }
                    }

                    if (closestTarget != null) {
                        double oldX = mc.player.getPosX();
                        double oldY = mc.player.getPosY();
                        double oldZ = mc.player.getPosZ();

                        mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(
                                closestTarget.getPosX(), closestTarget.getPosY(), closestTarget.getPosZ(), true
                        ));

                        mc.playerController.attackEntity(mc.player, closestTarget);
                        mc.player.swingArm(Hand.MAIN_HAND);

                        mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(
                                oldX, oldY, oldZ, true
                        ));

                        lastAttackTime = currentTime;
                    }
                }
            }
        } else if (e instanceof EventRender) {
            renderModule((EventRender) e);
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