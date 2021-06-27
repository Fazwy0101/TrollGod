package me.hollow.realth.client.clickgui;

import me.hollow.realth.Realth;
import me.hollow.realth.client.clickgui.item.Item;
import me.hollow.realth.client.clickgui.item.ModuleButton;
import me.hollow.realth.client.module.Module;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;


/**
 * @author TehNeon
 * @author nuf
 */
public final class ClickGui extends GuiScreen {
    private static ClickGui clickGui;
    private final ArrayList<Panel> panels = new ArrayList<>();

    public ClickGui() {
        load();
    }

    public static ClickGui getClickGui() {
        return clickGui == null ? clickGui = new ClickGui() : clickGui;
    }

    private void load() {
        int x = -84;//TODO fix everything
        for (Module.Category moduleType : Module.Category.values()) {
            String name = moduleType.name();
            panels.add(new Panel(name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase(), x += 94, 4, true) {
                @Override
                public void setupItems() {
                    Realth.INSTANCE.getModuleManager().getModules().forEach(mod -> {
                        if (mod.getCategory().equals(moduleType)) {
                            addButton(new ModuleButton(mod));
                        }
                    });
                }
            });
        }
        panels.forEach(panel -> panel.getItems().sort(Comparator.comparing(Item::getLabel)));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        scroll();
        panels.forEach(panel -> panel.drawScreen(mouseX, mouseY, partialTicks));
    }

    public void scroll() {
        int type = Mouse.getDWheel();
        if (type < 0) {
            panels.forEach(pnl -> pnl.setY(pnl.getY() - 5));
        } else if (type > 0) {
            panels.forEach(pnl -> pnl.setY(pnl.getY() + 5));
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        panels.forEach(pnl -> pnl.onKeyTyped(typedChar, keyCode));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int clickedButton) {
        panels.forEach(panel -> panel.mouseClicked(mouseX, mouseY, clickedButton));
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        panels.forEach(panel -> panel.mouseReleased(mouseX, mouseY, releaseButton));
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public final ArrayList<Panel> getPanels() {
        return panels;
    }
}
