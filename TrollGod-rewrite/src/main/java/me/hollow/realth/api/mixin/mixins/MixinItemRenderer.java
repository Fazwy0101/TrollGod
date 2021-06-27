package me.hollow.realth.api.mixin.mixins;

import me.hollow.realth.client.module.modules.visual.ViewmodelChanger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {

    @Final
    @Shadow
    private Minecraft mc;

    @Inject(method = "renderItemSide", at = @At("HEAD"))
    public void renderItemSid(EntityLivingBase entitylivingbaseIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform, boolean leftHanded, CallbackInfo ci) {
        if (ViewmodelChanger.INSTANCE.isEnabled()) {
            if (transform == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND) {
                GlStateManager.scale(ViewmodelChanger.INSTANCE.scaleX.getValue()/ 10, ViewmodelChanger.INSTANCE.scaleY.getValue()/ 10, ViewmodelChanger.INSTANCE.scaleZ.getValue()/ 10);
                if (mc.player.getActiveHand() == EnumHand.MAIN_HAND && mc.player.isHandActive())
                    return;
                GlStateManager.translate(ViewmodelChanger.INSTANCE.offsetX.getValue(), ViewmodelChanger.INSTANCE.offsetY.getValue(), ViewmodelChanger.INSTANCE.offsetZ.getValue());
            } else if (transform == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND) {
                GlStateManager.scale(ViewmodelChanger.INSTANCE.scaleX.getValue()/ 10, ViewmodelChanger.INSTANCE.scaleY.getValue()/ 10, ViewmodelChanger.INSTANCE.scaleZ.getValue() / 10);
                if (mc.player.getActiveHand() == EnumHand.OFF_HAND && mc.player.isHandActive())
                    return;
                GlStateManager.translate(-ViewmodelChanger.INSTANCE.offsetX.getValue(), ViewmodelChanger.INSTANCE.offhandY.getValue(), ViewmodelChanger.INSTANCE.offsetZ.getValue());
            }
        }
    }

}
