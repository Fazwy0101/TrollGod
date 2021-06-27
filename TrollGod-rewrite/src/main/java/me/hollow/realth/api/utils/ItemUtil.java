package me.hollow.realth.api.utils;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;

public class ItemUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static boolean isArmorUnderPercent(EntityPlayer player, float percent) {
        for (int i = 3; i >= 0; --i) {
            final ItemStack stack = player.inventory.armorInventory.get(i);
            if (getDamageInPercent(stack) < percent) {
                return true;
            }
        }
        return false;
    }

    public static int getItemFromHotbar(Class<?> clazz) {
        int slot = -1;

        for (int i = 8; i >= 0; --i) {
            if (mc.player.inventory.getStackInSlot(i).getItem().getClass() == clazz) {
                slot = i;
                break;
            }
        }

        return slot;
    }

    public static int getItemFromHotbar(Item item) {
        int slot = -1;

        for (int i = 8; i >= 0; --i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == item) {
                slot = i;
                break;
            }
        }

        return slot;
    }

    public static int getBlockFromHotbar(Block block) {
        int slot = -1;

        for (int i = 8; i >= 0; --i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Item.getItemFromBlock(block)) {
                slot = i;
                break;
            }
        }

        return slot;
    }

    public static int getItemSlot(Item item) {
        int slot = -1;

        for (int i = 44; i >= 0; --i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == item) {
                if (i < 9) {
                    i += 36;
                }
                slot = i;
                break;
            }
        }

        return slot;
    }

    public static int getItemCount(Item item) {
        int count = 0;

        for (int i = 44; i >= 0; --i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() == item) {
                count += stack.getCount();
            }
        }

        if (mc.player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND).getItem() == item) {
            count += mc.player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND).getCount();
        }

        return count;
    }

    public static int getRoundedDamage(ItemStack stack) {
        return (int)getDamageInPercent(stack);
    }

    public static boolean hasDurability(ItemStack stack) {
        final Item item = stack.getItem();
        return item instanceof ItemArmor || item instanceof ItemSword || item instanceof ItemTool || item instanceof ItemShield;
    }

    public static float getDamageInPercent(ItemStack stack) {
        float green = ((float)stack.getMaxDamage() - (float)stack.getItemDamage()) / (float)stack.getMaxDamage();
        float red = 1.0f - green;
        return 100 - (int)(red * 100.0f);
    }

}
