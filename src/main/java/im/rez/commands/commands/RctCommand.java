package im.rez.commands.commands;

import im.rez.Client;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.client.gui.screen.ConnectingScreen;
import net.minecraft.client.multiplayer.ServerData;
import im.rez.commands.Command;

import static im.rez.utils.Wrapper.mc;


public class RctCommand extends Command {

    private int an = -1;

    public RctCommand() {
        super("rct");
    }

    @Override
    public void execute(String[] args) {
        if (args.length > 0) {
            try {
                mc.player.sendChatMessage("/hub");
                mc.player.sendChatMessage("/an" + an);
            } catch (NumberFormatException e) {
                Client.addChatMessage("Некоректный номер анархии!");
            }
        } else {
            reconnectServer();
        }
    }

    private void findAn() {
        Scoreboard scoreboard = Minecraft.getInstance().world.getScoreboard();
        for (ScoreObjective objective : scoreboard.getScoreObjectives()) {
            String displayName = objective.getDisplayName().getString();

            String cleanDisplayName = displayName.replaceAll("[^А-Яа-я0-9-]", "");

            if (cleanDisplayName.startsWith("Анархия-")) {
                try {
                    String[] parts = cleanDisplayName.split("-");
                    if (parts.length == 2) {
                        an = Integer.parseInt(parts[1].trim());
                    }
                } catch (NumberFormatException e) {
                    an = -1;
                }
            }
        }
    }

    private void reconnectServer() {
        findAn();
        ServerData serverData = Minecraft.getInstance().getCurrentServerData();
        if (serverData != null) {
            Minecraft.getInstance().world.sendQuittingDisconnectingPacket();
            Minecraft.getInstance().displayGuiScreen(new ConnectingScreen(Minecraft.getInstance().currentScreen, Minecraft.getInstance(), serverData));

            new java.util.Timer().schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    sendAn();
                }
            }, 1000);
        }
    }

    private void sendAn() {
        if (an != -1) {
            String command = "/an" + an;
            Minecraft.getInstance().player.sendChatMessage(command);
        } else {
        }
    }
}