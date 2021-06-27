package me.hollow.sputnik.api.util;

import me.hollow.sputnik.api.interfaces.Minecraftable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class MessageUtil implements Minecraftable {

    private static final String prefix = "[\u00a75\u00a7lTrollGod\u00a7r] \u00a7d";

    public static void sendClientMessage(String string, boolean deleteOld) {
        if (mc.player == null)
            return;
        final ITextComponent component = new TextComponentString(prefix + string);
        if (deleteOld) {
            mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(component, -727);
        } else {
            mc.ingameGUI.getChatGUI().printChatMessage(component);
        }
    }

    public static void sendClientMessage(String string, int id) {
        if (mc.player == null)
            return;
        final ITextComponent component = new TextComponentString(prefix + string);
        mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(component, id);
    }

}
