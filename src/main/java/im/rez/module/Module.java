package im.rez.module;

import im.rez.event.Event;
import im.rez.module.modules.ModuleManager;
import im.rez.ui.clickgui.ClickGUI;
import im.rez.ui.clickgui.setting.Setting;
import im.rez.utils.animate.AnimateUtil;
import im.rez.utils.math.MathUtil;

import java.util.ArrayList;
import java.util.Arrays;

import static im.rez.utils.Wrapper.mc;

public class Module {
    public String name;
    public boolean enable = false;
    public boolean binding;
    public Category category;
    public boolean setting = false;
    public int server;
    public int bind;
    public String desc;
    private ArrayList<Setting> settings;
    private float scrol = 0;
    private AnimateUtil scrole = new AnimateUtil(0, 0, 0.15f);

    public Module(String name, Category category, String desc, int bind, int server) {
        this.name = name;
        this.category = category;
        this.desc = desc;
        this.bind = bind;
        this.server = server;
        settings = new ArrayList<>();
    }

    public void toggle() {
        this.enable = !this.enable;
        if (this.enable) {
            onEnable();
        } else {
            onDisable();
        }
    }


    public void onEnable() {
    }

    public void onDisable() {
    }

    public String getDescription() {
        return desc;
    }

    public String getName() {
        return name;
    }

    public void addSetting(Setting... addsetting) {
        settings.addAll(Arrays.asList(addsetting));
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public ArrayList<Setting> getSettings() {
        return settings;
    }

    public boolean isSetting() {
        return setting;
    }

    public boolean isBinding() {
        return binding;
    }

    public int getBind() {
        return bind;
    }

    public void setBind(int bind) {
        this.bind = bind;
    }

    public void setBinding(boolean binding) {
        this.binding = binding;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void Scrol(double a) {
        scrol += a * (6.5 + (mc.currentScreen instanceof ClickGUI ? 6 : 0));
        float size = 0;

        for (Module b : ModuleManager.getModules()) {
            size += 30 + 40;
        }

        float midle = size;
        scrol = MathUtil.clamp(scrol, -midle, 5);
        scrole.to = scrol;
    }

    public AnimateUtil getScrole() {
        return scrole;
    }

    public void event(Event e) {}
}
