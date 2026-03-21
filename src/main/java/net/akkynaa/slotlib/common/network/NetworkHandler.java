/*
 * This file is a modified version of code originally from Curios API by TheIllusiveC4.
 * Original source: https://github.com/TheIllusiveC4/Curios
 * Licensed under LGPL-3.0-or-later. Modifications are licensed under GPL-3.0-or-later.
 */
package net.akkynaa.slotlib.common.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.akkynaa.slotlib.SlotLib;
import net.akkynaa.slotlib.common.network.client.CPacketOpenSlotLib;
import net.akkynaa.slotlib.common.network.client.CPacketOpenVanilla;
import net.akkynaa.slotlib.common.network.server.SPacketGrabbedItem;
import net.akkynaa.slotlib.common.network.server.SPacketSyncSlots;

public class NetworkHandler {

    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SlotLib.MODID, "main"),
            () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, CPacketOpenSlotLib.class,
                CPacketOpenSlotLib::encode, CPacketOpenSlotLib::new, CPacketOpenSlotLib::handle);
        CHANNEL.registerMessage(id++, CPacketOpenVanilla.class,
                CPacketOpenVanilla::encode, CPacketOpenVanilla::new, CPacketOpenVanilla::handle);
        CHANNEL.registerMessage(id++, SPacketSyncSlots.class,
                SPacketSyncSlots::encode, SPacketSyncSlots::new, SPacketSyncSlots::handle);
        CHANNEL.registerMessage(id++, SPacketGrabbedItem.class,
                SPacketGrabbedItem::encode, SPacketGrabbedItem::new, SPacketGrabbedItem::handle);
    }
}
