package me.hollow.realth.client.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.hollow.realth.Realth;
import me.hollow.realth.api.utils.ChatUtil;
import me.hollow.realth.client.command.Command;
import me.hollow.realth.client.command.CommandManifest;

@CommandManifest(label = "Toggle", aliases = {"t", "tog"})
public class ToggleCommand extends Command {

    @Override
    public void execute(String[] arguments) {
        try {
            Realth.INSTANCE.getModuleManager().findByLabel(arguments[1]).toggle();
            ChatUtil.printMessage(ChatFormatting.GREEN + "Toggled " + arguments[1] + "!");
        } catch (NullPointerException exception) {
            ChatUtil.printMessage(ChatFormatting.RED + "Couldnt find a module labeled " + arguments[1]);
        }
    }

}
