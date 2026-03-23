/*
 * This file is a modified version of code originally from Curios API by TheIllusiveC4.
 * Original source: https://github.com/TheIllusiveC4/Curios
 * Licensed under LGPL-3.0-or-later. Modifications are licensed under GPL-3.0-or-later.
 */
package net.akkynaa.slotlib.common.network.client;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.akkynaa.slotlib.SlotLib;

public record CPacketOpenVanilla(ItemStack carried) implements CustomPacketPayload {

    public static final Type<CPacketOpenVanilla> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(SlotLib.MODID, "open_vanilla"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CPacketOpenVanilla> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, packet.carried()),
                    buf -> new CPacketOpenVanilla(ItemStack.OPTIONAL_STREAM_CODEC.decode(buf))
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
