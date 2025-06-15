package line.lineclient.module.modules.render;

import line.lineclient.event.Event;
import line.lineclient.event.events.EventRender;
import line.lineclient.module.Category;
import line.lineclient.module.Module;
import line.lineclient.ui.clickgui.setting.settings.CheckButtonSetting;
import line.lineclient.ui.clickgui.setting.settings.ValueSetting;

public class HitParticles extends Module {
    private final CheckButtonSetting krit = new CheckButtonSetting("Render krit hits", false);
    private final CheckButtonSetting enchantments = new CheckButtonSetting("Render enchantments hits", false);
    private final CheckButtonSetting boxes = new CheckButtonSetting("Render boxes hits", false);

    private final ValueSetting particleCount = new ValueSetting("Particle count", 2f, 1f, 100f, 1f);

    public HitParticles() {
        super("HitParticles", Category.RENDER, "Hit particles", -1, 1);
        addSetting(particleCount);
        addSetting(krit);
        addSetting(enchantments);
        addSetting(boxes);
    }

    public void event(Event e) {
        if (e instanceof EventRender) {
            EventRender event = (EventRender) e;
            if (event.getPartialTicks() == 0) {
                return;
            }
        }
    }

    public int GetParticles() {
        if (krit.get()) {
            return 1;
        }
        else if (enchantments.get()) {
            return 2;
        }
        else if (boxes.get()) {
            return 3;
        }
        return 0;
    }

    public int GetParticleCount() {
        return (int) particleCount.getValue();
    }
}
