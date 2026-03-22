/*
 * This file is a modified version of code originally from Curios API by TheIllusiveC4.
 * Original source: https://github.com/TheIllusiveC4/Curios
 * Licensed under LGPL-3.0-or-later. Modifications are licensed under GPL-3.0-or-later.
 */
package net.akkynaa.slotlib.common.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.akkynaa.slotlib.common.inventory.container.SlotLibContainerProvider;
import net.akkynaa.slotlib.common.network.client.CPacketOpenSlotLib;
import net.akkynaa.slotlib.common.network.client.CPacketOpenVanilla;
import net.akkynaa.slotlib.common.network.server.SPacketGrabbedItem;

public class ServerPayloadHandler {

    private static final ServerPayloadHandler INSTANCE = new ServerPayloadHandler();

    public static ServerPayloadHandler getInstance() {
        return INSTANCE;
    }

    public void handleOpenSlotLib(final CPacketOpenSlotLib data, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();

            if (player instanceof ServerPlayer serverPlayer) {
                ItemStack stack =
                        player.isCreative() ? data.carried() : player.containerMenu.getCarried();
                player.containerMenu.setCarried(ItemStack.EMPTY);
                if (player.containerMenu != player.inventoryMenu) {
                    serverPlayer.doCloseContainer();
                }
                player.openMenu(new SlotLibContainerProvider());

                if (!stack.isEmpty()) {
                    player.containerMenu.setCarried(stack);
                    PacketDistributor.sendToPlayer(serverPlayer, new SPacketGrabbedItem(stack));
                }
            }
        });
    }

    public void handleOpenVanilla(final CPacketOpenVanilla data, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();

            if (player instanceof ServerPlayer serverPlayer) {
                ItemStack stack =
                        player.isCreative() ? data.carried() : player.containerMenu.getCarried();
                player.containerMenu.setCarried(ItemStack.EMPTY);
                serverPlayer.doCloseContainer();

                if (!stack.isEmpty()) {
                    if (!player.isCreative()) {
                        player.containerMenu.setCarried(stack);
                    }
                    PacketDistributor.sendToPlayer(serverPlayer, new SPacketGrabbedItem(stack));
                }
            }
        });
    }
}
