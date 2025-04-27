package line.lineclient.event;

import line.lineclient.event.events.EventRender;
import line.lineclient.event.events.EventRotation;
import line.lineclient.event.events.EventTick;
import line.lineclient.module.Module;
import line.lineclient.module.modules.ModuleManager;

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
