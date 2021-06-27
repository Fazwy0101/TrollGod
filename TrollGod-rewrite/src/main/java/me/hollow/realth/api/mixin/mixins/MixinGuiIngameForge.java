package me.hollow.realth.api.mixin.mixins;

import me.hollow.realth.client.module.modules.visual.Crosshair;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngameForge.class)
public class MixinGuiIngameForge {

    @Inject(method = "renderCrosshairs", at = @At("HEAD"), remap = false, cancellable = true)
    public void renderCrosshairs(float partialTicks, CallbackInfo ci) {
        if (Crosshair.INSTANCE.isEnabled()) {
            ci.cancel();
        }
    }

}
