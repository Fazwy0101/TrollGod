package me.hollow.realth.api.mixin.mixins;

import me.hollow.realth.client.module.modules.visual.ShulkerViewer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiScreen.class)
public class MixinGuiScreen {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final ResourceLocation SHULKER_TEXTURE = new ResourceLocation("textures/gui/container/shulker_box.png");
    private final RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();
    private final FontRenderer fontRenderer = mc.fontRenderer;

    @Inject(method = "renderToolTip", at = @At("HEAD"), cancellable = true)
    public void renderToolTip(ItemStack stack, int x, int y, CallbackInfo info) {
        if (ShulkerViewer.INSTANCE.isEnabled() && stack.getItem() instanceof ItemShulkerBox) {
            NBTTagCompound tagCompound = stack.getTagCompound();
            NBTTagCompound blockEntityTag = null;
            if (tagCompound != null) {
                blockEntityTag = tagCompound.getCompoundTag("BlockEntityTag");
            }
            info.cancel();
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.color(1, 1, 1);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            mc.getTextureManager().bindTexture(SHULKER_TEXTURE);
            y -= 15;
            final int DEPTH = 500;
            drawTexturedRect(x, y, 0, 0, 176, 16, DEPTH);
            drawTexturedRect(x, y + 16, 0, 16, 176, 54, DEPTH);
            drawTexturedRect(x, y + 16 + 54, 0, 160, 176, 8, DEPTH);
            GlStateManager.disableDepth();
            fontRenderer.drawStringWithShadow(stack.getDisplayName(), x + 8, y + 6, 0xFFFFFF);
            GlStateManager.enableDepth();
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableColorMaterial();
            GlStateManager.enableLighting();
            if (tagCompound != null) {
                final NonNullList<ItemStack> nonnulllist = NonNullList.withSize(27, ItemStack.EMPTY);
                ItemStackHelper.loadAllItems(blockEntityTag, nonnulllist);
                final int size = nonnulllist.size();
                for (int i = 0; i < size; ++i) {
                    final int iX = x + (i % 9) * 18 + 8;
                    final int iY = y + (i / 9) * 18 + 18;
                    final ItemStack itemStack = nonnulllist.get(i);
                    mc.getRenderItem().zLevel = DEPTH + 1;
                    itemRender.renderItemAndEffectIntoGUI(itemStack, iX, iY);
                    itemRender.renderItemOverlayIntoGUI(fontRenderer, itemStack, iX, iY, null);
                    mc.getRenderItem().zLevel = 0.f;
                }
            }

            GlStateManager.disableLighting();
            GlStateManager.disableBlend();
            GlStateManager.color(1, 1, 1);
        }
    }

    private static void drawTexturedRect(int x, int y, int textureX, int textureY, int width, int height, int zLevel) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder BufferBuilder = tessellator.getBuffer();
        BufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        BufferBuilder.pos(x, y + height, zLevel).tex((float) (textureX) * 0.00390625F, (float) (textureY + height) * 0.00390625F).endVertex();
        BufferBuilder.pos(x + width, y + height, zLevel).tex((float) (textureX + width) * 0.00390625F, (float) (textureY + height) * 0.00390625F).endVertex();
        BufferBuilder.pos(x + width, y, zLevel).tex((float) (textureX + width) * 0.00390625F, (float) (textureY) * 0.00390625F).endVertex();
        BufferBuilder.pos(x, y, zLevel).tex((float) (textureX) * 0.00390625F, (float) (textureY) * 0.00390625F).endVertex();
        tessellator.draw();
    }

}
