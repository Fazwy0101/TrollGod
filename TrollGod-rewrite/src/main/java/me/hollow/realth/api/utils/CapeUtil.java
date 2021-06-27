package me.hollow.realth.api.utils;

import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public class CapeUtil {

    private static final ResourceLocation HOLLOW = new ResourceLocation("textures/2012.png");
    private static final String[] UUIDS = {
            "c9050000-3681-4df6-9b50-0fd8f5275835",
            "cc72ff00-a113-48f4-be18-2dda8db52355",
            "c1092cbe-b671-471e-b0a6-12da20d55135",
            "c137f0cf-5e87-4176-8b82-325916bcb3bd",
            "527f2230-e557-452a-9188-17dece1842ce",
            "13ffb8cf-a576-45ee-898d-47ce897f5968"
    };

    public static ResourceLocation getResourceLocation(UUID id) {
        final String stringId = id.toString();
        for (final String string : UUIDS) {
            if (string.equalsIgnoreCase(stringId)) {
                return HOLLOW;
            }
        }
        return null;
    }

}
