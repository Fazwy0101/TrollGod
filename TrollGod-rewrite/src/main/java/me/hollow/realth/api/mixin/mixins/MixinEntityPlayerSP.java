package me.hollow.realth.api.mixin.mixins;

import com.mojang.authlib.GameProfile;
import me.hollow.realth.client.events.MoveEvent;
import me.hollow.realth.client.events.UpdateEvent;
import me.hollow.realth.client.module.modules.misc.AntiPush;
import me.hollow.realth.client.module.modules.misc.Swing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {

    @Shadow
    protected Minecraft mc;

    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }

    @Override
    public void move(MoverType type, double x, double y, double z) {
        final MoveEvent event = new MoveEvent(x, y, z);
        MinecraftForge.EVENT_BUS.post(event);
        super.move(type, event.getMotionX(), event.getMotionY(), event.getMotionZ());
    }

    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    public void pushOutOfBlocks(double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (AntiPush.INSTANCE.isEnabled()) {
            cir.cancel();
        }
    }

    private boolean wasLastOffhand;

    @Inject(method = "swingArm", at = @At("HEAD"), cancellable = true)
    public void swingArm(EnumHand hand, CallbackInfo info) {
        if (Swing.INSTANCE.isEnabled()) {
            info.cancel();
            switch (Swing.INSTANCE.mode.getValue()) {
                case PACKET:
                    mc.getConnection().sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                    break;
                case OFFHAND:
                    super.swingArm(EnumHand.OFF_HAND);
                    break;
                default:
                    super.swingArm(wasLastOffhand ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
                    wasLastOffhand = !wasLastOffhand;
                    break;
            }
        }
    }

    @Inject(method = "onUpdate", at = @At("HEAD"))
    public void onUpdateWalking(CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new UpdateEvent(UpdateEvent.Stage.PRE));
    }

    @Inject(method = "onUpdate", at = @At("RETURN"))
    public void onUpdateWalkingPost(CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new UpdateEvent(UpdateEvent.Stage.POST));
    }

}
