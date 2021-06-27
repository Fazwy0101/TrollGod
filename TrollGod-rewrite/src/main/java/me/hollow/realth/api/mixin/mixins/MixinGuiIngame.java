package me.hollow.realth.api.mixin.mixins;

import me.hollow.realth.Realth;
import me.hollow.realth.client.module.modules.visual.RenderTweaks;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {

    private static final String[] KEYS = {
            "1", "2", "3", "4", "5", "6", "7", "8", "9"
    };

    @Inject(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderHelper;disableStandardItemLighting()V", shift = At.Shift.BEFORE))
    public void renderHotbar(ScaledResolution sr, float partialTicks, CallbackInfo ci) {
        if (RenderTweaks.INSTANCE.isEnabled() && RenderTweaks.INSTANCE.hotbarNumbers.getValue()) {
            for (int i = 0; i < 9; ++i) {
                Realth.INSTANCE.getFontManager().drawString(KEYS[i], sr.getScaledWidth() / 2f - 87 + i * 20, sr.getScaledHeight() - 18, 0xFFFFFF);
            }
        }
    }

}
