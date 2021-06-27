package me.hollow.realth.api.mixin.mixins;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelEnderCrystal;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RenderEnderCrystal.class)
public class MixinRenderEnderCrystal {

    private final ModelBase modelEnderCrystal = new ModelEnderCrystal(0.0F, false);

}
