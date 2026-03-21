/*
 * This file is a modified version of code originally from Curios API by TheIllusiveC4.
 * Original source: https://github.com/TheIllusiveC4/Curios
 * Licensed under LGPL-3.0-or-later. Modifications are licensed under GPL-3.0-or-later.
 */
package net.akkynaa.slotlib.common.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;
import net.akkynaa.slotlib.SlotLib;
import net.akkynaa.slotlib.common.network.client.CPacketOpenSlotLib;
import net.akkynaa.slotlib.common.network.client.CPacketOpenVanilla;
import net.akkynaa.slotlib.common.network.server.SPacketGrabbedItem;
import net.akkynaa.slotlib.common.network.server.SPacketSyncSlots;

public class NetworkHandler {

    public static final SimpleChannel CHANNEL = ChannelBuilder
            .named(ResourceLocation.fromNamespaceAndPath(SlotLib.MODID, "main"))
            .networkProtocolVersion(1)
            .simpleChannel();

    public static void register() {
        CHANNEL.messageBuilder(CPacketOpenSlotLib.class)
                .encoder(CPacketOpenSlotLib::encode)
                .decoder(CPacketOpenSlotLib::new)
                .consumerMainThread(CPacketOpenSlotLib::handle)
                .add();
        CHANNEL.messageBuilder(CPacketOpenVanilla.class)
                .encoder(CPacketOpenVanilla::encode)
                .decoder(CPacketOpenVanilla::new)
                .consumerMainThread(CPacketOpenVanilla::handle)
                .add();
        CHANNEL.messageBuilder(SPacketSyncSlots.class)
                .encoder(SPacketSyncSlots::encode)
                .decoder(SPacketSyncSlots::new)
                .consumerMainThread(SPacketSyncSlots::handle)
                .add();
        CHANNEL.messageBuilder(SPacketGrabbedItem.class)
                .encoder(SPacketGrabbedItem::encode)
                .decoder(SPacketGrabbedItem::new)
                .consumerMainThread(SPacketGrabbedItem::handle)
                .add();
    }
}
