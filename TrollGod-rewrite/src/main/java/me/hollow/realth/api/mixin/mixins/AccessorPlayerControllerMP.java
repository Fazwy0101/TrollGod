package me.hollow.realth.api.mixin.mixins;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerControllerMP.class)
public interface AccessorPlayerControllerMP {

    @Accessor
    float getCurBlockDamageMP();

    @Accessor
    void setIsHittingBlock(boolean hitting);

    @Accessor("blockHitDelay")
    void setBlockHitDelay(int delay);

}
