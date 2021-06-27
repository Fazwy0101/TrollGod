package me.hollow.realth.client.command.manage;

import me.hollow.realth.api.utils.ChatUtil;
import me.hollow.realth.client.command.Command;
import me.hollow.realth.client.command.commands.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class CommandManager {

    private final List<Command> commands = new ArrayList<>();
    private final Map<String, Command> aliasMap = new HashMap<>();

    public boolean checkForCommand(String text) {
        if (StringUtils.startsWith(text, ".")) {
            final String[] arguments = text.substring(1).split(" ");
            boolean foundCommand = false;
            for (Command cmd : commands) {
                if (arguments[0].equalsIgnoreCase(cmd.getLabel()) || aliasMap.get(arguments[0]) != null) {
                    cmd.execute(arguments);
                    foundCommand = true;
                }
            }
            if (!foundCommand) {
                ChatUtil.printMessage("Couldnt find a command labeled " + arguments[0]);
            }
            return true;
        }
        return false;
    }

    public void register(Command command) {
        commands.add(command);
        if (command.getAliases()[0].length() != 0) {
            Arrays.stream(command.getAliases()).forEach(als -> aliasMap.put(als, command));
        }
    }

    public void init() {
        register(new ToggleCommand());
        register(new BindCommand());
        register(new FriendCommand());
        register(new FakePlayerCommand());
        register(new DrawnCommand());
        register(new TutorialCommand());
    }

}
