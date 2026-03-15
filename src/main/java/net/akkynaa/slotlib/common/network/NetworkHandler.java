package net.akkynaa.slotlib.common.network;

import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.akkynaa.slotlib.common.network.client.CPacketOpenSlotLib;
import net.akkynaa.slotlib.common.network.client.CPacketOpenVanilla;
import net.akkynaa.slotlib.common.network.server.SlotLibServerPayloadHandler;
import net.akkynaa.slotlib.common.network.server.SPacketSyncSlots;
import net.akkynaa.slotlib.common.network.server.SPacketGrabbedItem;
import net.akkynaa.slotlib.common.network.client.SlotLibClientPayloadHandler;

public class NetworkHandler {

    public static void register(final PayloadRegistrar registrar) {
        // Client -> Server
        registrar.playToServer(CPacketOpenSlotLib.TYPE, CPacketOpenSlotLib.STREAM_CODEC,
                SlotLibServerPayloadHandler.getInstance()::handleOpenSlotLib);
        registrar.playToServer(CPacketOpenVanilla.TYPE, CPacketOpenVanilla.STREAM_CODEC,
                SlotLibServerPayloadHandler.getInstance()::handleOpenVanilla);

        // Server -> Client
        registrar.playToClient(SPacketSyncSlots.TYPE, SPacketSyncSlots.STREAM_CODEC,
                SlotLibClientPayloadHandler.getInstance()::handleSyncSlots);
        registrar.playToClient(SPacketGrabbedItem.TYPE, SPacketGrabbedItem.STREAM_CODEC,
                SlotLibClientPayloadHandler.getInstance()::handleGrabbedItem);
    }
}
