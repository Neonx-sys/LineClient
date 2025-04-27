package line.lineclient.commands.commands;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import line.lineclient.Client;
import line.lineclient.commands.Command;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FriendCommand extends Command {

    private static final File FRIENDS_FILE = new File("Yougame/files/friends.json");
    private static final Gson GSON = new Gson();

    public FriendCommand() {
        super("friend");
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            Client.addChatMessage("Используйте: \n\n.friend add - Добавить друга.\n.friend remove - Удалить друга.\n.friend clear - Очистить друзей.\n.friend list - Список друзей.");
            return;
        }

        switch (args[0]) {
            case "add":
                if (args.length < 2) {
                    Client.addChatMessage("Используйте: .friend add <name>");
                    return;
                }
                addFriend(args[1]);
                Client.addChatMessage("Друг: " + args[1] + " успешно добавлен!");
                break;
            case "remove":
                if (args.length < 2) {
                    Client.addChatMessage("Используйте: .friend remove <name>");
                    return;
                }
                if (removeFriend(args[1])) {
                    Client.addChatMessage("Друг: " + args[1] + " успешно удален!");
                } else {
                    Client.addChatMessage("Друг: " + args[1] + " не найден!");
                }
                break;
            case "clear":
                clearFriends();
                Client.addChatMessage("Список друзей очищен!");
                break;
            case "list":
                listFriends();
                break;
            default:
                Client.addChatMessage("Аргумент " + args[0] + " не найден!");
                break;
        }
    }

    private void addFriend(String name) {
        List<String> friends = loadFriends();
        if (!friends.contains(name)) {
            friends.add(name);
            saveFriends(friends);
        }
    }

    private boolean removeFriend(String name) {
        List<String> friends = loadFriends();
        if (friends.remove(name)) {
            saveFriends(friends);
            return true;
        }
        return false;
    }

    private void clearFriends() {
        saveFriends(new ArrayList<>());
    }

    private void listFriends() {
        List<String> friends = loadFriends();
        if (friends.isEmpty()) {
            Client.addChatMessage("Друзья не найдены.");
        } else {
            StringBuilder sb = new StringBuilder("Список друзей:\n\n");
            for (String friend : friends) {
                sb.append(friend).append("\n");
            }
            Client.addChatMessage(sb.toString());
        }
    }

    private List<String> loadFriends() {
        if (!FRIENDS_FILE.exists()) {
            try {
                FRIENDS_FILE.getParentFile().mkdirs();
                FRIENDS_FILE.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new ArrayList<>();
        }

        try (FileReader reader = new FileReader(FRIENDS_FILE)) {
            Type type = new TypeToken<List<String>>() {}.getType();
            return GSON.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void saveFriends(List<String> friends) {
        try (FileWriter writer = new FileWriter(FRIENDS_FILE)) {
            GSON.toJson(friends, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}