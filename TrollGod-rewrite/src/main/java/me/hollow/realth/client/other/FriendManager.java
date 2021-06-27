package me.hollow.realth.client.other;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import me.hollow.realth.api.utils.ChatUtil;
import net.minecraft.entity.player.EntityPlayer;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class FriendManager {

    private Set<String> friends = new HashSet<>();

    public void init() {
        if (!directory.exists()) {
            try {
                directory.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        loadFriends();
    }

    public void unload() {
        saveFriends();
    }

    private File directory;

    public void setDirectory(File directory) {
        this.directory = directory;
    }

    public void saveFriends() {
        if (directory.exists()) {
            try (final Writer writer = new FileWriter(directory)) {
                writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(friends));
            } catch (IOException e) {
                directory.delete();
                e.printStackTrace();
            }
        }
    }

    public void loadFriends() {
        if (!directory.exists())
            return;

        try (FileReader inFile = new FileReader(directory)) {
            friends = new HashSet<>(new GsonBuilder().setPrettyPrinting().create().fromJson(inFile, new TypeToken<HashSet<String>>(){}.getType()));
        } catch (Exception ignored) {}
    }

    public void addFriend(String name) {
        ChatUtil.printMessage("Added " + name + " as a friend ");
        friends.add(name);
    }

    public final boolean isFriend(String ign) {
        return friends.contains(ign);
    }

    public boolean isFriend(EntityPlayer ign) {
        return friends.contains(ign.getName());
    }

    public void clearFriends() {
        friends.clear();
    }

    public void removeFriend(String name) {
        friends.remove(name);
    }

}
