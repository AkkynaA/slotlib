/*
 * This file is a modified version of code originally from Curios API by TheIllusiveC4.
 * Original source: https://github.com/TheIllusiveC4/Curios
 * Licensed under LGPL-3.0-or-later. Modifications are licensed under GPL-3.0-or-later.
 */
package net.akkynaa.slotlib.common.network.server;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.akkynaa.slotlib.common.capability.SlotLibCapabilityProvider;

public class SPacketSyncSlots {

    private final int entityId;
    private final List<ItemStack> stacks;

    public SPacketSyncSlots(int entityId, List<ItemStack> stacks) {
        this.entityId = entityId;
        this.stacks = stacks;
    }

    public SPacketSyncSlots(FriendlyByteBuf buf) {
        RegistryFriendlyByteBuf regBuf = (RegistryFriendlyByteBuf) buf;
        this.entityId = buf.readVarInt();
        int size = buf.readVarInt();
        this.stacks = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            this.stacks.add(ItemStack.OPTIONAL_STREAM_CODEC.decode(regBuf));
        }
    }

    public void encode(FriendlyByteBuf buf) {
        RegistryFriendlyByteBuf regBuf = (RegistryFriendlyByteBuf) buf;
        buf.writeVarInt(this.entityId);
        buf.writeVarInt(this.stacks.size());
        for (ItemStack stack : this.stacks) {
            ItemStack.OPTIONAL_STREAM_CODEC.encode(regBuf, stack);
        }
    }

    public static void handle(SPacketSyncSlots msg, CustomPayloadEvent.Context ctx) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            var entity = mc.level.getEntity(msg.entityId);
            if (entity != null) {
                entity.getCapability(SlotLibCapabilityProvider.INVENTORY_CAP).ifPresent(inv -> {
                    for (int i = 0; i < msg.stacks.size() && i < inv.getSlots(); i++) {
                        inv.setStackInSlot(i, msg.stacks.get(i));
                    }
                });
            }
        }
    }
}
