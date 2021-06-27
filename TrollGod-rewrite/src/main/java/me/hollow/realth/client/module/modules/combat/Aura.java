package me.hollow.realth.client.module.modules.combat;

import me.hollow.realth.api.property.Value;
import me.hollow.realth.api.utils.EntityUtil;
import me.hollow.realth.api.utils.ItemUtil;
import me.hollow.realth.client.events.UpdateEvent;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleManifest(label = "Aura", category = Module.Category.COMBAT)
public class Aura extends Module {

    private final Value<Float> range = new Value<>("Range", 6F, 0F, 6F);
    private final Value<Sword> sword = new Value<>("Sword", Sword.REQUIRE);

    @Override
    public void onEnable() {
        setSuffix("Single");
    } //Oliver is making up words

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        final EntityPlayer target = EntityUtil.getClosestPlayer(range.getValue());
        if (target == null) {
            return;
        }

        switch (sword.getValue()) {
            case REQUIRE:
                if (!(mc.player.getHeldItemMainhand().getItem() instanceof ItemSword)) {
                    return;
                }
                break;
            case SWITCH:
                if (mc.player.getHeldItemMainhand().getItem().getClass() != ItemSword.class) {
                    final int swordSlot = ItemUtil.getItemFromHotbar(ItemSword.class);
                    if (swordSlot == -1) {
                        return;
                    }
                    mc.player.inventory.currentItem = swordSlot;
                    mc.getConnection().sendPacket(new CPacketHeldItemChange(swordSlot));
                    break;
                }
            default:
                break;
        }

        if (mc.player.getCooledAttackStrength(0) >= 1.0F) {
            mc.playerController.attackEntity(mc.player, target);
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }

    public enum Sword {
        REQUIRE,
        SWITCH,
        NONE
    }

}
