package me.hollow.realth.client.clickgui.item.properties;

import me.hollow.realth.Realth;
import me.hollow.realth.api.property.Value;
import me.hollow.realth.api.utils.RenderUtil;
import me.hollow.realth.client.clickgui.item.Button;

/**
 * @author TehNeon
 * @author nuf
 * @since May 30, 2016
 */
public class BooleanButton extends Button {
    private Value value;

    public BooleanButton(Value value) {
        super(value.getLabel());
        this.value = value;
        width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(x, y, x + width + 7.4F, y + height, getState() ? getColor(!isHovering(mouseX, mouseY)) : !isHovering(mouseX, mouseY) ? 0x11555555 : 0x88555555);
        Realth.INSTANCE.getFontManager().drawString(getLabel(), x + 2.3F, y - 1.7F +6, getState() ? 0xFFFFFFFF : 0xFFAAAAAA);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public int getHeight() {
        return 14;
    }

    public void toggle() {
        value.setValue(!(boolean) value.getValue());
    }

    public boolean getState() {
        return (boolean) value.getValue();
    }
}
