package me.hollow.realth.api.utils;

import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static jdk.nashorn.internal.objects.NativeMath.round;

public class BlockUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void placeBlock(final BlockPos pos) {
        for (final EnumFacing side : EnumFacing.VALUES) {
            final BlockPos neighbor = pos.offset(side);
            final IBlockState neighborState = mc.world.getBlockState(neighbor);
            if (neighborState.getBlock().canCollideCheck(neighborState, false)) {
                final boolean sneak = !mc.player.isSneaking() && neighborState.getBlock().onBlockActivated(mc.world, pos, mc.world.getBlockState(pos), mc.player, EnumHand.MAIN_HAND, side, 0.5f, 0.5f, 0.5f);
                if (sneak) {
                    mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                }
                mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(neighbor, side.getOpposite(), EnumHand.MAIN_HAND, 0.5F, 0.5F, 0.5F));
                mc.getConnection().sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                if (sneak) {
                    mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                }
            }
        }
    }

    public static boolean canPlaceCrystal(final BlockPos pos, boolean checkSecond) {
        final Chunk chunk = mc.world.getChunk(pos);
        final Block block = chunk.getBlockState(pos).getBlock();
        if (block != Blocks.BEDROCK && block != Blocks.OBSIDIAN) {
            return false;
        }

        final BlockPos boost = pos.offset(EnumFacing.UP, 1);
        if (chunk.getBlockState(boost).getBlock() != Blocks.AIR || chunk.getBlockState(pos.offset(EnumFacing.UP, 2)).getBlock() != Blocks.AIR) {
            return false;
        }

        return mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost.getX(), boost.getY(), boost.getZ(), boost.getX() + 1, boost.getY() + (checkSecond ? 2 : 1), boost.getZ() + 1), e -> !(e instanceof EntityEnderCrystal)).isEmpty();
    }

    public static List<BlockPos> getSphere(final float radius) {
        final List<BlockPos> sphere = new ArrayList<>();
        final BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        final int posX = pos.getX();
        final int posY = pos.getY();
        final int posZ = pos.getZ();
        for (int x = posX - (int) radius; x <= posX + radius; ++x) {
            for (int z = posZ - (int) radius; z <= posZ + radius; ++z) {
                for (int y = posY - (int) radius; y < posY + radius; ++y) {
                    if ((posX - x) * (posX - x) + (posZ - z) * (posZ - z) + (posY - y) * (posY - y) < radius * radius) {
                        sphere.add(new BlockPos(x, y, z));
                    }
                }
            }
        }
        return sphere;
    }

    public static int isPositionPlaceable(BlockPos pos, boolean entityCheck) {
        try {
            final Block block = mc.world.getBlockState(pos).getBlock();
            if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid) && !(block instanceof BlockTallGrass) && !(block instanceof BlockFire) && !(block instanceof BlockDeadBush) && !(block instanceof BlockSnow)) {
                return 0;
            }

            if (entityCheck) {
                for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
                    if (!entity.isDead && !(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
                        return 1;
                    }
                }
            }

            for (EnumFacing side : getPossibleSides(pos)) {
                if (canBeClicked(pos.offset(side))) {
                    return 3;
                }
            }

            return 2;
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public static boolean canBeClicked(BlockPos pos) {
        return getBlock(pos).canCollideCheck(getState(pos), false);
    }

    private static Block getBlock(BlockPos pos) {
        return getState(pos).getBlock();
    }

    private static IBlockState getState(BlockPos pos) {
        return mc.world.getBlockState(pos);
    }

    public static List<EnumFacing> getPossibleSides(BlockPos pos) {
        final List<EnumFacing> facings = new ArrayList<>(6);
        for (EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbour = pos.offset(side);
            if (mc.world.getBlockState(neighbour).getBlock().canCollideCheck(mc.world.getBlockState(neighbour), false)) {
                final IBlockState blockState = mc.world.getBlockState(neighbour);
                if (!blockState.getMaterial().isReplaceable()) {
                    facings.add(side);
                }
            }
        }
        return facings;
    }

    public static Vec3d[] getHelpingBlocks(Vec3d vec3d) {
        return new Vec3d[] {
                new Vec3d(vec3d.x, vec3d.y - 1, vec3d.z),
                new Vec3d(vec3d.x != 0 ? vec3d.x * 2 : vec3d.x, vec3d.y, vec3d.x != 0 ? vec3d.z : vec3d.z * 2),
                new Vec3d(vec3d.x == 0 ? vec3d.x + 1 : vec3d.x, vec3d.y, vec3d.x == 0 ? vec3d.z : vec3d.z + 1),
                new Vec3d(vec3d.x == 0 ? vec3d.x - 1 : vec3d.x, vec3d.y, vec3d.x == 0 ? vec3d.z : vec3d.z - 1),
                new Vec3d(vec3d.x, vec3d.y + 1, vec3d.z)
        };
    }

    public static List<Vec3d> getOffsetList(int y, boolean floor) {
        final List<Vec3d> offsets = new ArrayList<>(5);
        offsets.add(new Vec3d(-1, y, 0));
        offsets.add(new Vec3d(1, y, 0));
        offsets.add(new Vec3d(0, y, -1));
        offsets.add(new Vec3d(0, y, 1));

        if(floor) {
            offsets.add(new Vec3d(0, y - 1, 0));
        }

        return offsets;
    }

    public static Vec3d[] getOffsets(int y, boolean floor) {
        final List<Vec3d> offsets = getOffsetList(y, floor);
        final Vec3d[] array = new Vec3d[offsets.size()];
        return offsets.toArray(array);
    }

    public static Vec3d[] getUnsafeBlockArray(Entity entity, int height, boolean floor) {
        final List<Vec3d> list = getUnsafeBlocks(entity, height, floor);
        final Vec3d[] array = new Vec3d[list.size()];
        return list.toArray(array);
    }

    public static boolean isSafe(Entity entity, int height, boolean floor) {
        return getUnsafeBlocks(entity, height, floor).size() == 0;
    }

    public static List<Vec3d> getUnsafeBlocks(Entity entity, int height, boolean floor) {
        return getUnsafeBlocksFromVec3d(entity.getPositionVector(), height, floor);
    }

    public static List<Vec3d> getUnsafeBlocksFromVec3d(Vec3d pos, int height, boolean floor) {
        final List<Vec3d> vec3ds = new ArrayList<>(5);
        for (final Vec3d vector : BlockUtil.getOffsets(height, floor)) {
            final Block block = mc.world.getBlockState(new BlockPos(pos).add(vector.x, vector.y, vector.z)).getBlock();
            if (block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockTallGrass || block instanceof BlockFire || block instanceof BlockDeadBush || block instanceof BlockSnow) {
                vec3ds.add(vector);
            }
        }
        return vec3ds;
    }

    public static Vec3d[] getTrapOffsets(boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
        final List<Vec3d> offsets = getTrapOffsetsList(antiScaffold, antiStep, legs, platform, antiDrop);
        final Vec3d[] array = new Vec3d[offsets.size()];
        return offsets.toArray(array);
    }

    public static List<Vec3d> getTrapOffsetsList(boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
        final List<Vec3d> offsets = new ArrayList<>(getOffsetList(1, false));
        offsets.add(new Vec3d(0, 2, 0));
        if(antiScaffold) {
            offsets.add(new Vec3d(0, 3, 0));
        }
        if(antiStep) {
            offsets.addAll(getOffsetList(2, false));
        }
        if(legs) {
            offsets.addAll(getOffsetList(0, false));
        }
        if(platform) {
            offsets.addAll(getOffsetList(-1, false));
            offsets.add(new Vec3d(0, -1, 0));
        }
        if(antiDrop) {
            offsets.add(new Vec3d(0, -2, 0));
        }
        return offsets;
    }

    public static final Vec3d[] antiDropOffsetList = {
            new Vec3d(0, -2, 0),
    };

    public static final Vec3d[] platformOffsetList = {
            new Vec3d(0, -1, 0),
            new Vec3d(0, -1, -1),
            new Vec3d(0, -1, 1),
            new Vec3d(-1, -1, 0),
            new Vec3d(1, -1, 0)
    };

    public static final Vec3d[] legOffsetList = {
            new Vec3d(-1, 0, 0),
            new Vec3d(1, 0, 0),
            new Vec3d(0, 0, -1),
            new Vec3d(0, 0, 1)
    };

    public static final Vec3d[] OffsetList = {
            new Vec3d(1, 1, 0),
            new Vec3d(-1, 1, 0),
            new Vec3d(0, 1, 1),
            new Vec3d(0, 1, -1),
            new Vec3d(0, 2, 0),
            //new Vec3d(0, 2, -1)
    };

    public static final Vec3d[] antiStepOffsetList = {
            new Vec3d(-1, 2, 0),
            new Vec3d(1, 2, 0),
            new Vec3d(0, 2, 1),
            new Vec3d(0, 2, -1),
    };

    public static final Vec3d[] antiScaffoldOffsetList = {
            new Vec3d(0, 3, 0)
    };

    public static boolean isTrapped(EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
        return getUntrappedBlocks(player, antiScaffold, antiStep, legs, platform, antiDrop).size() == 0;
    }

    public static boolean isTrappedExtended(int extension, EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean raytrace) {
        return getUntrappedBlocksExtended(extension, player, antiScaffold, antiStep, legs, platform, antiDrop, raytrace).size() == 0;
    }

    public static List<Vec3d> getBlockBlocks(Entity entity) {
        final List<Vec3d> vec3ds = new ArrayList<>(8);
        final AxisAlignedBB bb = entity.getEntityBoundingBox();
        final double y = entity.posY;
        final double minX = round(bb.minX, 0);
        final double minZ = round(bb.minZ, 0);
        final double maxX = round(bb.maxX, 0);
        final double maxZ = round(bb.maxZ, 0);
        if(minX != maxX) {
            vec3ds.add(new Vec3d(minX, y, minZ));
            vec3ds.add(new Vec3d(maxX, y, minZ));
            if(minZ != maxZ) {
                vec3ds.add(new Vec3d(minX, y, maxZ));
                vec3ds.add(new Vec3d(maxX, y, maxZ));
                return vec3ds;
            }
        } else if(minZ != maxZ) {
            vec3ds.add(new Vec3d(minX, y, minZ));
            vec3ds.add(new Vec3d(minX, y, maxZ));
            return vec3ds;
        }
        vec3ds.add(entity.getPositionVector());
        return vec3ds;
    }

    public static List<Vec3d> getUntrappedBlocksExtended(int extension, EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean raytrace) {
        final List<Vec3d> placeTargets = new ArrayList<>();
        if(extension == 1) {
            placeTargets.addAll(targets(player.getPositionVector(), antiScaffold, antiStep, legs, platform, antiDrop, raytrace));
        } else {
            int extend = 1;
            for (Vec3d vec3d : getBlockBlocks(player)) {
                if(extend > extension) {
                    break;
                }
                placeTargets.addAll(targets(vec3d, antiScaffold, antiStep, legs, platform, antiDrop, raytrace));
                extend++;
            }
        }

        List<Vec3d> removeList = new ArrayList<>();
        for(Vec3d vec3d : placeTargets) {
            BlockPos pos = new BlockPos(vec3d);
            if(BlockUtil.isPositionPlaceable(pos, raytrace) == -1) {
                removeList.add(vec3d);
            }
        }

        for(Vec3d vec3d : removeList) {
            placeTargets.remove(vec3d);
        }

        return placeTargets;
    }


    public static List<Vec3d> getUntrappedBlocks(EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
        final List<Vec3d> vec3ds = new ArrayList<>();
        if(!antiStep && getUnsafeBlocks(player, 2, false).size() == 4) {
            vec3ds.addAll(getUnsafeBlocks(player, 2, false));
        }
        final Vec3d[] trapOffsets = getTrapOffsets(antiScaffold, antiStep, legs, platform, antiDrop);
        for (int i = 0; i < trapOffsets.length; i++) {
            final Vec3d vector = trapOffsets[i];
            final BlockPos targetPos = new BlockPos(player.getPositionVector()).add(vector.x, vector.y, vector.z);
            final Block block = mc.world.getBlockState(targetPos).getBlock();
            if (block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockTallGrass || block instanceof BlockFire || block instanceof BlockDeadBush || block instanceof BlockSnow) {
                vec3ds.add(vector);
            }
        }
        return vec3ds;
    }

    public static List<Vec3d> targets(Vec3d vec3d, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean raytrace) {
        List<Vec3d> placeTargets = new ArrayList<>();
        if(antiDrop) {
            Collections.addAll(placeTargets, convertVec3ds(vec3d, antiDropOffsetList));
        }

        if(platform){
            Collections.addAll(placeTargets, convertVec3ds(vec3d, platformOffsetList));
        }

        if(legs){
            Collections.addAll(placeTargets, convertVec3ds(vec3d, legOffsetList));
        }

        Collections.addAll(placeTargets, convertVec3ds(vec3d, OffsetList));

        if(antiStep){
            Collections.addAll(placeTargets, convertVec3ds(vec3d, antiStepOffsetList));
        } else {
            List<Vec3d> vec3ds = getUnsafeBlocksFromVec3d(vec3d, 2, false);
            if(vec3ds.size() == 4) {
                for (Vec3d vector : vec3ds) {
                    BlockPos position = new BlockPos(vec3d).add(vector.x, vector.y, vector.z);
                    switch (BlockUtil.isPositionPlaceable(position, raytrace)) {
                        case 0:
                            break;
                        case -1:
                        case 1:
                        case 2:
                            continue;
                        case 3:
                            placeTargets.add(vec3d.add(vector));
                            break;
                    }
                    break;
                }
            }
        }

        if(antiScaffold){
            Collections.addAll(placeTargets, convertVec3ds(vec3d, antiScaffoldOffsetList));
        }
        return placeTargets;
    }

    public static Vec3d[] convertVec3ds(Vec3d vec3d, Vec3d[] input) {
        final Vec3d[] output = new Vec3d[input.length];
        final int length = input.length;
        for (int i = 0; i < length; i++) {
            output[i] = vec3d.add(input[i]);
        }
        return output;
    }

}
