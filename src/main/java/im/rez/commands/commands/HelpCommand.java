package im.rez.commands.commands;

import im.rez.Client;
import im.rez.commands.Command;

public class HelpCommand extends Command {

    public HelpCommand() {
        super("help");
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            Client.addChatMessage("Список команд: \n\n.help - Список команд.\n.friend - Друзья.\n.rct - Перезайти на сервер.\n.config - Конфиги.");
        }
    }
}
