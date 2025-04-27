package line.lineclient.module.modules.movement;

import line.lineclient.event.Event;
import line.lineclient.event.events.EventTick;
import line.lineclient.module.Category;
import line.lineclient.module.Module;
import line.lineclient.ui.clickgui.setting.settings.ModeSetting;

import static line.lineclient.utils.Wrapper.mc;

public class Speed extends Module {
    private final ModeSetting mode = new ModeSetting("FunTime");
    public Speed() {
        super("Speed", Category.MOVEMENT, "Ускоряет вас", -1, 1);
        addSetting(mode);
    }

    @Override
    public void event(Event e) {
        if(e instanceof EventTick) {
            if (mc != null && mc.player != null) {
                if (mode.is("FunTime")) {

                }
            }
        }
    }
}