package me.hollow.realth.api.mixin.mixins;

import me.hollow.realth.api.mixin.accessors.IMinecraft;
import me.hollow.realth.client.events.ResizeEvent;
import me.hollow.realth.client.module.modules.misc.FPSLimit;
import me.hollow.realth.client.module.modules.misc.MiddleClick;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft implements IMinecraft {

    @Accessor("rightClickDelayTimer")
    public abstract void setDelay(int delay);

    @Inject(method = "resize", at = @At("RETURN"))
    public void resize(int width, int height, CallbackInfo ci) {
        if (Minecraft.getMinecraft().player == null)
            return;
        MinecraftForge.EVENT_BUS.post(new ResizeEvent());
    }

    @Inject(method = "middleClickMouse", at = @At("HEAD"))
    public void middleClick(CallbackInfo ci) {
        if (MiddleClick.INSTANCE.isEnabled()) {
            MiddleClick.INSTANCE.onMouse();
        }
    }
    
    @Inject(method = "getLimitFramerate", at = @At("HEAD"), cancellable = true)
    public void getLimitFramerate(CallbackInfoReturnable<Integer> cir) {
        if (FPSLimit.INSTANCE.isEnabled() && !Display.isActive()) {
            cir.setReturnValue(FPSLimit.INSTANCE.limit.getValue());
        }
    }

}
