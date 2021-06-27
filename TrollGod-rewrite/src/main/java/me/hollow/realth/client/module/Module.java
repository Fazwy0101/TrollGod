package me.hollow.realth.client.module;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.hollow.realth.api.property.Value;
import me.hollow.realth.api.utils.ChatUtil;
import me.hollow.realth.client.events.SuffixEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class    Module {

    private String label, suffix = "";
    private Category category;

    private boolean enabled, drawn, listenable, persistent;
    private int key;

    protected final Minecraft mc = Minecraft.getMinecraft();

    private final List<Value> properties = new ArrayList<>();

    public Module() {
        if (getClass().isAnnotationPresent(ModuleManifest.class)) {
            ModuleManifest manifest = getClass().getAnnotation(ModuleManifest.class);
            this.label = manifest.label();
            this.category = manifest.category();
            this.enabled = manifest.enabled();
            this.listenable = manifest.listenable();
            this.persistent = manifest.persistent();
            this.drawn = true;
            if ((enabled || persistent) && listenable)
                MinecraftForge.EVENT_BUS.register(this);
            this.key = manifest.key();
        }
    }

    public void register(Value value) {
        properties.add(value);
    }

    public List<Value> getProperties() {
        return properties;
    }

    public void onEnable() {}
    public void onDisable() {}
    public void onToggle() {}
    public void onRender3D() {}

    public void toggle() {
        setEnabled(!enabled);
    }

    public void setEnabled(boolean enabled) {
        if (persistent) {
            return;
        }
        this.enabled = enabled;
        onToggle();
        if (enabled) {
            if (listenable) {
                MinecraftForge.EVENT_BUS.register(this);
            }
            onEnable();
            ChatUtil.printMessageWithID(getLabel() + ChatFormatting.DARK_GRAY + " has been " + ChatFormatting.GREEN + "enabled", -1821);
        } else {
            if (listenable) {
                MinecraftForge.EVENT_BUS.unregister(this);
            }
            onDisable();
            ChatUtil.printMessageWithID(getLabel() + ChatFormatting.DARK_GRAY + " has been " + ChatFormatting.RED + "disabled", -1821);
        }
    }

    public String getSuffix() {
        if (suffix.length() == 0)
            return "";

        return " \u00a77[\u00a7f" + suffix + "\u00a77]";
    }

    public void setSuffix(String suffix) {
        if (!suffix.equals(this.suffix)) {
            MinecraftForge.EVENT_BUS.post(new SuffixEvent());
        }
        this.suffix = suffix;
    }

    public void setDrawn(boolean drawn) {
        this.drawn = drawn;
    }

    public void clearSuffix() {
        suffix = "";
    }

    public final boolean isNull() {
        return mc.player == null || mc.world == null;
    }

    public boolean isDrawn() {
        return drawn;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public void setKey(String key) {
        Keyboard.getKeyIndex(key.toUpperCase());
    }

    public Category getCategory() {
        return category;
    }

    public String getLabel() {
        return label;
    }

    public enum Category {
        COMBAT,
        MOVEMENT,
        PLAYER,
        VISUAL,
        MISC,
        OTHER
    }

}
