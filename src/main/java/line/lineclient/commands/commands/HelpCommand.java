package line.lineclient.commands.commands;

import line.lineclient.Client;
import line.lineclient.commands.Command;

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
