package me.hollow.realth.client.clickgui;

import me.hollow.realth.Realth;
import me.hollow.realth.api.utils.RenderUtil;
import me.hollow.realth.client.clickgui.item.Button;
import me.hollow.realth.client.clickgui.item.Item;
import me.hollow.realth.client.other.ColorHandler;

import java.util.ArrayList;

/**
 * @author TehNeon
 * @author nuf
 */
public abstract class Panel {
    private final String label;
    private int x;
    private int y;
    private int x2;
    private int y2;
    private final int width;
    private final int height;
    private boolean open;
    public boolean drag;
    private final ArrayList<Item> items = new ArrayList<>();

    public Panel(String label, int x, int y, boolean open) {
        this.label = label;
        this.x = x;
        this.y = y;
        this.width = 88;
        this.height = 18;
        this.open = open;
        setupItems();
    }

    /**
     * dont remove, actually has a use (ClickGui.java)
     */
    public abstract void setupItems();

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drag(mouseX, mouseY);
        float totalItemHeight = open ? getTotalItemHeight() - 2F : 0F;
        RenderUtil.drawRect(x, y - 1.5F, (x + width), y + height - 6, ColorHandler.getColorInt());
        if (open) {
            RenderUtil.drawRect(x, y + 12.5F, (x + width), y + height + totalItemHeight, 0x77000000);
        }
        Realth.INSTANCE.getFontManager().drawString(label, x + 3F, y - 5F + 6, 0xFFFFFFFF);
        if (open) {
            float y = this.y + height - 3F;
            for (Item item : items) {
                item.setLocation(x + 2F, y);
                item.setWidth(width - 4);
                item.drawScreen(mouseX, mouseY, partialTicks);
                y += item.getHeight() + 1.5F;
            }
        }
    }

    private void drag(int mouseX, int mouseY) {
        if (!drag) {
            return;
        }
        x = x2 + mouseX;
        y = y2 + mouseY;
    }

    public void onKeyTyped(char typedChar, int keyCode) {
        items.forEach(item -> item.onKeyTyped(typedChar, keyCode));
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && isHovering(mouseX, mouseY)) {
            x2 = x - mouseX;
            y2 = y - mouseY;
            ClickGui.getClickGui().getPanels().forEach(panel -> {
                if (panel.drag) {
                    panel.drag = false;
                }
            });
            drag = true;
            return;
        }
        if (mouseButton == 1 && isHovering(mouseX, mouseY)) {
            open = !open;
           // Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("random.click"), 1F));
            return;
        }
        if (!open) {
            return;
        }
        items.forEach(item -> item.mouseClicked(mouseX, mouseY, mouseButton));
    }

    public void addButton(Button button) {
        items.add(button);
    }

    public void mouseReleased(final int mouseX, int mouseY, int releaseButton) {
        if (releaseButton == 0)
            drag = false;
        if (!open) {
            return;
        }
        items.forEach(item -> item.mouseReleased(mouseX, mouseY, releaseButton));
    }


    public final String getLabel() {
        return label;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean getOpen() {
        return open;
    }

    public final ArrayList<Item> getItems() {
        return items;
    }

    private boolean isHovering(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + getHeight() - (open ? 2 : 0);
    }

    private float getTotalItemHeight() {
        float height = 0;
        for (Item item : getItems()) {
            height += item.getHeight() + 1.5F;
        }
        return height;
    }

    public void setX(int dragX) {
        this.x = dragX;
    }

    public void setY(int dragY) {
        this.y = dragY;
    }
}
