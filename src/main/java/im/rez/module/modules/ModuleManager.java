package im.rez.module.modules;

import im.rez.module.Module;
import im.rez.module.modules.movement.Speed;
import im.rez.module.modules.render.HUD;
import im.rez.module.modules.render.Test;
import im.rez.module.modules.util.AutoBuy;
import im.rez.module.modules.movement.*;
import im.rez.module.modules.render.*;
import im.rez.module.modules.util.*;

import java.util.ArrayList;

public class ModuleManager {
    public static ArrayList<Module> modules = new ArrayList<>();
    public static Test test = new Test();
    public static HUD hud = new HUD();
    public static Speed speed = new Speed();
    public static AutoBuy autoBuy = new AutoBuy();

    public static void ModulesRegistry(){
        add(test);
        add(hud);
        add(speed);
        add(autoBuy);
    }

    static void add(Module m) {
        modules.add(m);
    }

    public static ArrayList<Module> getModules() {
        return modules;
    }
}
