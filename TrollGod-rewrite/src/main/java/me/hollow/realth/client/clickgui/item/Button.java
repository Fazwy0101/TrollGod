package me.hollow.realth.client.clickgui.item;


import me.hollow.realth.Realth;
import me.hollow.realth.api.utils.RenderUtil;
import me.hollow.realth.client.clickgui.ClickGui;
import me.hollow.realth.client.clickgui.Panel;
import me.hollow.realth.client.module.modules.other.ClickGuiMod;
import me.hollow.realth.client.other.ColorHandler;

/**
 * @author TehNeon
 * @author nuf
 */
public class Button extends Item {
    private boolean state;

    public Button(String label) {
        super(label);
        this.height = 15;
    }

    public static int getColor(boolean hovered) {
        return changeAlpha(ColorHandler.getColorInt(), hovered ? ClickGuiMod.INSTANCE.alpha.getValue() : 120);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(x, y, x + width, y + height, getState() ? getColor(!isHovering(mouseX, mouseY)) : !isHovering(mouseX, mouseY) ? 0x55555555 : 0x99555555);
        Realth.INSTANCE.getFontManager().drawString(getLabel(), x + 2.3F, y - 2F + 6, getState() ? 0xFFFFFFFF : 0xFFAAAAAA);
    }

    public static int changeAlpha(int origColor, int userInputedAlpha) {
        return userInputedAlpha << 24 | (origColor &= 0xFFFFFF);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && isHovering(mouseX, mouseY)) {
            state = !state;
            toggle();
            //Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("random.click"), 1F));
        }
    }

    public void toggle() {

    }

    public boolean getState() {
        return state;
    }

    @Override
    public int getHeight() {
        return 14;
    }

    protected boolean isHovering(int mouseX, int mouseY) {
        for (Panel panel : ClickGui.getClickGui().getPanels()) {
            if (panel.drag) {
                return false;
            }
        }
        return mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() && mouseY <= getY() + height;
    }
}