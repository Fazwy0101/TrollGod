package me.hollow.realth.client.module.modules.player;

import me.hollow.realth.api.utils.Timer;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;

@ModuleManifest(label = "Heil", category = Module.Category.PLAYER)
public class Heil extends Module {
    public int heilY;
    private final Timer timer = new Timer();

    @Override
    public void onEnable() {
        heilY = 0;
    }

    public void setBiped(Entity entityIn, ModelBiped biped) {
            if (timer.sleep(1) && heilY > -190) {
                heilY -= 1;
            }
            biped.bipedRightArm.rotateAngleX = heilY * 0.01F;
            biped.bipedHead.rotateAngleX = -0.025F;
            biped.bipedRightArm.rotateAngleY = 0.15f;
            biped.bipedLeftArm.rotateAngleX = 0;
            if (heilY < 0) {
            if (timer.sleep(1)) heilY += 0.1;
            biped.bipedRightArm.rotateAngleX = heilY * 0.01F;
            biped.bipedHead.rotateAngleX = -0.025F;
            biped.bipedRightArm.rotateAngleY = 0.15f;
            biped.bipedLeftArm.rotateAngleX = 0;
        }
    }
}
