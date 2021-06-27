package me.hollow.realth;

import me.hollow.realth.client.command.manage.CommandManager;
import me.hollow.realth.client.module.manage.ModuleManager;
import me.hollow.realth.client.other.*;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = Realth.MOD_ID, name = Realth.MOD_NAME, version = Realth.VERSION)
public class Realth {

    public static final String MOD_ID = "realth";
    public static final String MOD_NAME = "Realth";
    public static final String VERSION = "1.0";
    public static final Logger LOGGER = LogManager.getLogger("Realth");

    @Mod.Instance(MOD_ID)
    public static Realth INSTANCE;

    private final File file = new File(Minecraft.getMinecraft().gameDir, "Realth");
    private final FontManager fontManager = new FontManager();
    private final ModuleManager moduleManager = new ModuleManager();
    private final CommandManager commandManager = new CommandManager();
    private final FriendManager friendManager = new FriendManager();
    private final SafetyManager safetyManager = new SafetyManager();
    private final ConfigManager configManager = new ConfigManager();
    private final SpeedManager speedManager = new SpeedManager();
    private final FileManager fileManager = new FileManager();
    private final PopManager popManager = new PopManager();

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        moduleManager.init();
        friendManager.setDirectory(new File(file, "friends.json"));
        friendManager.init();
        commandManager.init();
        configManager.init();
        EventListener.INSTANCE.init();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            configManager.saveConfig();
            friendManager.unload();
        }));
    }

    public FontManager getFontManager() {
        return fontManager;
    }

    public SpeedManager getSpeedManager() {
        return speedManager;
    }

    public PopManager getPopManager() {
        return popManager;
    }

    public SafetyManager getSafetyManager() {
        return safetyManager;
    }

    public FriendManager getFriendManager() {
        return friendManager;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

}
