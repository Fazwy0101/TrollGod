package me.hollow.realth.client.module.modules.visual;

import me.hollow.realth.api.property.Value;
import me.hollow.realth.client.events.Render2DEvent;
import me.hollow.realth.client.events.UpdateEvent;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleManifest(label = "RenderTweaks", category = Module.Category.VISUAL)
public class RenderTweaks extends Module {

    public static RenderTweaks INSTANCE;

    {
        INSTANCE = this;
    }

    public final Value<Boolean> hotbarNumbers = new Value<>("HBar Numbers", true);
    public final Value<Float> ratio = new Value<>("Ratio", 1.833f, 0f, 3f);
    public final Value<Boolean> resetRatio = new Value<>("Reset", false);
    public final Value<Boolean> bumpy = new Value<>("Bumpy", true);

    private static final ResourceLocation BUMPY = new ResourceLocation("shaders/post/bumpy.json");

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (resetRatio.getValue()) {
            ratio.setValue(1.777777777777778F);
            resetRatio.setValue(false);
        }
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent event) {
        if (bumpy.getValue()) {
            if (!mc.entityRenderer.isShaderActive()) {
                if (mc.entityRenderer.getShaderGroup() != null) {
                    mc.entityRenderer.getShaderGroup().deleteShaderGroup();
                }
                try {
                    mc.entityRenderer.loadShader(BUMPY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
