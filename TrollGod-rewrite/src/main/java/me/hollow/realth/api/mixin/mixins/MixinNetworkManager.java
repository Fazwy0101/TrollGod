package me.hollow.realth.api.mixin.mixins;

import io.netty.channel.ChannelHandlerContext;
import me.hollow.realth.Realth;
import me.hollow.realth.api.utils.TickRate;
import me.hollow.realth.client.events.PacketEvent;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkManager.class)
public class    MixinNetworkManager {

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void sendPacket(final Packet<?> packet, final CallbackInfo ci) {
        if (packet instanceof CPacketChatMessage) {
            if (Realth.INSTANCE.getCommandManager().checkForCommand(((CPacketChatMessage) packet).getMessage())) {
                ci.cancel();
            }
        }
        if (MinecraftForge.EVENT_BUS.post(new PacketEvent.Send(packet))) {
            ci.cancel();
        }
    }

    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    public void receivePacket(final ChannelHandlerContext channelContext, final Packet<?> packet, final CallbackInfo ci) {
        if (packet instanceof SPacketTimeUpdate) {
            TickRate.update(packet);
            return;
        }
        if (packet instanceof SPacketEntityStatus) {
            Realth.INSTANCE.getPopManager().onEntityStatus((SPacketEntityStatus) packet);
            return;
        }
        if (MinecraftForge.EVENT_BUS.post(new PacketEvent.Receive(packet))) {
            ci.cancel();
        }
    }

    @Inject(method = "closeChannel", at = @At("HEAD"))
    public void closechanenl(ITextComponent message, CallbackInfo ci) {
        TickRate.reset();
    }

}
