package me.hollow.realth.client.command.commands;

import me.hollow.realth.api.utils.ChatUtil;
import me.hollow.realth.client.command.Command;
import me.hollow.realth.client.command.CommandManifest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.tutorial.TutorialSteps;

@CommandManifest(label = "Tutorial", aliases = {"t", "tut"})
public class TutorialCommand extends Command {

    @Override
    public void execute(String[] arguments) {
        ChatUtil.printMessageWithID("Set tutorial step to none", -222114);
        Minecraft.getMinecraft().gameSettings.tutorialStep = TutorialSteps.NONE;
        Minecraft.getMinecraft().getTutorial().setStep(TutorialSteps.NONE);
    }
}
