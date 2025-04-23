package im.rez.event;

import im.rez.event.events.EventRender;
import im.rez.event.events.EventRotation;
import im.rez.event.events.EventTick;
import im.rez.module.Module;
import im.rez.module.modules.ModuleManager;

public class Events {
    public static EventRender eventRender;
    public static EventTick eventTick;
    public static EventRotation eventRotation;

    public static void events(Event e) {
        for(Module m : ModuleManager.modules) {
            if(m.isEnable()){
                m.event(e);
            }
        }
    }
}
