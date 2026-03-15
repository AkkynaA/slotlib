package net.akkyna.slotlib.common.network.server;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.akkyna.slotlib.SlotLib;

public record SPacketGrabbedItem(ItemStack stack) implements CustomPacketPayload {

    public static final Type<SPacketGrabbedItem> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SlotLib.MODID, "grabbed_item"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SPacketGrabbedItem> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, packet.stack()),
                    buf -> new SPacketGrabbedItem(ItemStack.OPTIONAL_STREAM_CODEC.decode(buf))
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
