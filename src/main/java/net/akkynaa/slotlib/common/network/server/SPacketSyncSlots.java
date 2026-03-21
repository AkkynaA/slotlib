/*
 * This file is a modified version of code originally from Curios API by TheIllusiveC4.
 * Original source: https://github.com/TheIllusiveC4/Curios
 * Licensed under LGPL-3.0-or-later. Modifications are licensed under GPL-3.0-or-later.
 */
package net.akkynaa.slotlib.common.network.server;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.akkynaa.slotlib.common.capability.SlotLibCapabilityProvider;

public class SPacketSyncSlots {

    private final int entityId;
    private final List<ItemStack> stacks;

    public SPacketSyncSlots(int entityId, List<ItemStack> stacks) {
        this.entityId = entityId;
        this.stacks = stacks;
    }

    public SPacketSyncSlots(FriendlyByteBuf buf) {
        this.entityId = buf.readVarInt();
        int size = buf.readVarInt();
        this.stacks = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            this.stacks.add(buf.readItem());
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(this.entityId);
        buf.writeVarInt(this.stacks.size());
        for (ItemStack stack : this.stacks) {
            buf.writeItem(stack);
        }
    }

    public static void handle(SPacketSyncSlots msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
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
        });
        ctx.get().setPacketHandled(true);
    }
}
