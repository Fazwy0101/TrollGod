package me.hollow.realth.client.module.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.hollow.realth.api.mixin.mixins.AccessorSPacketChat;
import me.hollow.realth.client.events.PacketEvent;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Date;

@ModuleManifest(label = "ChatTimeStamps", category = Module.Category.MISC)
public class ChatTimeStamps extends Module {

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketChat) {
            final SPacketChat packet = (SPacketChat) event.getPacket();
            final Date date = new Date();
            final String time = ChatFormatting.DARK_PURPLE + "<" + date.getHours() + ":" + (date.getMinutes() > 9 ? date.getMinutes() : "0" + date.getMinutes()) + "> \u00a7r";
            ((AccessorSPacketChat) packet).setChatComponent(new TextComponentString(time + packet.getChatComponent().getFormattedText()));
        }
    }

}
