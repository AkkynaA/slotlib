package net.akkyna.slotlib.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.akkyna.slotlib.SlotLib;
import net.akkyna.slotlib.client.KeyRegistry;
import net.akkyna.slotlib.common.inventory.container.SlotLibContainer;

public class SlotLibScreen extends EffectRenderingInventoryScreen<SlotLibContainer>
        implements RecipeUpdateListener {

    static final ResourceLocation SLOTLIB_INVENTORY =
            ResourceLocation.fromNamespaceAndPath(SlotLib.MODID, "textures/gui/slotlib/inventory.png");

    private final RecipeBookComponent recipeBookGui = new RecipeBookComponent();
    public boolean widthTooNarrow;

    private ImageButton recipeBookButton;
    private SlotLibButton buttonSlotLib;

    // Extended height to accommodate slots below
    private int panelHeight = 0;

    public SlotLibScreen(SlotLibContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        // Extend the image height to accommodate extra slot row below
        this.imageHeight = 166 + 32; // 32 extra pixels for the slot panel below
    }

    @Override
    public void init() {
        if (this.minecraft != null) {
            this.panelHeight = 32;
            this.leftPos = (this.width - 176) / 2;
            this.topPos = (this.height - this.imageHeight) / 2;
            this.widthTooNarrow = true;
            this.recipeBookGui.init(this.width, this.height, this.minecraft, true, this.menu);
            this.addWidget(this.recipeBookGui);
            this.setInitialFocus(this.recipeBookGui);

            if (this.getMinecraft().player != null
                    && this.getMinecraft().player.isCreative()
                    && this.recipeBookGui.isVisible()) {
                this.recipeBookGui.toggleVisibility();
            }

            // Button position - near the crafting output area
            this.buttonSlotLib = new SlotLibButton(
                    this,
                    this.getGuiLeft() + 126,
                    this.getGuiTop() + 63,
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
            int panelX = i;
            int panelY = j + 166;

            // Draw panel background - a simple bordered panel
            // Top border
            guiGraphics.fill(panelX, panelY, panelX + 176, panelY + 1, 0xFFC6C6C6);
            // Background fill
            guiGraphics.fill(panelX, panelY + 1, panelX + 176, panelY + 31, 0xFFC6C6C6);
            // Bottom border
            guiGraphics.fill(panelX, panelY + 31, panelX + 176, panelY + 32, 0xFF555555);
            // Left border
            guiGraphics.fill(panelX, panelY, panelX + 1, panelY + 32, 0xFFFFFFFF);
            // Right border
            guiGraphics.fill(panelX + 175, panelY, panelX + 176, panelY + 32, 0xFF555555);

            // Draw slot backgrounds
            for (int s = 0; s < slotCount; s++) {
                int slotX = i + 7 + s * 18;
                int slotY = panelY + 5;
                // Slot border (dark)
                guiGraphics.fill(slotX, slotY, slotX + 18, slotY + 18, 0xFF8B8B8B);
                // Slot inner (darker)
                guiGraphics.fill(slotX + 1, slotY + 1, slotX + 17, slotY + 17, 0xFF373737);
                // Slot highlight (lighter borders for 3D effect)
                guiGraphics.fill(slotX, slotY, slotX + 17, slotY + 1, 0xFF373737);
                guiGraphics.fill(slotX, slotY, slotX + 1, slotY + 17, 0xFF373737);
                guiGraphics.fill(slotX + 17, slotY, slotX + 18, slotY + 18, 0xFFFFFFFF);
                guiGraphics.fill(slotX, slotY + 17, slotX + 18, slotY + 18, 0xFFFFFFFF);
            }

            // Draw the "SlotLib" label
            guiGraphics.drawString(this.font,
                    Component.translatable("slotlib.gui.title"),
                    panelX + 8, panelY - 10, 4210752, false);
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
        boolean flag = mouseX < guiLeftIn
                || mouseY < guiTopIn
                || mouseX >= guiLeftIn + this.imageWidth
                || mouseY >= guiTopIn + this.imageHeight;
        return this.recipeBookGui.hasClickedOutside(mouseX, mouseY, this.leftPos, this.topPos,
                this.imageWidth, this.imageHeight, mouseButton) && flag;
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
