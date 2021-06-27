package me.hollow.realth.client.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.hollow.realth.Realth;
import me.hollow.realth.api.utils.ChatUtil;
import me.hollow.realth.client.command.Command;
import me.hollow.realth.client.command.CommandManifest;
import me.hollow.realth.client.module.Module;
import org.lwjgl.input.Keyboard;

@CommandManifest(label = "Bind", aliases = {"b"})
public class BindCommand extends Command {

    @Override
    public void execute(String[] arguments) {
        if (arguments.length < 2) {
            ChatUtil.printMessage(ChatFormatting.RED + "Not enough arguments!");
            return;
        }
        final Module module = Realth.INSTANCE.getModuleManager().findByLabel(arguments[1]);
        if (module == null) {
            ChatUtil.printMessage(ChatFormatting.RED + "Couldnt find a module labeled " + arguments[1] + "!");
            return;
        }

        int keyIndex = Keyboard.getKeyIndex(arguments[2].toUpperCase());
        module.setKey(keyIndex);
        ChatUtil.printMessage(ChatFormatting.GREEN + "Bound " + module.getLabel());
    }

}
