package me.hollow.realth.client.other;

import me.hollow.realth.api.utils.ChatUtil;
import me.hollow.realth.client.module.modules.misc.PopCounter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;

import java.util.HashMap;
import java.util.Map;

public class PopManager {

    private final Map<String, Integer> popMap = new HashMap<>();
    private final Minecraft mc = Minecraft.getMinecraft();

    public void update() {
        final int size = mc.world.playerEntities.size();
        for (int i = 0; i < size; ++i) {
            final EntityPlayer plr = mc.world.playerEntities.get(i);
            if (plr.getHealth() <= 0) {
                if (popMap.containsKey(plr.getName())) {
                    if (PopCounter.INSTANCE.isEnabled()) {
                        ChatUtil.printMessageWithID(plr.getName() + " died after popping their " + popMap.get(plr.getName()) + getNumberStringThing(popMap.get(plr.getName())) + " totem.", plr.getEntityId());
                    }
                    popMap.remove(plr.getName(), popMap.get(plr.getName()));
                }
            }
        }
    }

    public static String getNumberStringThing(int number) {
        if (number > 3) {
            return "th";
        }
        switch (number) {
            case 2:
                return "nd";
            case 3:
                return "rd";
        }
        return "";
    }

    public void onEntityStatus(final SPacketEntityStatus packet) {
        if (packet.getOpCode() == 35) {
            final Entity entity = packet.getEntity(mc.world);
            if (popMap.get(entity.getName()) == null) {
                popMap.put(entity.getName(), 1);
                if (PopCounter.INSTANCE.isEnabled()) {
                    ChatUtil.printMessageWithID(entity.getName() + " popped " + "a totem.", entity.getEntityId());
                }
            } else if (popMap.get(entity.getName()) != null) {
                final int popCounter = popMap.get(entity.getName());
                final int newPopCounter = popCounter + 1;
                popMap.put(entity.getName(), newPopCounter);
                if (PopCounter.INSTANCE.isEnabled()) {
                    ChatUtil.printMessageWithID(entity.getName() + " popped their " + newPopCounter + getNumberStringThing(newPopCounter) + " totem.", entity.getEntityId());
                }
            }
        }
    }

    public Map<String, Integer> getPopMap() {
        return popMap;
    }
}
