package me.hollow.realth.client.command.commands;

import com.mojang.authlib.GameProfile;
import me.hollow.realth.client.command.Command;
import me.hollow.realth.client.command.CommandManifest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;

import java.util.UUID;

@CommandManifest(label = "FakePlayer", aliases = {"fake", "deeznutz"})
public class FakePlayerCommand extends Command {

    private final Minecraft mc = Minecraft.getMinecraft();

    private EntityOtherPlayerMP fakePlayer;

    @Override
    public void execute(String[] arguments) {
        if (arguments.length < 1 || arguments.length > 2) {
            return;
        }

        switch (arguments[1].toUpperCase()) {
            case "ADD":
            case "SPAWN":
                fakePlayer = new EntityOtherPlayerMP(mc.world, new GameProfile(UUID.randomUUID(), "fake"));
                fakePlayer.copyLocationAndAnglesFrom(mc.player);
                fakePlayer.inventory.copyInventory(mc.player.inventory);
                mc.world.spawnEntity(fakePlayer);
                break;
            case "DEL":
            case "REMOVE":
            case "DELETE":
                if (fakePlayer != null)
                    mc.world.removeEntity(fakePlayer);
                break;
        }
    }

}
