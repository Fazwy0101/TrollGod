package me.hollow.realth.api.mixin.mixins;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.hollow.realth.Realth;
import me.hollow.realth.client.module.modules.misc.ExtraTab;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(GuiPlayerTabOverlay.class)
public class MixinGuiPlayerTabOverlay {

    @Redirect(method = "renderPlayerlist", at = @At(value = "INVOKE", target = "Ljava/util/List;subList(II)Ljava/util/List;"))
    public List<?> sublist(List<?> list, int fromIndex, int toIndex) {
        return list.subList(fromIndex, ExtraTab.INSTANCE.isEnabled() ? 999 : toIndex);
    }

    @Inject(method = "getPlayerName", at = @At("HEAD"), cancellable = true)
    private void getPlayerName(NetworkPlayerInfo networkPlayerInfoIn, CallbackInfoReturnable<String> cir) {
        if (ExtraTab.INSTANCE.isEnabled() && networkPlayerInfoIn.getDisplayName() != null) {
            String name = networkPlayerInfoIn.getDisplayName().getUnformattedText();
            if (Realth.INSTANCE.getFriendManager().isFriend(name)) {
                cir.cancel();
                cir.setReturnValue(ChatFormatting.GREEN + name);
            }
        }
    }

}
