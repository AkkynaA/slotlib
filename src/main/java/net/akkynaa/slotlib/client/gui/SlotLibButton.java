package net.akkynaa.slotlib.client.gui;

import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.akkynaa.slotlib.SlotLib;
import net.akkynaa.slotlib.common.network.client.CPacketOpenSlotLib;
import net.akkynaa.slotlib.common.network.client.CPacketOpenVanilla;

public class SlotLibButton extends ImageButton {

    public static final WidgetSprites BIG =
            new WidgetSprites(
                    ResourceLocation.fromNamespaceAndPath(SlotLib.MODID, "button"),
                    ResourceLocation.fromNamespaceAndPath(SlotLib.MODID, "button_highlighted"));
    public static final WidgetSprites SMALL =
            new WidgetSprites(
                    ResourceLocation.fromNamespaceAndPath(SlotLib.MODID, "button_small"),
                    ResourceLocation.fromNamespaceAndPath(SlotLib.MODID, "button_small_highlighted"));

    private final AbstractContainerScreen<?> parentGui;

    SlotLibButton(AbstractContainerScreen<?> parentGui, int xIn, int yIn, int widthIn, int heightIn,
                  WidgetSprites sprites) {
        super(xIn, yIn, widthIn, heightIn, sprites,
                (button) -> {
                    Minecraft mc = Minecraft.getInstance();

                    if (mc.player != null) {
                        ItemStack stack = mc.player.containerMenu.getCarried();
                        mc.player.containerMenu.setCarried(ItemStack.EMPTY);

                        if (parentGui instanceof SlotLibScreen) {
                            InventoryScreen inventory = new InventoryScreen(mc.player);
                            mc.setScreen(inventory);
                            mc.player.containerMenu.setCarried(stack);
                            PacketDistributor.sendToServer(new CPacketOpenVanilla(stack));
                        } else {
                            if (parentGui instanceof InventoryScreen inventory) {
                                RecipeBookComponent recipeBookGui = inventory.getRecipeBookComponent();
                                if (recipeBookGui.isVisible()) {
                                    recipeBookGui.toggleVisibility();
                                }
                            }
                            PacketDistributor.sendToServer(new CPacketOpenSlotLib(stack));
                        }
                    }
                });
        this.parentGui = parentGui;
    }

    @Override
    public void renderWidget(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY,
                             float partialTicks) {

        int xOffset = parentGui instanceof CreativeModeInventoryScreen ? 95 : 63;
        this.setX(parentGui.getGuiLeft() + xOffset);
        int yOffset = parentGui instanceof CreativeModeInventoryScreen ? 39 : 66;
        this.setY(parentGui.getGuiTop() + yOffset);

        if (parentGui instanceof CreativeModeInventoryScreen gui) {
            boolean isInventoryTab = gui.isInventoryOpen();
            this.active = isInventoryTab;
            if (!isInventoryTab) {
                return;
            }
        }
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);
    }
}
