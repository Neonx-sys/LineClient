package line.lineclient.ui.clickgui.setting.settings;

import line.lineclient.module.Module;
import line.lineclient.module.modules.ModuleManager;
import java.util.ArrayList;
import java.util.List;

public class ModulePanelManager {
    private static final List<String> activeModules = new ArrayList<>();
    private static final int PANEL_HEIGHT = 25;    
    private static final int PANEL_SPACING = 5;    
    private static final int SCREEN_MARGIN = 5;    

    public static void addModule(String moduleName) {
        if (!activeModules.contains(moduleName)) {
            activeModules.add(moduleName);
        }
    }

    public static void removeModule(String moduleName) {
        activeModules.remove(moduleName);
    }

    public static void updateActiveModules() {
        activeModules.clear();
        for (Module module : ModuleManager.getModules()) {
            if (module.isEnable()) {
                addModule(module.getName());
            }
        }
    }

    public static int getModulePosition(String moduleName) {
        updateActiveModules(); // Оновлюємо список перед отриманням позиції
        int index = activeModules.indexOf(moduleName);
        if (index == -1) return SCREEN_MARGIN;
        return SCREEN_MARGIN + index * (PANEL_HEIGHT + PANEL_SPACING);
    }

    public static void clear() {
        activeModules.clear();
    }

    public static List<String> getActiveModules() {
        updateActiveModules(); // Оновлюємо список перед поверненням
        return new ArrayList<>(activeModules);
    }
}