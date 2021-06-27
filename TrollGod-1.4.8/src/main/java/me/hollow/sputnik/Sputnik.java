package me.hollow.sputnik;

import me.hollow.sputnik.client.managers.*;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;
import tcb.bces.bus.DRCEventBus;

import java.io.File;

@Mod(name = "TrollGod", modid = "trollgod", version = Sputnik.VERSION)
public final class Sputnik {

    public static final String NAME = "TrollGod";
    public static final String VERSION = "1.4.8";
    public static final Logger logger = LogManager.getLogger(NAME);

    private final DRCEventBus eventBus = new DRCEventBus();

    private final File directory = new File(Minecraft.getMinecraft().gameDir, "TrollGod");

    private final CommandManager commandManager = new CommandManager();
    private final ModuleManager moduleManager = new ModuleManager();
    private final ConfigManager configManager = new ConfigManager();
    private final FriendManager friendManager = new FriendManager();
    private final EventManager eventManager = new EventManager();
    private final FileManager fileManager = new FileManager();
    private final SafeManager safeManager = new SafeManager();
    private final PopManager popManager = new PopManager();
    private final TPSManager tpsManager = new TPSManager();


    public static final Sputnik INSTANCE = new Sputnik();

    public static final FontManager fontManager = new FontManager();

    public final void init() {
        moduleManager.init();
        commandManager.init();
        eventManager.init();
        friendManager.setDirectory(new File(directory, "friends.json"));
        friendManager.init();
        tpsManager.init();
        popManager.init();
        configManager.init();
        eventBus.bind();
        Runtime.getRuntime().addShutdownHook(new ShutdownThread());
        Display.setTitle("trollgod");
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public final TPSManager getTpsManager() {
        return tpsManager;
    }

    public final DRCEventBus getBus() {
        return eventBus;
    }

    public final PopManager getPopManager() {
        return popManager;
    }

    public final FriendManager getFriendManager() {
        return friendManager;
    }

    public final ConfigManager getConfigManager() {
        return configManager;
    }

    public final ModuleManager getModuleManager() {
        return moduleManager;
    }

    public final SafeManager getSafeManager() {
        return safeManager;
    }

    final static class ShutdownThread extends Thread {

        @Override
        public void run() {
            logger.info("Trying to save config....");
            Sputnik.INSTANCE.getConfigManager().saveConfig(Sputnik.INSTANCE.getConfigManager().config.replaceFirst("TrollGod/", ""));
            Sputnik.INSTANCE.getFriendManager().unload();
            logger.info("Config saved!");
        }

    }

}
