/*
 * This file is a modified version of code originally from Curios API by TheIllusiveC4.
 * Original source: https://github.com/TheIllusiveC4/Curios
 * Licensed under LGPL-3.0-or-later. Modifications are licensed under GPL-3.0-or-later.
 */
package net.akkynaa.slotlib.common.network.server;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.akkynaa.slotlib.SlotLib;

public record SPacketSyncSlots(int entityId, List<ItemStack> stacks) implements CustomPacketPayload {

    public static final Type<SPacketSyncSlots> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(SlotLib.MODID, "sync_slots"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SPacketSyncSlots> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> {
                        buf.writeVarInt(packet.entityId());
                        buf.writeVarInt(packet.stacks().size());
                        for (ItemStack stack : packet.stacks()) {
                            ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, stack);
                        }
                    },
                    buf -> {
                        int entityId = buf.readVarInt();
                        int size = buf.readVarInt();
                        List<ItemStack> stacks = new ArrayList<>(size);
                        for (int i = 0; i < size; i++) {
                            stacks.add(ItemStack.OPTIONAL_STREAM_CODEC.decode(buf));
                        }
                        return new SPacketSyncSlots(entityId, stacks);
                    }
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
