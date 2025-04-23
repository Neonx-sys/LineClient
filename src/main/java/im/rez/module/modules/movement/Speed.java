package im.rez.module.modules.movement;

import im.rez.event.Event;
import im.rez.event.events.EventTick;
import im.rez.module.Category;
import im.rez.module.Module;
import im.rez.ui.clickgui.setting.settings.ModeSetting;

import static im.rez.utils.Wrapper.mc;

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