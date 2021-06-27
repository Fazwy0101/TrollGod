package me.hollow.realth.client.clickgui.item;

import me.hollow.realth.Realth;
import me.hollow.realth.api.property.Bind;
import me.hollow.realth.api.property.Value;
import me.hollow.realth.client.clickgui.item.properties.BindButton;
import me.hollow.realth.client.clickgui.item.properties.BooleanButton;
import me.hollow.realth.client.clickgui.item.properties.EnumButton;
import me.hollow.realth.client.clickgui.item.properties.NumberSlider;
import me.hollow.realth.client.module.Module;

import java.util.ArrayList;
import java.util.List;


/**
 * @author TehNeon
 * @author nuf
 * @since May 30, 2016
 */
public class ModuleButton extends Button {

    private final Module module;
    private final List<Item> items = new ArrayList<>();
    private boolean subOpen;

    public ModuleButton(Module module) {
        super(module.getLabel());
        this.module = module;

        final List<Value> properties = module.getProperties();
        final int size = properties.size();
        for (int i = 0; i < size; ++i) {
            final Value value = properties.get(i);
            if (value.getValue() instanceof Boolean) {
                items.add(new BooleanButton(value));
            }
            if (value.getValue() instanceof Number) {
                items.add(new NumberSlider(value));
            }
            if (value.getValue() instanceof Bind) {
                items.add(new BindButton(value));
            }
            if (value.getValue() instanceof Enum) {
                items.add(new EnumButton(value));
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (!items.isEmpty()) {
            Realth.INSTANCE.getFontManager().drawString(subOpen ? "-" : "+", x - 1F + width - 8F, y - 2F + 6,0xFFFFFFFF);
            if (subOpen) {
                float height = 1;
                for (Item item : items) {
                    height += 15F;
                    item.setLocation(x + 1, y + height);
                    item.setHeight(15);
                    item.setWidth(width - 9);
                    item.drawScreen(mouseX, mouseY, partialTicks);
                }
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (!items.isEmpty()) {
            if (mouseButton == 1 && isHovering(mouseX, mouseY)) {
                subOpen = !subOpen;
            }
            if (subOpen) {
                for (Item item : items) {
                    item.mouseClicked(mouseX, mouseY, mouseButton);
                }
            }
        }
    }

    @Override
    public int getHeight() {
        if (subOpen) {
            int height = 14;
            for (Item item : items) {
                height += item.getHeight() + 1;
            }
            return height + 2;
        } else {
            return 14;
        }
    }

    public void toggle() {
        module.toggle();
    }

    public boolean getState() {
        return module.isEnabled() || module.isPersistent();
    }
}
