package net.akkyna.slotlib.common.network.server;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.akkyna.slotlib.SlotLib;

public record SPacketSyncSlots(int entityId, List<ItemStack> stacks) implements CustomPacketPayload {

    public static final Type<SPacketSyncSlots> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SlotLib.MODID, "sync_slots"));

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
