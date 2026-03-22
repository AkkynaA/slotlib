/*
 * This file is a modified version of code originally from Curios API by TheIllusiveC4.
 * Original source: https://github.com/TheIllusiveC4/Curios
 * Licensed under LGPL-3.0-or-later. Modifications are licensed under GPL-3.0-or-later.
 */
package net.akkynaa.slotlib.client.gui;

import com.mojang.blaze3d.platform.InputConstants;

import javax.annotation.Nonnull;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.CraftingRecipeBookComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.akkynaa.slotlib.client.KeyRegistry;
import net.akkynaa.slotlib.client.compat.BackpackClientCompat;
import net.akkynaa.slotlib.client.compat.CuriosCompat;
import net.akkynaa.slotlib.compat.BackpackCompat;
import net.akkynaa.slotlib.common.inventory.container.SlotLibContainer;
import net.neoforged.fml.ModList;

public class SlotLibScreen extends AbstractRecipeBookScreen<SlotLibContainer> {

    private SlotLibButton buttonSlotLib;

    // Extended height to accommodate slots below
    private int panelHeight = 0;

    public SlotLibScreen(SlotLibContainer container, Inventory playerInventory, Component title) {
        super(container, new CraftingRecipeBookComponent(container), playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.panelHeight = 32;

        int[] btnPos = SlotLibButton.getButtonPosition(this);
        this.buttonSlotLib = new SlotLibButton(
                this,
                btnPos[0], btnPos[1],
                10, 10,
                SlotLibButton.BIG);
        this.addRenderableWidget(this.buttonSlotLib);

        if (ModList.get().isLoaded("curios")) {
            this.addRenderableWidget(CuriosCompat.createCuriosButtonForSlotLibScreen(this));
        }
    }

    @Override
    protected ScreenPosition getRecipeBookButtonPosition() {
        return new ScreenPosition(this.leftPos + 104, this.height / 2 - 22);
    }

    @Override
    protected boolean isBiggerResultSlot() {
        return false;
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (ModList.get().isLoaded("yyzsbackpack")) {
            BackpackCompat.setBackpackVisible(this.menu, true);
            BackpackCompat.setBackpackGuiPos(this.menu, 0, 0);
        }
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void renderLabels(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (this.minecraft != null && this.minecraft.player != null) {
            guiGraphics.drawString(this.font, this.title, 97, 6, 4210752, false);
        }
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        if (this.minecraft != null && this.minecraft.player != null) {
            int i = this.leftPos;
            int j = this.topPos;

            // Draw the standard 176x166 vanilla inventory background
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, INVENTORY_LOCATION, i, j, 0, 0, 176, 166, 256, 256);

            // Render player model
            InventoryScreen.renderEntityInInventoryFollowsMouse(
                    guiGraphics, i + 26, j + 8, i + 75, j + 78, 30, 0.0625F,
                    mouseX, mouseY, this.minecraft.player);

            // Draw the SlotLib panel below the vanilla inventory
            int slotCount = this.menu.getSlotLibSlotCount();
            int panelY = j + 166;

            // left edge
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, INVENTORY_LOCATION, i, j + 168, 0, 0, 7, 25, 256, 256);
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, INVENTORY_LOCATION, i, j + 193, 0, 159, 7, 7, 256, 256);

            // Draw slot backgrounds
            for (int s = 0; s < slotCount; s++) {
                int slotX = i + 7 + s * 18;
                int slotY = panelY + 9;

                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, INVENTORY_LOCATION, slotX, slotY, 7, 83, 18, 18, 256, 256);
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, INVENTORY_LOCATION, slotX, slotY - 7, 7, 0, 18, 7, 256, 256);
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, INVENTORY_LOCATION, slotX, slotY + 18, 7, 159, 18, 7, 256, 256);
            }

            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, INVENTORY_LOCATION, i + 7 + slotCount * 18, j + 168, 169, 0, 7, 25, 256, 256);
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, INVENTORY_LOCATION, i + 7 + slotCount * 18, j + 193, 169, 159, 7, 7, 256, 256);

            // Render backpack equip slot background if yyzsbackpack is loaded
            if (ModList.get().isLoaded("yyzsbackpack")) {
                BackpackClientCompat.renderEquipSlotBackground(this.menu, guiGraphics, i, j);
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (KeyRegistry.openSlotLib.isActiveAndMatches(
                InputConstants.getKey(keyCode, scanCode))) {
            LocalPlayer playerEntity = this.getMinecraft().player;
            if (playerEntity != null) {
                playerEntity.closeContainer();
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeftIn, int guiTopIn,
                                        int mouseButton) {
        // Include the panel below the vanilla inventory in the "inside" area
        int totalHeight = this.imageHeight + this.panelHeight;
        boolean flag = mouseX < guiLeftIn
                || mouseY < guiTopIn
                || mouseX >= guiLeftIn + this.imageWidth
                || mouseY >= guiTopIn + totalHeight;
        return super.hasClickedOutside(mouseX, mouseY, guiLeftIn, guiTopIn, mouseButton) && flag;
    }
}
