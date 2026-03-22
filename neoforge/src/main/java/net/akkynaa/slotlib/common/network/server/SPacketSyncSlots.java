/*
 * This file is a modified version of code originally from Curios API by TheIllusiveC4.
 * Original source: https://github.com/TheIllusiveC4/Curios
 * Licensed under LGPL-3.0-or-later. Modifications are licensed under GPL-3.0-or-later.
 */
package net.akkynaa.slotlib.common.network.server;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.akkynaa.slotlib.SlotLibConstants;

public record SPacketSyncSlots(int entityId, List<ItemStack> stacks) implements CustomPacketPayload {

    public static final Type<SPacketSyncSlots> TYPE =
            new Type<>(new ResourceLocation(SlotLibConstants.MODID, "sync_slots"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SPacketSyncSlots> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, SPacketSyncSlots::entityId,
                    ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list()), SPacketSyncSlots::stacks,
                    SPacketSyncSlots::new);

    @Nonnull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
