/*
 * This file is a modified version of code originally from Curios API by TheIllusiveC4.
 * Original source: https://github.com/TheIllusiveC4/Curios
 * Licensed under LGPL-3.0-or-later. Modifications are licensed under GPL-3.0-or-later.
 */
package net.akkynaa.slotlib.client.gui;

import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.akkynaa.slotlib.SlotLibConstants;
import net.akkynaa.slotlib.client.SlotLibClientConfig;
import net.akkynaa.slotlib.client.SlotLibClientConfig.Client.ButtonCorner;
import net.akkynaa.slotlib.common.network.NetworkHandler;
import net.akkynaa.slotlib.common.network.client.CPacketOpenSlotLib;
import net.akkynaa.slotlib.common.network.client.CPacketOpenVanilla;
import net.minecraftforge.network.PacketDistributor;

public class SlotLibButton extends Button {

    private static final ResourceLocation BUTTON_TEXTURE =
            new ResourceLocation(SlotLibConstants.MODID, "textures/gui/slotlib/button.png");
    private static final ResourceLocation BUTTON_HIGHLIGHTED =
            new ResourceLocation(SlotLibConstants.MODID, "textures/gui/slotlib/button_highlighted.png");
    private static final ResourceLocation BUTTON_SMALL =
            new ResourceLocation(SlotLibConstants.MODID, "textures/gui/slotlib/button_small.png");
    private static final ResourceLocation BUTTON_SMALL_HIGHLIGHTED =
            new ResourceLocation(SlotLibConstants.MODID, "textures/gui/slotlib/button_small_highlighted.png");

    private final AbstractContainerScreen<?> parentGui;
    private final boolean small;

    public SlotLibButton(AbstractContainerScreen<?> parentGui, int xIn, int yIn,
                         int widthIn, int heightIn, boolean small) {
        super(xIn, yIn, widthIn, heightIn, Component.empty(),
                (button) -> {
                    Minecraft mc = Minecraft.getInstance();

                    if (mc.player != null) {
                        ItemStack stack = mc.player.containerMenu.getCarried();
                        mc.player.containerMenu.setCarried(ItemStack.EMPTY);

                        if (parentGui instanceof SlotLibScreen) {
                            InventoryScreen inventory = new InventoryScreen(mc.player);
                            mc.setScreen(inventory);
                            mc.player.containerMenu.setCarried(stack);
                            NetworkHandler.CHANNEL.send(new CPacketOpenVanilla(stack),
                                    PacketDistributor.SERVER.noArg());
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
                            NetworkHandler.CHANNEL.send(new CPacketOpenSlotLib(stack),
                                    PacketDistributor.SERVER.noArg());
                        }
                    }
                },
                Button.DEFAULT_NARRATION);
        this.parentGui = parentGui;
        this.small = small;
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

        ResourceLocation texture = this.isHovered
                ? (small ? BUTTON_SMALL_HIGHLIGHTED : BUTTON_HIGHLIGHTED)
                : (small ? BUTTON_SMALL : BUTTON_TEXTURE);

        guiGraphics.blit(texture, this.getX(), this.getY(), 0, 0,
                this.width, this.height, this.width, this.height);
    }
}
