package me.hollow.sputnik.client.command.commands;

import me.hollow.sputnik.Sputnik;
import me.hollow.sputnik.client.command.Command;
import me.hollow.sputnik.client.command.CommandManifest;

@CommandManifest(label = "Save", aliases = "s")
public class SaveCommand extends Command {

    @Override
    public void execute(String[] args) {
        Sputnik.INSTANCE.getConfigManager().saveConfig(Sputnik.INSTANCE.getConfigManager().config.replaceFirst("TrollGod/", ""));
    }
}
