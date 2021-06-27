package me.hollow.realth.client.clickgui.item.properties;


import me.hollow.realth.Realth;
import me.hollow.realth.api.property.Value;
import me.hollow.realth.api.utils.RenderUtil;
import me.hollow.realth.client.clickgui.ClickGui;
import me.hollow.realth.client.clickgui.Panel;
import me.hollow.realth.client.clickgui.item.Item;
import me.hollow.realth.client.module.modules.other.ClickGuiMod;
import me.hollow.realth.client.other.ColorHandler;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Mouse;

/**
 * Created by nuf on 6/3/2016.
 */
public class NumberSlider extends Item {
    private final Value numberValue;

    private boolean sliding;
    private final Number min;
    private final Number max;
    private final int difference;

    public NumberSlider(Value numberValue) {
        super(numberValue.getLabel());
        this.min = (Number) numberValue.getMinimum();
        this.max = (Number) numberValue.getMaximum();
        this.difference = max.intValue() - min.intValue();
        this.numberValue = numberValue;
    }

    public int getColor(boolean hovered) {
        return changeAlpha(ColorHandler.getColorInt(), hovered ? ClickGuiMod.INSTANCE.alpha.getValue() : 120);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        dragSetting(mouseX, mouseY);
        RenderUtil.drawRect(x, y, x + width + 7.4f, y + height, !isHovering(mouseX, mouseY) ? 0x11555555 : 0x88555555);
        RenderUtil.drawRect(x, y, ((Number) numberValue.getValue()).floatValue() <= min.floatValue() ? x : x + (width + 7.4F) * partialMultiplier(), y + height, getColor(!isHovering(mouseX, mouseY)));
        Realth.INSTANCE.getFontManager().drawString(String.format("%s\2477 %s", getLabel() + ":", numberValue.getValue()), x + 2.3F, y - 1F + 6, 0xFFFFFFFF);
    }

    public static int changeAlpha(int origColor, int userInputedAlpha) {
        return userInputedAlpha << 24 | (origColor &= 0xFFFFFF);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isHovering(mouseX, mouseY) && mouseButton == 0) {
            sliding = true;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        super.mouseReleased(mouseX, mouseY, releaseButton);
        if (isHovering(mouseX, mouseY) && releaseButton == 0) {
            sliding = false;
        }
    }

    @Override
    public int getHeight() {
        return 14;
    }

    private boolean isHovering(int mouseX, int mouseY) {
        for (Panel panel : ClickGui.getClickGui().getPanels()) {
            if (panel.drag) {
                return false;
            }
        }
        return mouseX >= getX() && mouseX <= getX() + getWidth() + 8 && mouseY >= getY() && mouseY <= getY() + height;
    }

    private void dragSetting(int mouseX, int mouseY) {
        if(isHovering(mouseX, mouseY) && Mouse.isButtonDown(0)) {
            setSettingFromX(mouseX);
        }
    }

    private void setSettingFromX(int mouseX) {
        float percent = (mouseX - x) / (width + 7.4F);
        if (numberValue.getValue() instanceof Double) {
            double result = (Double) numberValue.getMinimum() + (difference * percent);
            numberValue.setValue(Math.round(25.0 * result) / 25.0);
        } else if (numberValue.getValue() instanceof Float) {
            float result = (Float) numberValue.getMinimum() + (difference * percent);
            numberValue.setValue(Math.round(25.0f * result) / 25.0f);
        } else if (numberValue.getValue() instanceof Integer) {
            numberValue.setValue(((Integer) numberValue.getMinimum() + (int)(difference * percent)));
        }
    }

    private float middle() {
        return max.floatValue() - min.floatValue();
    }

    private float part() {
        return ((Number) numberValue.getValue()).floatValue() - min.floatValue();
    }

    private float partialMultiplier() {
        return part() / middle();
    }
}
