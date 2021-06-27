package me.hollow.realth.api.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;

public class ChatUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final String prefix = "\u00a78<Realth>\u00a7r ";

    public static void printMessage(String message) {
        if (mc.player == null) {
            return;
        }

        mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString(prefix + message));
    }

    public static void printMessageWithID(String message, int id) {
        if (mc.player == null) {
            return;
        }

        mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(prefix + message), id);
    }

}
