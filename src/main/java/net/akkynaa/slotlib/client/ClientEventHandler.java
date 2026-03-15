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
