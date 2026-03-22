/*
 * This file is a modified version of code originally from Curios API by TheIllusiveC4.
 * Original source: https://github.com/TheIllusiveC4/Curios
 * Licensed under LGPL-3.0-or-later. Modifications are licensed under GPL-3.0-or-later.
 */
package net.akkynaa.slotlib.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.akkynaa.slotlib.common.SlotLibRegistry;
import net.akkynaa.slotlib.common.network.server.SPacketGrabbedItem;
import net.akkynaa.slotlib.common.network.server.SPacketSyncSlots;

public class ClientPayloadHandler {

    private static final ClientPayloadHandler INSTANCE = new ClientPayloadHandler();

    public static ClientPayloadHandler getInstance() {
        return INSTANCE;
    }

    public void handleSyncSlots(final SPacketSyncSlots data, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null) {
                var entity = mc.level.getEntity(data.entityId());
                if (entity != null) {
                    var inv = entity.getData(SlotLibRegistry.INVENTORY);
                    for (int i = 0; i < data.stacks().size() && i < inv.getSlots(); i++) {
                        inv.setStackInSlot(i, data.stacks().get(i));
                    }
                }
            }
        });
    }

    public void handleGrabbedItem(final SPacketGrabbedItem data, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                player.containerMenu.setCarried(data.stack());
            }
        });
    }
}
