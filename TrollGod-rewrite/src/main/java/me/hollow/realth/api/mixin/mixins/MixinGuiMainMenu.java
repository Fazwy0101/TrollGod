package me.hollow.realth.api.mixin.mixins;

import me.hollow.realth.client.events.ResizeEvent;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMainMenu.class)
public class MixinGuiMainMenu extends GuiScreen {

    boolean init;

    @Inject(method = "drawPanorama", at = @At("RETURN"))
    public void drawPanorama(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (!init) {
            MinecraftForge.EVENT_BUS.post(new ResizeEvent());
            init = false;
        }
    }

    @Inject(method = "addSingleplayerMultiplayerButtons", at = @At("RETURN"))
    public void addButton(int p_73969_1_, int p_73969_2_, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new ResizeEvent());
        this.buttonList.add(new GuiButton(-1, 2, 20, mc.fontRenderer.getStringWidth("Elite") + 10, 20, "Elite"));
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"))
    public void actionPerformed(GuiButton button, CallbackInfo ci) {
        if (button.id == -1) {
            mc.displayGuiScreen(new GuiConnecting(this, mc, "eliteanarchy.org", 25565));
        }
    }

}
