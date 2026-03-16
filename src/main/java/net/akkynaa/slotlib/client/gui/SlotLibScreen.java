package net.akkynaa.slotlib.client.gui;

import com.mojang.blaze3d.platform.InputConstants;

import javax.annotation.Nonnull;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.akkynaa.slotlib.client.KeyRegistry;
import net.akkynaa.slotlib.common.inventory.container.SlotLibContainer;

public class SlotLibScreen extends EffectRenderingInventoryScreen<SlotLibContainer>
        implements RecipeUpdateListener {


    private final RecipeBookComponent recipeBookGui = new RecipeBookComponent();
    public boolean widthTooNarrow;

    private ImageButton recipeBookButton;
    private SlotLibButton buttonSlotLib;

    // Extended height to accommodate slots below
    private int panelHeight = 0;

    public SlotLibScreen(SlotLibContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
    }

    @Override
    public void init() {
        if (this.minecraft != null) {
            this.leftPos = (this.width - this.imageWidth) / 2;
            this.topPos = (this.height - this.imageHeight) / 2;
            this.panelHeight = 32;
            this.widthTooNarrow = true;
            this.recipeBookGui.init(this.width, this.height, this.minecraft, true, this.menu);
            this.addWidget(this.recipeBookGui);
            this.setInitialFocus(this.recipeBookGui);

            if (this.getMinecraft().player != null
                    && this.getMinecraft().player.isCreative()
                    && this.recipeBookGui.isVisible()) {
                this.recipeBookGui.toggleVisibility();
            }

            int[] btnPos = SlotLibButton.getButtonPosition(this);
            this.buttonSlotLib = new SlotLibButton(
                    this,
                    btnPos[0], btnPos[1],
                    10, 10,
                    SlotLibButton.BIG);
            this.addRenderableWidget(this.buttonSlotLib);

            if (!this.menu.player.isCreative()) {
                this.recipeBookButton = new ImageButton(
                        this.leftPos + 104,
                        this.height / 2 - 22,
                        20, 18,
                        RecipeBookComponent.RECIPE_BUTTON_SPRITES,
                        (button) -> {
                            this.recipeBookGui.toggleVisibility();
                            button.setPosition(this.leftPos + 104, this.height / 2 - 22);
                        });
                this.addRenderableWidget(this.recipeBookButton);
            }
        }
    }

    @Override
    public void containerTick() {
        super.containerTick();
        this.recipeBookGui.tick();
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.recipeBookGui.isVisible() && this.widthTooNarrow) {
            this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
            this.recipeBookGui.render(guiGraphics, mouseX, mouseY, partialTicks);
        } else {
            this.recipeBookGui.render(guiGraphics, mouseX, mouseY, partialTicks);
            super.render(guiGraphics, mouseX, mouseY, partialTicks);
            this.recipeBookGui.renderGhostRecipe(
                    guiGraphics, this.leftPos, this.topPos, true, partialTicks);
        }
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (this.minecraft != null && this.minecraft.player != null) {
            guiGraphics.drawString(this.font, this.title, 97, 6, 4210752, false);
        }
    }

    @Override
    public void renderBg(@Nonnull GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        if (this.minecraft != null && this.minecraft.player != null) {
            int i = this.leftPos;
            int j = this.topPos;

            // Draw the standard 176x166 vanilla inventory background
            guiGraphics.blit(INVENTORY_LOCATION, i, j, 0, 0, 176, 166);

            // Render player model
            InventoryScreen.renderEntityInInventoryFollowsMouse(
                    guiGraphics, i + 26, j + 8, i + 75, j + 78, 30, 0.0625F,
                    mouseX, mouseY, this.minecraft.player);

            // Draw the SlotLib panel below the vanilla inventory
            int slotCount = this.menu.getSlotLibSlotCount();
            int panelY = j + 166;

            // left edge
            guiGraphics.blit(INVENTORY_LOCATION, i, j +168, 0, 0, 7, 25); //top left corner
            guiGraphics.blit(INVENTORY_LOCATION, i, j +193, 0, 159, 7, 7); // bottom right corner

            // Draw slot backgrounds
            for (int s = 0; s < slotCount; s++) {
                int slotX = i + 7 + s * 18;
                int slotY = panelY + 9;

                guiGraphics.blit(INVENTORY_LOCATION, slotX, slotY, 7, 83, 18, 18); // slot
                guiGraphics.blit(INVENTORY_LOCATION, slotX, slotY-7, 7, 0, 18, 7); // border up
                guiGraphics.blit(INVENTORY_LOCATION, slotX, slotY+18, 7, 159, 18, 7); // border down

            }

            guiGraphics.blit(INVENTORY_LOCATION, i+7+slotCount*18, j +168, 169, 0, 7, 25); //top left corner
            guiGraphics.blit(INVENTORY_LOCATION, i+7+slotCount*18, j +193, 169, 159, 7, 7); // bottom right corner

        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.recipeBookGui.isVisible() && this.widthTooNarrow) {
            this.recipeBookGui.toggleVisibility();
            return true;
        } else if (KeyRegistry.openSlotLib.isActiveAndMatches(
                InputConstants.getKey(keyCode, scanCode))) {
            LocalPlayer playerEntity = this.getMinecraft().player;
            if (playerEntity != null) {
                playerEntity.closeContainer();
            }
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    @Override
    protected boolean isHovering(int rectX, int rectY, int rectWidth, int rectHeight,
                                 double pointX, double pointY) {
        return (!this.widthTooNarrow || !this.recipeBookGui.isVisible())
                && super.isHovering(rectX, rectY, rectWidth, rectHeight, pointX, pointY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (this.recipeBookGui.mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }
        return this.widthTooNarrow && this.recipeBookGui.isVisible()
                || super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return super.mouseReleased(mouseX, mouseY, button);
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
        return this.recipeBookGui.hasClickedOutside(mouseX, mouseY, this.leftPos, this.topPos,
                this.imageWidth, totalHeight, mouseButton) && flag;
    }

    @Override
    protected void slotClicked(@Nonnull Slot slotIn, int slotId, int mouseButton,
                               @Nonnull ClickType type) {
        super.slotClicked(slotIn, slotId, mouseButton, type);
        this.recipeBookGui.slotClicked(slotIn);
    }

    @Override
    public void recipesUpdated() {
        this.recipeBookGui.recipesUpdated();
    }

    @Nonnull
    @Override
    public RecipeBookComponent getRecipeBookComponent() {
        return this.recipeBookGui;
    }
}
