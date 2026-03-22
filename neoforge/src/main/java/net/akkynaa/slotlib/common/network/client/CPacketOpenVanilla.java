/*
 * This file is a modified version of code originally from Curios API by TheIllusiveC4.
 * Original source: https://github.com/TheIllusiveC4/Curios
 * Licensed under LGPL-3.0-or-later. Modifications are licensed under GPL-3.0-or-later.
 */
package net.akkynaa.slotlib.common.network.client;

import javax.annotation.Nonnull;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.akkynaa.slotlib.SlotLibConstants;

public record CPacketOpenVanilla(ItemStack carried) implements CustomPacketPayload {

    public static final Type<CPacketOpenVanilla> TYPE =
            new Type<>(new ResourceLocation(SlotLibConstants.MODID, "open_vanilla"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CPacketOpenVanilla> STREAM_CODEC =
            StreamCodec.composite(
                    ItemStack.OPTIONAL_STREAM_CODEC,
                    CPacketOpenVanilla::carried,
                    CPacketOpenVanilla::new);

    @Nonnull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
