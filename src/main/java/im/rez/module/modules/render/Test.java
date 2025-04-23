package im.rez.module.modules.render;

import im.rez.event.Event;
import im.rez.event.events.EventRender;
import im.rez.module.Category;
import im.rez.module.Module;
import im.rez.ui.clickgui.setting.settings.ColorSetting;

import java.awt.*;

public class Test extends Module {
public ColorSetting colorSetting = new ColorSetting("Цвет для секса", new Color(-1));

    public Test() {
        super("Test", Category.RENDER, "Тестировка модуля", -1, 1);
        addSetting(colorSetting);
    }

    @Override
    public void event(Event e) {
        if(e instanceof EventRender) {

        }
    }
}
