package me.hollow.realth.api.mixin.mixins;

import me.hollow.realth.Realth;
import me.hollow.realth.client.module.modules.visual.BetterChat;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiNewChat.class)
public class MixinGuiNewChat {

    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V"))
    public void drawRect(int left, int top, int right, int bottom, int color) {
        if (BetterChat.INSTANCE.isEnabled() && BetterChat.INSTANCE.noRect.getValue()) {
            return;
        }
        Gui.drawRect(left, top, right, bottom, color);
    }

    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"))
    public int drawString(FontRenderer fontRenderer, String text, float x, float y, int color) {
        if (BetterChat.INSTANCE.isEnabled() && BetterChat.INSTANCE.font.getValue()) {
            Realth.INSTANCE.getFontManager().drawString(text, x, y, color);
            return 0;
        }
        return fontRenderer.drawStringWithShadow(text, x, y, color);
    }

}
