/*
 * This file is a modified version of code originally from Curios API by TheIllusiveC4.
 * Original source: https://github.com/TheIllusiveC4/Curios
 * Licensed under LGPL-3.0-or-later. Modifications are licensed under GPL-3.0-or-later.
 */
package net.akkynaa.slotlib.common.network.server;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class SPacketGrabbedItem {

    private final ItemStack stack;

    public SPacketGrabbedItem(ItemStack stack) {
        this.stack = stack;
    }

    public SPacketGrabbedItem(FriendlyByteBuf buf) {
        this.stack = ItemStack.OPTIONAL_STREAM_CODEC.decode((RegistryFriendlyByteBuf) buf);
    }

    public void encode(FriendlyByteBuf buf) {
        ItemStack.OPTIONAL_STREAM_CODEC.encode((RegistryFriendlyByteBuf) buf, this.stack);
    }

    public static void handle(SPacketGrabbedItem msg, CustomPayloadEvent.Context ctx) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            player.containerMenu.setCarried(msg.stack);
        }
    }
}
