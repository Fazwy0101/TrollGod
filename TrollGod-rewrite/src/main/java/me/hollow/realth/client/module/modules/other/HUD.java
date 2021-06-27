package me.hollow.realth.client.module.modules.other;

import me.hollow.realth.Realth;
import me.hollow.realth.api.property.Value;
import me.hollow.realth.api.utils.ItemUtil;
import me.hollow.realth.api.utils.TickRate;
import me.hollow.realth.client.events.Render2DEvent;
import me.hollow.realth.client.events.ResizeEvent;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import me.hollow.realth.client.other.ColorHandler;
import me.hollow.realth.client.other.SpeedManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

@ModuleManifest(label = "HUD", category = Module.Category.OTHER, enabled = true)
public class HUD extends Module {

    private final Value<Boolean> watermark = new Value<>("Watermark", true);
    private final Value<Boolean> arrayList = new Value<>("Arraylist", true);
    private final Value<Boolean> armor = new Value<>("Armor", true);
    private final Value<Boolean> coordinates = new Value<>("Coordinates", true);
    private final Value<Boolean> tps = new Value<>("TPS", true);
    private final Value<Boolean> ping = new Value<>("Ping", true);
    private final Value<Boolean> speed = new Value<>("Speed", true);
    private final Value<Boolean> framesPerSecond = new Value<>("FPS", true);
    private final Value<Boolean> rotations = new Value<>("Rotations", true);
    private final Value<Boolean> potionEffects = new Value<>("Effects", true);

    private final Map<Module, Float> extendedAmount = new HashMap<>();

    private List<Module> moduleList = new ArrayList<>();
    private ScaledResolution resolution = new ScaledResolution(mc);

    public static HUD INSTANCE;

    {
        INSTANCE = this;
    }

    public void init() {
        moduleList = Realth.INSTANCE.getModuleManager().getModules();
    }

    @SubscribeEvent
    public void onResize(ResizeEvent event) {
        resolution = new ScaledResolution(mc);
    }

    @SubscribeEvent
    public void onRender(final Render2DEvent event) {
        if (watermark.getValue()) {
            Realth.INSTANCE.getFontManager().drawString("Realth v1.0", 2, 2, ColorHandler.getColorInt());
        }
        final float speedRatio = (144.0F / Minecraft.getDebugFPS());
        if (arrayList.getValue()) {
            int offsetY = -8;
            moduleList.sort(Comparator.comparingDouble(mod -> -Realth.INSTANCE.getFontManager().getStringWidth(mod.getLabel() + mod.getSuffix())));
            final int size = moduleList.size();
            for (int i = 0; i < size; i++) {
                final Module module = moduleList.get(i);
                extendedAmount.putIfAbsent(module, -3f);
                if (!module.isDrawn())
                    continue;
                if (module.isEnabled() || extendedAmount.get(module) > -3f) {
                    final String fullLabel = new StringBuilder()
                            .append(module.getLabel())
                            .append(module.getSuffix())
                            .toString();
                    final float openingTarget = Realth.INSTANCE.getFontManager().getStringWidth(fullLabel);
                    final float target = module.isEnabled() ? openingTarget : -3F;
                    float newAmount = extendedAmount.get(module);

                    newAmount += 1.5 * speedRatio * (module.isEnabled() ? 1 : -1);

                    newAmount = module.isEnabled() ? Math.min(target, newAmount) : Math.max(target, newAmount);

                    if (!module.isEnabled() && newAmount < 0) {
                        newAmount = -3F;
                    }
                    if (module.isEnabled() && target - newAmount < 1) {
                        newAmount = target;
                    }

                    float percent = newAmount / openingTarget;
                    extendedAmount.put(module, newAmount);
                    Realth.INSTANCE.getFontManager().drawString(fullLabel, resolution.getScaledWidth() - extendedAmount.get(module) - 2, offsetY += 10 * percent, ColorHandler.getColorInt());
                }
            }
        }

        int offset = 10;
        if (potionEffects.getValue()) {
            for (PotionEffect effect : mc.player.getActivePotionEffects()) {
                final Potion potion = effect.getPotion();
                String fullName = I18n.format(effect.getPotion().getName());

                if (effect.getAmplifier() == 1) {
                    fullName = fullName + " " + I18n.format("enchantment.level.2");
                } else if (effect.getAmplifier() == 2) {
                    fullName = fullName + " " + I18n.format("enchantment.level.3");
                } else if (effect.getAmplifier() == 3) {
                    fullName = fullName + " " + I18n.format("enchantment.level.4");
                }

                String s = Potion.getPotionDurationString(effect, 1.0F);
                fullName = fullName + " " + s;
                Realth.INSTANCE.getFontManager().drawString(fullName, resolution.getScaledWidth() - Realth.INSTANCE.getFontManager().getStringWidth(fullName) - 2, resolution.getScaledHeight() - offset, potion.getLiquidColor());
                offset += 10;
            }
        }

        if (speed.getValue()) {
            final String speed = "Speed:\u00a7f " + String.format("%.2f", SpeedManager.getSpeedKMH()) + "km/h";
            Realth.INSTANCE.getFontManager().drawString(speed, resolution.getScaledWidth() - Realth.INSTANCE.getFontManager().getStringWidth(speed) - 2, resolution.getScaledHeight() - offset, ColorHandler.getColorInt());
            offset += 10;
        }

        if (tps.getValue()) {
            final String tps = "TPS:\u00a7f " + String.format("%.2f", TickRate.TPS);
            Realth.INSTANCE.getFontManager().drawString(tps, resolution.getScaledWidth() - Realth.INSTANCE.getFontManager().getStringWidth(tps) - 2, resolution.getScaledHeight() - offset, ColorHandler.getColorInt());
            offset += 10;
        }

        if (framesPerSecond.getValue()) {
            final String fps = "FPS:\u00a7f " + Minecraft.getDebugFPS();
            Realth.INSTANCE.getFontManager().drawString(fps, resolution.getScaledWidth() - Realth.INSTANCE.getFontManager().getStringWidth(fps) - 2, resolution.getScaledHeight() - offset, ColorHandler.getColorInt());
            offset += 10;
        }

        if (ping.getValue()) {
            if (mc.getConnection().getPlayerInfo(mc.player.getName()) != null) {
                final String ping = "Ping:\u00a7f " + mc.getConnection().getPlayerInfo(mc.player.getName()).getResponseTime();
                Realth.INSTANCE.getFontManager().drawString(ping, resolution.getScaledWidth() - Realth.INSTANCE.getFontManager().getStringWidth(ping) - 2, resolution.getScaledHeight() - offset, ColorHandler.getColorInt());
            }
        }

        boolean openChat = mc.ingameGUI.getChatGUI().getChatOpen();
        if (rotations.getValue() && !openChat) {
            Realth.INSTANCE.getFontManager().drawString("Pitch:\u00a7f " + String.format("%.2f", MathHelper.wrapDegrees(mc.player.rotationPitch)), 2, resolution.getScaledHeight() - 20, ColorHandler.getColorInt());
            Realth.INSTANCE.getFontManager().drawString("Yaw:\u00a7f " + String.format("%.2f", MathHelper.wrapDegrees(mc.player.rotationYaw)), 2, resolution.getScaledHeight() - 30, ColorHandler.getColorInt());
        }

        if (coordinates.getValue()) {
            String facing;
            switch (mc.getRenderViewEntity().getHorizontalFacing()) {
                case NORTH:
                    facing = " \u00a78[\u00a7f-Z\u00a78]";
                    break;
                case SOUTH:
                    facing = " \u00a78[\u00a7f+Z\u00a78]";
                    break;
                case WEST:
                    facing = " \u00a78[\u00a7f-X\u00a78]";
                    break;
                case EAST:
                    facing = " \u00a78[\u00a7f+X\u00a78]";
                    break;
                default:
                    facing = " \u00a78[\u00a7fWTF\u00a78]";
            }
            Realth.INSTANCE.getFontManager().drawString("XYZ: \u00a7f" + String.format("%.2f", mc.player.posX) + "\u00a78, \u00a7f" + String.format("%.2f", mc.player.posY) + "\u00a78, \u00a7f" + String.format("%.2f", mc.player.posZ) + " \u00a78(\u00a7f" + String.format("%.2f", getDimensionCoord(mc.player.posX)) + "\u00a78, \u00a7f" + String.format("%.2f", getDimensionCoord(mc.player.posZ)) + "\u00a78)" + facing, 2, resolution.getScaledHeight() - (openChat ? 22 : 10), ColorHandler.getColorInt());
        }

        if (armor.getValue()) {
            final int i = resolution.getScaledWidth() >> 1; // Evil Bit Hack
            final int y = resolution.getScaledHeight() - 55 - (mc.player.isInWater() && mc.playerController.gameIsSurvivalOrAdventure() ? 10 : 0);
            GlStateManager.enableTexture2D();
            for (int j = 0; j < 4; ++j) {
                final ItemStack is = mc.player.inventory.armorInventory.get(j);
                if (is.isEmpty()) continue;
                final int x = i - 90 + (9 - j - 1) * 20 + 2;
                GlStateManager.enableDepth();
                mc.getRenderItem().zLevel = 200.0f;
                mc.getRenderItem().renderItemAndEffectIntoGUI(is, x, y);
                mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, is, x, y, "");
                mc.getRenderItem().zLevel = 0.0f;
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                final int dmg = (int) ItemUtil.getDamageInPercent(is);
                Realth.INSTANCE.getFontManager().drawString(dmg + "", x + 8 - (Realth.INSTANCE.getFontManager().getStringWidth(dmg + "") >> 1), y + -8, is.getItem().getRGBDurabilityForDisplay(is));
            }
            GlStateManager.enableDepth();
            GlStateManager.disableLighting();
        }
    }

    public double getDimensionCoord(double coord) {
        return mc.player.dimension == 0 ? coord / 8 : coord * 8;
    }

}
