package line.lineclient.module.modules;

import line.lineclient.module.Module;
import line.lineclient.module.modules.movement.Speed;
import line.lineclient.module.modules.render.HUD;
import line.lineclient.module.modules.render.Test;
import line.lineclient.module.modules.util.AutoBuy;
import line.lineclient.module.modules.movement.*;
import line.lineclient.module.modules.render.*;
import line.lineclient.module.modules.util.*;

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
