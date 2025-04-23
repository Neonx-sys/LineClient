package im.rez.commands;

import im.rez.commands.commands.ConfigCommand;
import im.rez.commands.commands.FriendCommand;
import im.rez.commands.commands.HelpCommand;
import im.rez.commands.commands.RctCommand;
import im.rez.commands.commands.*;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final Map<String, Command> commands = new HashMap<>();

    public void registerCommand(Command command) {
        commands.put(command.getName(), command);
    }

    public void executeCommand(String name, String[] args) {
        Command command = commands.get(name);
        if (command != null) {
            command.execute(args);
        } else {
        }
    }

    public void init() {
        registerCommand(new FriendCommand());
        registerCommand(new HelpCommand());
        registerCommand(new RctCommand());
        registerCommand(new ConfigCommand());
    }
}