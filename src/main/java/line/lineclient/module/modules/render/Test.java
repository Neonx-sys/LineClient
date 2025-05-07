package line.lineclient.module.modules.render;

import line.lineclient.event.Event;
import line.lineclient.event.events.EventRender;
import line.lineclient.module.Category;
import line.lineclient.module.Module;
import line.lineclient.ui.clickgui.setting.settings.ColorSetting;
import line.lineclient.utils.render.RenderUtils;

import java.awt.*;

public class Test extends Module {
public ColorSetting colorSetting = new ColorSetting("", new Color(-1));

    public Test() {
        super("Test", Category.RENDER, "Тестировка модуля", -1, 1);
        addSetting(colorSetting);
    }

    @Override
    public void event(Event e) {
        if (e instanceof EventRender)
        {

        }
    }
}
