package me.hollow.realth.client.other;

import me.hollow.realth.api.utils.EntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;

public class SafetyManager {

    private final Minecraft mc = Minecraft.getMinecraft();

    private boolean safe;

    public void update() {
        safe = true;
        final int size = mc.world.loadedEntityList.size();
        for (int i = 0; i < size; ++i) {
            final Entity entity = mc.world.loadedEntityList.get(i);
            if (entity instanceof EntityEnderCrystal) {
                if (!entity.isDead && mc.player.getDistanceSq(entity) < 100) {
                    if (EntityUtil.calculate(entity.posX, entity.posY, entity.posZ, mc.player) + 2 > mc.player.getHealth() + mc.player.getAbsorptionAmount()) {
                        safe = false;
                        break;
                    }
                }
            }
        }
    }

    public boolean isSafe() {
        return safe;
    }

}
