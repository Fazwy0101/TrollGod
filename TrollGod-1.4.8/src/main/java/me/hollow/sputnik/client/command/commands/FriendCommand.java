package me.hollow.sputnik.client.command.commands;

import me.hollow.sputnik.Sputnik;
import me.hollow.sputnik.client.command.Command;
import me.hollow.sputnik.client.command.CommandManifest;

@CommandManifest(label = "Friend", aliases = {"friends", "friend, f"})
public class FriendCommand extends Command {

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            return;
        }

        try {
            String name = args[2];
            switch (args[1].toUpperCase()) {
                case ("ADD"):
                    Sputnik.INSTANCE.getFriendManager().addFriend(name);
                    break;
                case ("DEL"):
                    Sputnik.INSTANCE.getFriendManager().removeFriend(name);
                    break;
                case ("DELETE"):
                    Sputnik.INSTANCE.getFriendManager().removeFriend(name);
                    break;
                case ("CLEAR"):
                    Sputnik.INSTANCE.getFriendManager().clearFriends();
                    break;
                case ("INSIDE"):
                    Sputnik.INSTANCE.getFriendManager().clearFriends();
                    break;
            }
        } catch (Exception e) {
        }
    }

}
