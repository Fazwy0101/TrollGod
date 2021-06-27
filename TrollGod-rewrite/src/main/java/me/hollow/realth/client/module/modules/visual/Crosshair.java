package me.hollow.realth.client.module.modules.visual;

import me.hollow.realth.api.property.Value;
import me.hollow.realth.api.utils.ColorUtil;
import me.hollow.realth.api.utils.RenderUtil;
import me.hollow.realth.client.events.Render2DEvent;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

@ModuleManifest(label = "Crosshair", category = Module.Category.VISUAL)
public class Crosshair extends Module {

    private final Value<Boolean> dynamic = new Value<>("Dynamic", true);
    private final Value<Float> width = new Value<>("Width", 1.0f, 0.5f, 10.0f);
    private final Value<Float> gap = new Value<>("Gap", 3.0f, 0.5f, 10.0f);
    private final Value<Float> length = new Value<>("Length", 7.0f, 0.5f, 100.0f);
    private final Value<Float> dynamicGap = new Value<>("DynamicGap", 1.5f, 0.5f, 10.0f);
    private final Value<Integer> red = new Value<>("Red", 255, 0, 255);
    private final Value<Integer> green = new Value<>("Green", 255, 0, 255);
    private final Value<Integer> blue = new Value<>("Blue", 255, 0, 255);
    private final Value<Integer> alpha = new Value<>("Alpha", 255, 0, 255);
    private final Value<Boolean> rainbow = new Value<>("Rainbow", false);
    private final Value<Boolean> staticRainbow = new Value<>("Static Rainbow", false);

    public static Crosshair INSTANCE;

    {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent event) {
        Color rai = new Color(ColorUtil.getRainbow(6000, -15, 0.75F));
        final int color = staticRainbow.getValue() ? color(2, 100) : (rainbow.getValue() ? new Color(rai.getRed(), rai.getGreen(), rai.getBlue(), alpha.getValue()).getRGB() : new Color(red.getValue(), green.getValue(), blue.getValue(), alpha.getValue()).getRGB());

        final ScaledResolution resolution = new ScaledResolution(mc);
        final float middlex = resolution.getScaledWidth() / 2F;
        final float middley = resolution.getScaledHeight() / 2F;
        // top box
        RenderUtil.drawBordered(middlex - (width.getValue()), middley - (gap.getValue() + length.getValue()) - ((isMoving() && dynamic.getValue()) ? dynamicGap.getValue() : 0), middlex + (width.getValue()), middley - (gap.getValue()) - ((isMoving() && dynamic.getValue()) ? dynamicGap.getValue() : 0), 0.5F, color, 0xff000000);
        // bottom box
        RenderUtil.drawBordered(middlex - (width.getValue()), middley + (gap.getValue()) + ((isMoving() && dynamic.getValue()) ? dynamicGap.getValue() : 0), middlex + (width.getValue()), middley + (gap.getValue() + length.getValue()) + ((isMoving() && dynamic.getValue()) ? dynamicGap.getValue() : 0), 0.5F, color, 0xff000000);
        // left box
        RenderUtil.drawBordered(middlex - (gap.getValue() + length.getValue()) - ((isMoving() && dynamic.getValue()) ? dynamicGap.getValue() : 0), middley - (width.getValue()), middlex - (gap.getValue()) - ((isMoving() && dynamic.getValue()) ? dynamicGap.getValue() : 0), middley + (width.getValue()), 0.5F, color, 0xff000000);
        // right box
        RenderUtil.drawBordered(middlex + (gap.getValue()) + ((isMoving() && dynamic.getValue()) ? dynamicGap.getValue() : 0), middley - (width.getValue()), middlex + (gap.getValue() + length.getValue()) + ((isMoving() && dynamic.getValue()) ? dynamicGap.getValue() : 0), middley + (width.getValue()), 0.5F, color, 0xff000000);
    }

    public boolean isMoving() {
        return mc.player.moveForward != 0 || mc.player.moveStrafing != 0 || mc.player.moveVertical != 0;
    }

    public int color(int index, int count) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(red.getValue(),green.getValue(),blue.getValue(), hsb);

        float brightness = Math.abs(((getOffset() + (index / (float) count) * 2) % 2) - 1);
        brightness = 0.4f + (0.4f * brightness);

        hsb[2] = brightness % 1f;
        Color clr = new Color(Color.HSBtoRGB(hsb[0],hsb[1], hsb[2]));
        return new Color(clr.getRed(),clr.getGreen(),clr.getBlue(),alpha.getValue()).getRGB();
    }

    private static float getOffset() {
        return (System.currentTimeMillis() % 2000) / 1000f;
    }

}
