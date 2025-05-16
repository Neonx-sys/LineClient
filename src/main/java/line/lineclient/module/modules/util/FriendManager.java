package line.lineclient.module.modules.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class FriendManager {
    private static final File FRIENDS_FILE = new File("Yougame/files/friends.json");
    private static final Gson GSON = new Gson();

    public static List<String> getFriends() {
        if (!FRIENDS_FILE.exists()) {
            return new ArrayList<>();
        }

        try (FileReader reader = new FileReader(FRIENDS_FILE)) {
            return GSON.fromJson(reader, new TypeToken<List<String>>(){}.getType());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static boolean isFriend(String name) {
        return getFriends().contains(name);
    }
}