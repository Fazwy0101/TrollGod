package me.hollow.realth.api.mixin.mixins;

import me.hollow.realth.api.utils.CapeUtil;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer {

    @Shadow
    protected abstract NetworkPlayerInfo getPlayerInfo();

    @Inject(method = "getLocationCape", at = @At("HEAD"), cancellable = true)
    public void getLocationCape(CallbackInfoReturnable<ResourceLocation> callbackInfoReturnable) {
        final NetworkPlayerInfo playerInfo = getPlayerInfo();
        if (playerInfo != null) {
            final ResourceLocation location = CapeUtil.getResourceLocation(playerInfo.getGameProfile().getId());
            if (location != null) {
                callbackInfoReturnable.setReturnValue(location);
            }
        }
    }

}
