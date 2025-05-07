package line.lineclient.module.modules;

import line.lineclient.module.Module;
import line.lineclient.module.modules.combat.AutoTotem;
import line.lineclient.module.modules.combat.GhostAura;
import line.lineclient.module.modules.combat.KillAura;
import line.lineclient.module.modules.movement.Speed;
import line.lineclient.module.modules.render.ESP;
import line.lineclient.module.modules.render.HUD;
import line.lineclient.module.modules.render.Test;
import line.lineclient.module.modules.util.AutoBuy;
import line.lineclient.module.modules.movement.*;
import line.lineclient.module.modules.util.FastBreak;

import java.util.ArrayList;

public class ModuleManager {
    public static ArrayList<Module> modules = new ArrayList<>();
    public static Test test = new Test();
    public static HUD hud = new HUD();
    public static Speed speed = new Speed();
    public static AutoBuy autoBuy = new AutoBuy();
    public static GhostAura ghostAura = new GhostAura();
    public static AutoTotem autoTotem = new AutoTotem();
    public static KillAura killAura = new KillAura();
    public static Spider spider = new Spider();
    public static FastBreak fastBreak = new FastBreak();
    public static ESP ESP = new ESP();

    public static void ModulesRegistry(){
        add(test);
        add(hud);
        add(speed);
        add(autoBuy);
        add(ghostAura);
        add(autoTotem);
        add(killAura);
        add(spider);
        add(fastBreak);
        add(ESP);
    }

    static void add(Module m) {
        modules.add(m);
    }

    public static ArrayList<Module> getModules() {
        return modules;
    }
}
