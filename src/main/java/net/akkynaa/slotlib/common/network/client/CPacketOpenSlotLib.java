/*
 * This file is a modified version of code originally from Curios API by TheIllusiveC4.
 * Original source: https://github.com/TheIllusiveC4/Curios
 * Licensed under LGPL-3.0-or-later. Modifications are licensed under GPL-3.0-or-later.
 */
package net.akkynaa.slotlib.common.network.client;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.akkynaa.slotlib.common.inventory.container.SlotLibContainerProvider;
import net.akkynaa.slotlib.common.network.NetworkHandler;
import net.akkynaa.slotlib.common.network.server.SPacketGrabbedItem;

public class CPacketOpenSlotLib {

    private final ItemStack carried;

    public CPacketOpenSlotLib(ItemStack carried) {
        this.carried = carried;
    }

    public CPacketOpenSlotLib(FriendlyByteBuf buf) {
        this.carried = buf.readItem();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeItem(this.carried);
    }

    public static void handle(CPacketOpenSlotLib msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();

            if (player instanceof ServerPlayer serverPlayer) {
                ItemStack stack =
                        player.isCreative() ? msg.carried : player.containerMenu.getCarried();
                player.containerMenu.setCarried(ItemStack.EMPTY);
                if (player.containerMenu != player.inventoryMenu) {
                    serverPlayer.doCloseContainer();
                }
                player.openMenu(new SlotLibContainerProvider());

                if (!stack.isEmpty()) {
                    player.containerMenu.setCarried(stack);
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer),
                            new SPacketGrabbedItem(stack));
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
