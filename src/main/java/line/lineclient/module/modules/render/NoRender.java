package line.lineclient.module.modules.render;

import line.lineclient.event.Event;
import line.lineclient.event.events.EventRender;
import line.lineclient.event.events.EventTick;
import line.lineclient.module.Category;
import line.lineclient.module.Module;
import line.lineclient.ui.clickgui.setting.settings.CheckBoxSetting;
import line.lineclient.ui.clickgui.setting.settings.CheckButtonSetting;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.item.Items;
import java.util.Collection;

import static line.lineclient.utils.Wrapper.mc;

public class NoRender extends Module {
    private final CheckBoxSetting renderWeather = new CheckBoxSetting("Render weather", true);
    private final CheckButtonSetting renderHurtCamera = new CheckButtonSetting("Render hurt camera", true);

    private boolean wasRaining = false;
    private float oldRainStrength = 0f;
    private float oldThunderStrength = 0f;

    public NoRender() {
        super("NoRender", Category.RENDER, "Off visual effects", -1, 2);
        addSetting(renderWeather);
        addSetting(renderHurtCamera);
    }

    @Override
    public void event(Event e) {
        if (mc.world == null || mc.player == null) return;

        if (e instanceof EventRender) {
            if (renderHurtCamera.get()) {
                mc.player.hurtTime = 0;
                mc.player.maxHurtTime = 0;
            }
        }

        if (e instanceof EventTick) {
            if (renderWeather.get()) {
                if (!wasRaining) {
                    oldRainStrength = mc.world.getRainStrength(1.0F);
                    oldThunderStrength = mc.world.getThunderStrength(1.0F);
                    wasRaining = true;
                }
                mc.world.getWorldInfo().setRaining(false);
                mc.world.setRainStrength(0);
                mc.world.setThunderStrength(0);
            } else if (wasRaining) {
                mc.world.getWorldInfo().setRaining(true);
                mc.world.setRainStrength(oldRainStrength);
                mc.world.setThunderStrength(oldThunderStrength);
                wasRaining = false;
            }
        }
    }

    @Override
    public void onDisable() {
        if (mc.world != null) {
            if (wasRaining) {
                mc.world.getWorldInfo().setRaining(true);
                mc.world.setRainStrength(oldRainStrength);
                mc.world.setThunderStrength(oldThunderStrength);
                wasRaining = false;
            }
        }
    }
}