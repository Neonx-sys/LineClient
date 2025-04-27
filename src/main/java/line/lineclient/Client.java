package line.lineclient;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import line.lineclient.commands.CommandManager;
import line.lineclient.commands.commands.ConfigCommand;
import line.lineclient.module.modules.ModuleManager;
import line.lineclient.utils.fonts.Fonts;

import java.util.Comparator;

import static line.lineclient.utils.Wrapper.mc;

public class Client {
    public static String name = "LineClient";
    public static String version = "1.0";
    public static Client instance;
    public static CommandManager commandManager;

    @Getter
    @Setter
    public static UserData userData;

    public void init() {
        new ConfigCommand().loadLastConfig();
        ModuleManager.ModulesRegistry();
        Fonts.init();
        ModuleManager.getModules().sort(Comparator.comparingDouble(module -> -Fonts.gilroyBold[20].getWidth(module.getName())));
        commandManager = new CommandManager();
        commandManager.init();
    }

    public static CommandManager getCommandManager() {
        return commandManager;
    }

    public static void parsing(String response) {
        try {
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonResponse = jsonParser.parse(response).getAsJsonObject();

            String status = jsonResponse.get("status").getAsString();

            if ("success".equalsIgnoreCase(status)) {
                JsonObject data = jsonResponse.getAsJsonObject("data");
                String login = data.get("login").getAsString();
                String role = data.get("role").getAsString();
                int uid = data.get("id").getAsInt();
                userData = new UserData(login, role, uid);
            } else {
                System.exit(1488);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1488);
        }
    }

    public static void addChatMessage(String msg) {
        mc.ingameGUI.getChatGUI().printChatMessage(new TranslationTextComponent(TextFormatting.BLUE + name + TextFormatting.GRAY + " -> " + TextFormatting.RESET + msg));
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserData {
        final String login;
        final String role;
        final int uid;
    }
}
