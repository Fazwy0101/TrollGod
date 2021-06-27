package me.hollow.realth.client.command.commands;

import me.hollow.realth.Realth;
import me.hollow.realth.api.utils.ChatUtil;
import me.hollow.realth.client.command.Command;
import me.hollow.realth.client.command.CommandManifest;
import me.hollow.realth.client.module.Module;

@CommandManifest(label = "drawn", aliases = {"hide"})
public class DrawnCommand extends Command {

    @Override
    public void execute(String[] arguments) {
        Module module = Realth.INSTANCE.getModuleManager().findByLabel(arguments[1]);
        if (module != null) {
            module.setDrawn(!module.isDrawn());
            ChatUtil.printMessage(module.getLabel() + " has been " + (module.isDrawn() ? "unhidden" : "hidden"));
        }
    }

}
