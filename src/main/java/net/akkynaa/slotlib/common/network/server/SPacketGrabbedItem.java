/*
 * This file is a modified version of code originally from Curios API by TheIllusiveC4.
 * Original source: https://github.com/TheIllusiveC4/Curios
 * Licensed under LGPL-3.0-or-later. Modifications are licensed under GPL-3.0-or-later.
 */
package net.akkynaa.slotlib.common.network.server;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class SPacketGrabbedItem {

    private final ItemStack stack;

    public SPacketGrabbedItem(ItemStack stack) {
        this.stack = stack;
    }

    public SPacketGrabbedItem(FriendlyByteBuf buf) {
        this.stack = buf.readItem();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeItem(this.stack);
    }

    public static void handle(SPacketGrabbedItem msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                player.containerMenu.setCarried(msg.stack);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
