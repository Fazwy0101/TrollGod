package me.hollow.realth.client.module.modules.visual;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.hollow.realth.Realth;
import me.hollow.realth.api.mixin.mixins.AccessorRenderManager;
import me.hollow.realth.api.property.Value;
import me.hollow.realth.api.utils.EntityUtil;
import me.hollow.realth.api.utils.RenderUtil;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagList;
import org.lwjgl.opengl.GL11;

import java.util.Map;
import java.util.Objects;

@ModuleManifest(label = "Nametags", category = Module.Category.VISUAL, listenable = false)
public class  Nametags extends Module {

    private final Value<Boolean> armor = new Value<>("Armor", true);
    private final Value<Boolean> ping = new Value<>("Ping", true);
    private final Value<Boolean> totemPops = new Value<>("TotemPops", true);
    private final Value<Boolean> rect = new Value<>("Rect", true);
    private final Value<Float> scaling = new Value<>("Size", 5.9f, 0.1f, 20.0f);
    private final Value<Float> factor = new Value<>("Factor", 0.5f, 0.1f, 1.0f);

    public static Nametags INSTANCE;

    private final ICamera camera = new Frustum();
    private final AccessorRenderManager renderManager = (AccessorRenderManager) mc.getRenderManager();

    public Nametags() {
        INSTANCE = this;
    }

    @Override
    public void onRender3D() {
        if (mc.getRenderViewEntity() == null) {
            return;
        }
        camera.setPosition(mc.getRenderViewEntity().posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
        float partialTicks = mc.getRenderPartialTicks();
        final int size = mc.world.playerEntities.size();
        for (int i = 0; i < size; ++i) {
            final EntityPlayer player = mc.world.playerEntities.get(i);
            if (camera.isBoundingBoxInFrustum(player.getEntityBoundingBox())) {
                if (player != mc.player && !player.isDead && player.getHealth() > 0) {
                    final double x = interpolate(player.lastTickPosX, player.posX, partialTicks) - renderManager.getRenderPosX();
                    final double y = interpolate(player.lastTickPosY, player.posY, partialTicks) - renderManager.getRenderPosY();
                    final double z = interpolate(player.lastTickPosZ, player.posZ, partialTicks) - renderManager.getRenderPosZ();

                    double tempY = y;
                    tempY += player.isSneaking() ? 0.5D : 0.7D;
                    final Entity camera = mc.getRenderViewEntity();
                    final double originalPositionX = camera.posX;
                    final double originalPositionY = camera.posY;
                    final double originalPositionZ = camera.posZ;
                    camera.posX = interpolate(camera.prevPosX, camera.posX, partialTicks);
                    camera.posY = interpolate(camera.prevPosY, camera.posY, partialTicks);
                    camera.posZ = interpolate(camera.prevPosZ, camera.posZ, partialTicks);

                    final String displayTag = getDisplayTag(player);
                    final double distance = camera.getDistance(x + mc.getRenderManager().viewerPosX, y + mc.getRenderManager().viewerPosY, z + mc.getRenderManager().viewerPosZ);
                    final int width = Realth.INSTANCE.getFontManager().getStringWidth(displayTag) >> 1; // >> 1 is the same as / 2 but way faster
                    double scale = (0.0018 + scaling.getValue() * (distance * factor.getValue())) / 1000.0;

                    if (distance <= 8) {
                        scale = 0.0245D;
                    }

                    GlStateManager.pushMatrix();
                    RenderHelper.enableStandardItemLighting();
                    GlStateManager.disableLighting();
                    GlStateManager.translate((float) x, (float) tempY + 1.4F, (float) z);
                    GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(mc.getRenderManager().playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0F : 1.0F, 0.0F, 0.0F);
                    GlStateManager.scale(-scale, -scale, scale);
                    GlStateManager.disableDepth();

                    if (rect.getValue()) {
                        RenderUtil.drawRect(-width - 2, -10, width + 2F, 1.5F, 0x60000000);
                    }

                    if (armor.getValue()) {
                        int xOffset = -48;
                        GlStateManager.pushMatrix();
                        this.renderItemStack(player.getHeldItemOffhand(), xOffset);
                        xOffset += 16;

                        for (int j = 0; j < 4; ++j) {
                            this.renderItemStack(player.inventory.armorInventory.get(j), xOffset);
                            xOffset += 16;
                        }

                        this.renderItemStack(player.getHeldItemMainhand(), xOffset);

                        GlStateManager.popMatrix();
                    }


                    Realth.INSTANCE.getFontManager().drawString(displayTag, -width, -8, Nametags.getDisplayColour(player));

                    camera.posX = originalPositionX;
                    camera.posY = originalPositionY;
                    camera.posZ = originalPositionZ;
                    GlStateManager.enableDepth();
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                }
            }
        }
    }

    private void renderItemStack(ItemStack stack, int x) {
        GlStateManager.depthMask(true);
        GlStateManager.clear(GL11.GL_ACCUM);

        RenderHelper.enableStandardItemLighting();
        mc.getRenderItem().zLevel = -150.0F;
        GlStateManager.disableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.disableCull();

        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, -26);
        mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, x, -26);

        mc.getRenderItem().zLevel = 0.0F;
        RenderHelper.disableStandardItemLighting();

        GlStateManager.enableCull();
        GlStateManager.enableAlpha();

        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        GlStateManager.disableDepth();
        renderEnchantmentText(stack, x);
        GlStateManager.enableDepth();
        GlStateManager.scale(2F, 2F, 2F);
    }

    private static void renderEnchantmentText(ItemStack stack, int x) {
        int enchantmentY = -26 - 8;

        final NBTTagList enchants = stack.getEnchantmentTagList();
        final int tagCount = enchants.tagCount();
        for (int index = 0; index < tagCount; ++index) {
            final short id = enchants.getCompoundTagAt(index).getShort("id");
            final short level = enchants.getCompoundTagAt(index).getShort("lvl");
            final Enchantment enc = Enchantment.getEnchantmentByID(id);

            if (enc != null) {
                if (enc.getName().contains("fall") || !(enc.getName().contains("all") || enc.getName().contains("explosion"))) continue;

                Realth.INSTANCE.getFontManager().drawString(enc.getTranslatedName(level).substring(0, 1).toLowerCase() + level, x << 1, enchantmentY, -1);
                enchantmentY -= 8;
            }
        }

        if (hasDurability(stack)) {
            final int percent = getRoundedDamage(stack);
            String color;
            if (percent > 90) {
                color = ChatFormatting.GREEN.toString();
            } else if (percent > 75) {
                color = ChatFormatting.DARK_GREEN.toString();
            } else if (percent > 50) {
                color = ChatFormatting.YELLOW.toString();
            } else if (percent > 30) {
                color = ChatFormatting.GOLD.toString();
            } else if (percent > 15) {
                color = ChatFormatting.RED.toString();
            } else {
                color = ChatFormatting.DARK_RED.toString();
            }
            Realth.INSTANCE.getFontManager().drawString(color + percent + "%", x << 1 /*bit shift instead of multiplying by 2*/, enchantmentY, 0xFFFFFFFF);
        }
    }

    public static int getItemDamage(ItemStack stack) {
        return stack.getMaxDamage() - stack.getItemDamage();
    }

    public static float getDamageInPercent(ItemStack stack) {
        return (getItemDamage(stack) / (float)stack.getMaxDamage()) * 100;
    }

    public static int getRoundedDamage(ItemStack stack) {
        return (int)getDamageInPercent(stack);
    }

    public static boolean hasDurability(ItemStack stack) {
        final Item item = stack.getItem();
        return item instanceof ItemArmor || item instanceof ItemSword || item instanceof ItemTool || item instanceof ItemShield;
    }

    private String getDisplayTag(final EntityPlayer player) {
        final float health = EntityUtil.getHealth(player);
        String color;

        if (health > 18) {
            color = ChatFormatting.GREEN.toString();
        } else if (health > 16) {
            color = ChatFormatting.DARK_GREEN.toString();
        } else if (health > 12) {
            color = ChatFormatting.YELLOW.toString();
        } else if (health > 8) {
            color = ChatFormatting.GOLD.toString();
        } else if (health > 5) {
            color = ChatFormatting.RED.toString();
        } else {
            color = ChatFormatting.DARK_RED.toString();
        }

        String pingStr = "";
        if (ping.getValue()) {
            try {
                final int responseTime = Objects.requireNonNull(mc.getConnection()).getPlayerInfo(player.getUniqueID()).getResponseTime();
                pingStr += responseTime + "ms";
            } catch (Exception ignored) {
                pingStr += "-1ms";
            }
        }

        String popStr = "";
        if (totemPops.getValue()) {
            final Map<String, Integer> registry = Realth.INSTANCE.getPopManager().getPopMap();
            popStr += registry.containsKey(player.getName()) ? " -" + registry.get(player.getName()) : "";
        }

        return player.getName() + color + " " + (int)health + " " + ChatFormatting.RESET + pingStr + popStr;
    }

    private static int getDisplayColour(EntityPlayer player) {
        if (Realth.INSTANCE.getFriendManager().isFriend(player)) {
            return 0xFF55C0ED;
        }
        return 0xFFFFFFFF;
    }

    private static double interpolate(double previous, double current, float delta) {
        return (previous + (current - previous) * delta);
    }

}
