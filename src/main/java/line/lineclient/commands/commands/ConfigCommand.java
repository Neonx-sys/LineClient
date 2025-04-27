package line.lineclient.commands.commands;

import com.google.gson.*;
import line.lineclient.Client;
import line.lineclient.commands.Command;
import line.lineclient.module.Module;
import line.lineclient.module.modules.ModuleManager;
import line.lineclient.module.modules.render.HUD;
import line.lineclient.ui.clickgui.ClickGUI;
import line.lineclient.ui.clickgui.setting.Setting;
import line.lineclient.ui.clickgui.setting.settings.CheckBoxSetting;
import line.lineclient.ui.clickgui.setting.settings.ColorSetting;
import line.lineclient.ui.clickgui.setting.settings.ModeSetting;
import line.lineclient.ui.clickgui.setting.settings.ValueSetting;

import java.awt.*;
import java.io.*;
import java.util.Map;

public class ConfigCommand extends Command {

    private static final String CONFIG_DIR = "Yougame/files/configs/";
    private static final String LAST_CONFIG_FILE = "Yougame/files/lastconfig.txt";

    public ConfigCommand() {
        super("cfg");
    }

        @Override
        public void execute(String[] args) {
            if (args.length < 1) {
                Client.addChatMessage("Используйте: \n\n.cfg save - Сохранить конфиг.\n.cfg load - Загрузить конфиг.\n.cfg remove - Удалить конфиг.\n.cfg list - Список конфигов.\n.cfg dir - Открыть папку конфигов.\n.cfg clear - Удалить все конфиги.");
                return;
            }

            String action = args[0];
            String name = args.length > 1 ? args[1] : null;
            File configFile = name != null ? new File(CONFIG_DIR + name + ".cfg") : null;

            File configDir = new File(CONFIG_DIR);
            if (!configDir.exists()) {
                configDir.mkdirs();
            }

            switch (action.toLowerCase()) {
                case "save":
                    if (name == null) {
                        Client.addChatMessage("Не указано название для сохранения!");
                        return;
                    }
                    loadConfig(configFile);
                    saveConfig(configFile);
                    Client.addChatMessage("Конфиг: " + name + " сохранен!");
                    break;
                case "load":
                    if (configFile != null && configFile.exists()) {
                        loadConfig(configFile);
                        saveLastLoadedConfig(name);
                        Client.addChatMessage("Конфиг: " + name + " загружен!");
                    } else {
                        Client.addChatMessage("Конфиг: " + name + " не найден!");
                    }
                    break;
                case "remove":
                    if (configFile != null && configFile.exists()) {
                        deleteConfig(configFile);
                        Client.addChatMessage("Конфиг: " + name + " удален!");
                    } else {
                        Client.addChatMessage("Конфиг: " + name + " не найден!");
                    }
                    break;
                case "list":
                    listConfigs();
                    break;
                case "dir":
                    openConfigDir();
                    break;
                case "clear":
                    clearAllConfigs();
                    break;
                default:
                    Client.addChatMessage("Неизвестное действие: " + action);
                    break;
            }
        }

    private void saveConfig(File file) {
        try {
            JsonObject config = new JsonObject();
            JsonObject modulesObject = new JsonObject();
            for (Module module : ModuleManager.modules) {
                JsonObject moduleObject = new JsonObject();
                moduleObject.addProperty("enabled", module.isEnable());
                moduleObject.addProperty("bind", module.getBind());
                if (!module.getSettings().isEmpty()) {
                    JsonObject settingsObject = new JsonObject();
                    for (Setting setting : module.getSettings()) {
                        if (setting instanceof CheckBoxSetting) {
                            settingsObject.addProperty(setting.getName(), ((CheckBoxSetting) setting).get());
                        }
                        else if (setting instanceof ValueSetting) {
                            settingsObject.addProperty(setting.getName(), ((ValueSetting) setting).getValue());
                        }
                        else if (setting instanceof ModeSetting) {
                            settingsObject.addProperty(setting.getName(), ((ModeSetting) setting).getMode());
                        }
                    }
                    moduleObject.add("settings", settingsObject);
                }

                if (module instanceof HUD) {
                    HUD hud = (HUD) module;
                    JsonObject posObject = new JsonObject();
                    posObject.addProperty("watermarkX", hud.watermarkX);
                    posObject.addProperty("watermarkY", hud.watermarkY);
                    posObject.addProperty("bpsX", hud.bpsX);
                    posObject.addProperty("bpsY", hud.bpsY);
                    moduleObject.add("positions", posObject);
                }

                modulesObject.add(module.getName(), moduleObject);
            }
            config.add("modules", modulesObject);
            JsonObject themeObject = new JsonObject();
            ColorSetting[] themeColors = ClickGUI.getThemeColors();

            for (ColorSetting color : themeColors) {
                JsonObject colorObject = new JsonObject();
                Color currentColor = color.getColor();

                colorObject.addProperty("red", currentColor.getRed());
                colorObject.addProperty("green", currentColor.getGreen());
                colorObject.addProperty("blue", currentColor.getBlue());
                colorObject.addProperty("alpha", currentColor.getAlpha());

                float[] hsb = Color.RGBtoHSB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), null);
                colorObject.addProperty("hue", hsb[0]);
                colorObject.addProperty("saturation", hsb[1]);
                colorObject.addProperty("brightness", hsb[2]);

                themeObject.add(color.getName(), colorObject);
            }
            config.add("theme", themeObject);
            String jsonOutput = new GsonBuilder().setPrettyPrinting().create().toJson(config);
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(jsonOutput);
                writer.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveLastLoadedConfig(String configName) {
        try {
            File lastConfigFile = new File(LAST_CONFIG_FILE);
            lastConfigFile.getParentFile().mkdirs();
            FileWriter writer = new FileWriter(lastConfigFile);
            writer.write(configName);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadLastConfig() {
        try {
            File lastConfigFile = new File(LAST_CONFIG_FILE);
            if (lastConfigFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(lastConfigFile));
                String lastConfig = reader.readLine();
                reader.close();

                if (lastConfig != null && !lastConfig.isEmpty()) {
                    File configFile = new File(CONFIG_DIR + lastConfig + ".cfg");
                    if (configFile.exists()) {
                        loadConfig(configFile);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadConfig(File file) {
        try {
            JsonParser parser = new JsonParser();
            JsonObject config = parser.parse(new FileReader(file)).getAsJsonObject();
            if (config.has("modules")) {
                JsonObject modulesObject = config.getAsJsonObject("modules");
                for (Map.Entry<String, JsonElement> entry : modulesObject.entrySet()) {
                    Module module = ModuleManager.modules.stream()
                            .filter(m -> m.getName().equals(entry.getKey()))
                            .findFirst()
                            .orElse(null);

                    if (module != null) {
                        JsonObject moduleObject = entry.getValue().getAsJsonObject();
                        module.setEnable(moduleObject.get("enabled").getAsBoolean());
                        module.setBind(moduleObject.get("bind").getAsInt());
                        if (moduleObject.has("settings")) {
                            JsonObject settingsObject = moduleObject.getAsJsonObject("settings");
                            for (Setting setting : module.getSettings()) {
                                if (settingsObject.has(setting.getName())) {
                                    if (setting instanceof CheckBoxSetting) {
                                        ((CheckBoxSetting) setting).setValue(settingsObject.get(setting.getName()).getAsBoolean());
                                    }
                                    else if (setting instanceof ValueSetting) {
                                        ((ValueSetting) setting).setValue(settingsObject.get(setting.getName()).getAsFloat());
                                    }
                                    else if (setting instanceof ModeSetting) {
                                        ModeSetting modeSetting = (ModeSetting) setting;
                                        String newMode = settingsObject.get(setting.getName()).getAsString();
                                        int newIndex = 0;
                                        for (int i = 0; i < modeSetting.values.size(); i++) {
                                            if (modeSetting.values.get(i).equals(newMode)) {
                                                newIndex = i;
                                                break;
                                            }
                                        }
                                        modeSetting.setIndex(newIndex);
                                    }
                                }
                            }
                        }

                        if (module instanceof HUD && moduleObject.has("positions")) {
                            HUD hud = (HUD) module;
                            JsonObject posObject = moduleObject.getAsJsonObject("positions");
                            hud.watermarkX = posObject.get("watermarkX").getAsFloat();
                            hud.watermarkY = posObject.get("watermarkY").getAsFloat();
                            hud.bpsX = posObject.get("bpsX").getAsFloat();
                            hud.bpsY = posObject.get("bpsY").getAsFloat();
                        }
                    }
                }
            }
            if (config.has("theme")) {
                JsonObject themeObject = config.getAsJsonObject("theme");
                ColorSetting[] themeColors = ClickGUI.getThemeColors();

                for (ColorSetting color : themeColors) {
                    if (themeObject.has(color.getName())) {
                        JsonObject colorObject = themeObject.getAsJsonObject(color.getName());

                        int red = colorObject.get("red").getAsInt();
                        int green = colorObject.get("green").getAsInt();
                        int blue = colorObject.get("blue").getAsInt();
                        int alpha = colorObject.get("alpha").getAsInt();

                        Color newColor = new Color(red, green, blue, alpha);
                        color.setColor(newColor);

                        if (colorObject.has("hue")) {
                            color.setHue(colorObject.get("hue").getAsFloat());
                            color.setSaturation(colorObject.get("saturation").getAsFloat());
                            color.setBrightness(colorObject.get("brightness").getAsFloat());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


        private void deleteConfig(File file) {
            if (file.delete()) {
            } else {
            }
        }

        private void listConfigs() {
            File configDir = new File(CONFIG_DIR);
            File[] files = configDir.listFiles();

            if (files != null && files.length > 0) {
                StringBuilder list = new StringBuilder("Доступные конфиги:\n");
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".cfg")) {
                        list.append(file.getName().replace(".cfg", "")).append("\n");
                    }
                }
                Client.addChatMessage(list.toString());
            } else {
                Client.addChatMessage("Нет доступных конфигов.");
            }
        }

    private void openConfigDir() {
        File dir = new File(CONFIG_DIR);
        if (dir.exists() && dir.isDirectory()) {
            Client.addChatMessage("Папка конфигураций находится по следующему пути: " + dir.getAbsolutePath());
        } else {
            Client.addChatMessage("Папка конфигураций не найдена.");
        }
    }

        private void clearAllConfigs() {
            File configDir = new File(CONFIG_DIR);
            File[] files = configDir.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".cfg")) {
                        file.delete();
                    }
                }
                Client.addChatMessage("Все конфиги удалены.");
            } else {
                Client.addChatMessage("Нет доступных конфигов для удаления.");
            }
        }
    }