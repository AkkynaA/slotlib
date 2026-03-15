package net.akkyna.slotlib.common.network.client;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.akkyna.slotlib.SlotLib;

public record CPacketOpenSlotLib(ItemStack carried) implements CustomPacketPayload {

    public static final Type<CPacketOpenSlotLib> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SlotLib.MODID, "open_slotlib"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CPacketOpenSlotLib> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, packet.carried()),
                    buf -> new CPacketOpenSlotLib(ItemStack.OPTIONAL_STREAM_CODEC.decode(buf))
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
