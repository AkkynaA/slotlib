/*
 * This file is a modified version of code originally from Curios API by TheIllusiveC4.
 * Original source: https://github.com/TheIllusiveC4/Curios
 * Licensed under LGPL-3.0-or-later. Modifications are licensed under GPL-3.0-or-later.
 */
package net.akkynaa.slotlib.common.network.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.PacketDistributor;
import net.akkynaa.slotlib.common.network.NetworkHandler;
import net.akkynaa.slotlib.common.network.server.SPacketGrabbedItem;

public class CPacketOpenVanilla {

    private final ItemStack carried;

    public CPacketOpenVanilla(ItemStack carried) {
        this.carried = carried;
    }

    public CPacketOpenVanilla(FriendlyByteBuf buf) {
        this.carried = ItemStack.OPTIONAL_STREAM_CODEC.decode((RegistryFriendlyByteBuf) buf);
    }

    public void encode(FriendlyByteBuf buf) {
        ItemStack.OPTIONAL_STREAM_CODEC.encode((RegistryFriendlyByteBuf) buf, this.carried);
    }

    public static void handle(CPacketOpenVanilla msg, CustomPayloadEvent.Context ctx) {
        Player player = ctx.getSender();

        if (player instanceof ServerPlayer serverPlayer) {
            ItemStack stack =
                    player.isCreative() ? msg.carried : player.containerMenu.getCarried();
            player.containerMenu.setCarried(ItemStack.EMPTY);
            serverPlayer.doCloseContainer();

            if (!stack.isEmpty()) {
                if (!player.isCreative()) {
                    player.containerMenu.setCarried(stack);
                }
                NetworkHandler.CHANNEL.send(new SPacketGrabbedItem(stack),
                        PacketDistributor.PLAYER.with(serverPlayer));
            }
        }
    }
}
