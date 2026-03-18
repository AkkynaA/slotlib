/*
 * This file is a modified version of code originally from Curios API by TheIllusiveC4.
 * Original source: https://github.com/TheIllusiveC4/Curios
 * Licensed under LGPL-3.0-or-later. Modifications are licensed under GPL-3.0-or-later.
 */
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
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.akkynaa.slotlib.SlotLib;
import net.akkynaa.slotlib.client.SlotLibClientConfig;
import net.akkynaa.slotlib.client.SlotLibClientConfig.Client.ButtonCorner;
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

    public SlotLibButton(AbstractContainerScreen<?> parentGui, int xIn, int yIn, int widthIn, int heightIn,
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
                            } else if (mc.player.containerMenu != mc.player.inventoryMenu) {
                                mc.player.connection.send(new ServerboundContainerClosePacket(
                                        mc.player.containerMenu.containerId));
                                mc.player.containerMenu = mc.player.inventoryMenu;
                            }
                            PacketDistributor.sendToServer(new CPacketOpenSlotLib(stack));
                        }
                    }
                });
        this.parentGui = parentGui;
    }

    public static int[] getButtonPosition(AbstractContainerScreen<?> gui) {
        boolean isCreative = gui instanceof CreativeModeInventoryScreen;
        SlotLibClientConfig.Client client = SlotLibClientConfig.CLIENT;
        ButtonCorner corner = client.buttonCorner.get();

        int x, y;
        if (isCreative) {
            x = gui.getGuiLeft() + corner.getCreativeXOffset() + client.creativeButtonXOffset.get();
            y = gui.getGuiTop() + corner.getCreativeYOffset() + client.creativeButtonYOffset.get();
        } else {
            x = gui.getGuiLeft() + corner.getXOffset() + client.buttonXOffset.get();
            y = gui.getGuiTop() + corner.getYOffset() + client.buttonYOffset.get();
        }
        return new int[]{x, y};
    }

    @Override
    public void renderWidget(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY,
                             float partialTicks) {
        int[] pos = getButtonPosition(parentGui);
        this.setX(pos[0]);
        this.setY(pos[1]);

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
