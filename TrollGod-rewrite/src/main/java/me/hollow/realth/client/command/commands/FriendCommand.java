package me.hollow.realth.client.command.commands;

import me.hollow.realth.Realth;
import me.hollow.realth.client.command.Command;
import me.hollow.realth.client.command.CommandManifest;

@CommandManifest(label = "Friend", aliases = {"f"})
public class FriendCommand extends Command {

    @Override
    public void execute(String[] arguments) {
        if (arguments.length < 3)
            return;

        switch (arguments[1].toUpperCase()) {
            case "ADD":
                Realth.INSTANCE.getFriendManager().addFriend(arguments[2]);
                break;
            case "DELETE":
            case "DEL":
                Realth.INSTANCE.getFriendManager().removeFriend(arguments[2]);
                break;
        }
    }
}
