package me.hollow.realth.client.module.modules.player;

import me.hollow.realth.client.events.UpdateEvent;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.ItemShears;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleManifest(label = "AutoShear", category = Module.Category.PLAYER)
public class AutoSheer extends Module {

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
            if (mc.player.inventory.getCurrentItem().getItem() instanceof ItemShears) {
                for (Entity e : mc.world.loadedEntityList) {
                    if (e != null && e instanceof EntitySheep) {
                        final EntitySheep sheep = (EntitySheep) e;
                        if (sheep.getHealth() > 0) {
                            if (!sheep.isChild() && !sheep.getSheared() && mc.player.getDistance(sheep) <= 4.5f) {
                                mc.playerController.interactWithEntity(mc.player, sheep, EnumHand.MAIN_HAND);
                            }
                        }
                    }
                }
            }
        }
    }
