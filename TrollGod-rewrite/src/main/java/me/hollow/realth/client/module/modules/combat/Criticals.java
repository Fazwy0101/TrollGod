package me.hollow.realth.client.module.modules.combat;

import me.hollow.realth.api.mixin.mixins.AccessorEntity;
import me.hollow.realth.api.property.Value;
import me.hollow.realth.client.events.PacketEvent;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleManifest(label = "Criticals", category = Module.Category.COMBAT)
public class Criticals extends Module {

    private final Value<Boolean> sneak = new Value<>("Sneak", true);

    {
        setSuffix("Packet");
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketUseEntity) {
            final CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();
            if (packet.getAction() == CPacketUseEntity.Action.ATTACK) {
                if (packet.getEntityFromWorld(mc.world) instanceof EntityLivingBase && mc.player.onGround && !((AccessorEntity) mc.player).getIsInWeb()) {
                    boolean shouldSneak = sneak.getValue() && mc.player.isInLava() && !mc.player.isSneaking();
                    if (shouldSneak) {
                        mc.player.setSneaking(true);
                    }
                    mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0899131, mc.player.posZ, false));
                    mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                    if (shouldSneak) {
                        mc.player.setSneaking(false);
                    }
                }
            }
        }
    }

}
