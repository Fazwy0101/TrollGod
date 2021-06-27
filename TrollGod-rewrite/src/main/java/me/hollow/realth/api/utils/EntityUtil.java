package me.hollow.realth.api.utils;

import me.hollow.realth.Realth;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;

public class EntityUtil {

    private final static Minecraft mc = Minecraft.getMinecraft();

    public static EntityPlayer getClosestPlayer(final float range) {
        EntityPlayer player = null;
        double maxDistance = 999F;
        final int size = mc.world.playerEntities.size();
        for (int i = 0; i < size; ++i) {
            final EntityPlayer entityPlayer = mc.world.playerEntities.get(i);
            if (isPlayerValid(entityPlayer, range)) {
                final double distanceSq = mc.player.getDistanceSq(entityPlayer);
                if (maxDistance == 999F || distanceSq < maxDistance) {
                    maxDistance = distanceSq;
                    player = entityPlayer;
                }
            }
        }
        return player;
    }


    public static boolean isPlayerValid(final EntityPlayer player, final float range) {
        return player != mc.player && mc.player.getDistance(player) < range && !isDead(player) && !Realth.INSTANCE.getFriendManager().isFriend(player.getName());
    }

    public static boolean isDead(final EntityLivingBase entity) {
        return entity.isDead || entity.getHealth() <= 0;
    }

    public static float getHealth(final EntityLivingBase entity) {
        return entity.getHealth() + entity.getAbsorptionAmount();
    }

    public static float calculate(final double posX, final double posY, final double posZ, final EntityLivingBase entity) {
        final double distancedsize = entity.getDistance(posX, posY, posZ) / 12.0;
        final Vec3d vec3d = new Vec3d(posX, posY, posZ);
        final double blockDensity = getBlockDensity(vec3d, entity.getEntityBoundingBox());
        final double v = (1.0 - distancedsize) * blockDensity;
        final float damage = (int) ((v * v + v) / 2.0F * 7.0F * 12.0F + 1.0F);
        return getBlastReduction(entity, getDamageMultiplied(damage), new Explosion(mc.world, null, posX, posY, posZ, 6.0f, false, true));
    }

    private static float getBlastReduction(final EntityLivingBase entity, final float damageI, final Explosion explosion) {
        float damage = damageI;
        if (entity instanceof EntityPlayer) {
            final EntityPlayer ep = (EntityPlayer) entity;
            final DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) ep.getTotalArmorValue(), (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            final int k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            final float f = MathHelper.clamp((float) k, 0.0f, 20.0f);
            damage *= 1.0f - f / 25.0f;
            if (entity.isPotionActive(MobEffects.RESISTANCE)) {
                damage -= damage / 4.0f;
            }
            return damage;
        }
        damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        return damage;
    }

    private static float getDamageMultiplied(final float damage) {
        final int diff = mc.world.getDifficulty().getId();
        return damage * (diff == 0 ? 0.0f : (diff == 2 ? 1.0f : (diff == 1 ? 0.5f : 1.5f)));
    }

    public static float getBlockDensity(final Vec3d vec, final AxisAlignedBB bb) {
        final double d0 = 1.0D / ((bb.maxX - bb.minX) * 2.0D + 1.0D);
        final double d1 = 1.0D / ((bb.maxY - bb.minY) * 2.0D + 1.0D);
        final double d2 = 1.0D / ((bb.maxZ - bb.minZ) * 2.0D + 1.0D);
        final double d3 = (1.0D - Math.floor(1.0D / d0) * d0) / 2.0D;
        final double d4 = (1.0D - Math.floor(1.0D / d2) * d2) / 2.0D;

        if (d0 >= 0.0D && d1 >= 0.0D && d2 >= 0.0D) {
            int j2 = 0;
            int k2 = 0;

            for (float f = 0.0F; f <= 1.0F; f = (float) ((double) f + d0)) {
                for (float f1 = 0.0F; f1 <= 1.0F; f1 = (float) ((double) f1 + d1)) {
                    for (float f2 = 0.0F; f2 <= 1.0F; f2 = (float) ((double) f2 + d2)) {
                        final double d5 = bb.minX + (bb.maxX - bb.minX) * (double) f;
                        final double d6 = bb.minY + (bb.maxY - bb.minY) * (double) f1;
                        final double d7 = bb.minZ + (bb.maxZ - bb.minZ) * (double) f2;

                        if (rayTraceBlocks(new Vec3d(d5 + d3, d6, d7 + d4), vec) == null) {
                            ++j2;
                        }

                        ++k2;
                    }
                }
            }

            return (float) j2 / (float) k2;
        } else {
            return 0.0F;
        }
    }

    public static RayTraceResult rayTraceBlocks(Vec3d vec31, Vec3d vec32) {
        final int i = MathHelper.floor(vec32.x);
        final int j = MathHelper.floor(vec32.y);
        final int k = MathHelper.floor(vec32.z);
        int l = MathHelper.floor(vec31.x);
        int i1 = MathHelper.floor(vec31.y);
        int j1 = MathHelper.floor(vec31.z);
        BlockPos blockpos = new BlockPos(l, i1, j1);
        IBlockState iblockstate = mc.world.getBlockState(blockpos);
        Block block = iblockstate.getBlock();

        if (block.canCollideCheck(iblockstate, false) && block != Blocks.WEB) {
            return iblockstate.collisionRayTrace(mc.world, blockpos, vec31, vec32);
        }

        int k1 = 200;
        final double d6 = vec32.x - vec31.x;
        final double d7 = vec32.y - vec31.y;
        final double d8 = vec32.z - vec31.z;
        while (k1-- >= 0) {
            if (l == i && i1 == j && j1 == k) {
                return null;
            }

            boolean flag2 = true;
            boolean flag = true;
            boolean flag1 = true;
            double d0 = 999.0D;
            double d1 = 999.0D;
            double d2 = 999.0D;

            if (i > l) {
                d0 = (double) l + 1.0D;
            } else if (i < l) {
                d0 = (double) l + 0.0D;
            } else {
                flag2 = false;
            }

            if (j > i1) {
                d1 = (double) i1 + 1.0D;
            } else if (j < i1) {
                d1 = (double) i1 + 0.0D;
            } else {
                flag = false;
            }

            if (k > j1) {
                d2 = (double) j1 + 1.0D;
            } else if (k < j1) {
                d2 = (double) j1 + 0.0D;
            } else {
                flag1 = false;
            }

            double d3 = 999.0D;
            double d4 = 999.0D;
            double d5 = 999.0D;

            if (flag2) {
                d3 = (d0 - vec31.x) / d6;
            }

            if (flag) {
                d4 = (d1 - vec31.y) / d7;
            }

            if (flag1) {
                d5 = (d2 - vec31.z) / d8;
            }

            if (d3 == -0.0D) {
                d3 = -1.0E-4D;
            }

            if (d4 == -0.0D) {
                d4 = -1.0E-4D;
            }

            if (d5 == -0.0D) {
                d5 = -1.0E-4D;
            }

            EnumFacing enumfacing;

            if (d3 < d4 && d3 < d5) {
                enumfacing = i > l ? EnumFacing.WEST : EnumFacing.EAST;
                vec31 = new Vec3d(d0, vec31.y + d7 * d3, vec31.z + d8 * d3);
            } else if (d4 < d5) {
                enumfacing = j > i1 ? EnumFacing.DOWN : EnumFacing.UP;
                vec31 = new Vec3d(vec31.x + d6 * d4, d1, vec31.z + d8 * d4);
            } else {
                enumfacing = k > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                vec31 = new Vec3d(vec31.x + d6 * d5, vec31.y + d7 * d5, d2);
            }

            l = MathHelper.floor(vec31.x) - (enumfacing == EnumFacing.EAST ? 1 : 0);
            i1 = MathHelper.floor(vec31.y) - (enumfacing == EnumFacing.UP ? 1 : 0);
            j1 = MathHelper.floor(vec31.z) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
            blockpos = new BlockPos(l, i1, j1);
            iblockstate = mc.world.getBlockState(blockpos);
            block = iblockstate.getBlock();

            if (block.canCollideCheck(iblockstate, false) && block != Blocks.WEB) {
                return iblockstate.collisionRayTrace(mc.world, blockpos, vec31, vec32);
            }
        }
        return null;
    }

}
