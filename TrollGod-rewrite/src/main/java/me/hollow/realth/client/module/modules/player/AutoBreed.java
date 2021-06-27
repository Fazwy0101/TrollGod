package me.hollow.realth.client.module.modules.player;

import me.hollow.realth.client.events.UpdateEvent;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleManifest(label = "AutoBreed", category = Module.Category.PLAYER)
public class AutoBreed extends Module {

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
            for (Entity e : mc.world.loadedEntityList) {
                if (e != null && e instanceof EntityAnimal) {
                    final EntityAnimal animal = (EntityAnimal) e;
                    if (animal.getHealth() > 0) {
                        if (!animal.isChild() && !animal.isInLove() && mc.player.getDistance(animal) <= 4.5f && animal.isBreedingItem(mc.player.inventory.getCurrentItem())) {
                            mc.playerController.interactWithEntity(mc.player, animal, EnumHand.MAIN_HAND);
                        }
                    }
                }
            }
        }
    }