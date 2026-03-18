/*
 * This file is a modified version of code originally from Curios API by TheIllusiveC4.
 * Original source: https://github.com/TheIllusiveC4/Curios
 * Licensed under LGPL-3.0-or-later. Modifications are licensed under GPL-3.0-or-later.
 */
package net.akkynaa.slotlib.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.akkynaa.slotlib.common.network.client.CPacketOpenSlotLib;

public class ClientEventHandler {

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post evt) {
        if (KeyRegistry.openSlotLib.consumeClick() && Minecraft.getInstance().isWindowActive()) {
            PacketDistributor.sendToServer(new CPacketOpenSlotLib(ItemStack.EMPTY));
        }
    }
}
