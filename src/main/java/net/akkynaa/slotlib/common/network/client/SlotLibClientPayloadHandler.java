package net.akkynaa.slotlib.common.network.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.akkynaa.slotlib.common.SlotLibRegistry;
import net.akkynaa.slotlib.common.capability.SlotLibInventory;
import net.akkynaa.slotlib.common.network.server.SPacketSyncSlots;
import net.akkynaa.slotlib.common.network.server.SPacketGrabbedItem;

public class SlotLibClientPayloadHandler {

    private static final SlotLibClientPayloadHandler INSTANCE = new SlotLibClientPayloadHandler();

    public static SlotLibClientPayloadHandler getInstance() {
        return INSTANCE;
    }

    public void handleSyncSlots(final SPacketSyncSlots data, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null) {
                var entity = mc.level.getEntity(data.entityId());
                if (entity != null && entity.hasData(SlotLibRegistry.INVENTORY)) {
                    SlotLibInventory inv = entity.getData(SlotLibRegistry.INVENTORY);
                    for (int i = 0; i < data.stacks().size() && i < inv.getSlots(); i++) {
                        inv.setStackInSlot(i, data.stacks().get(i));
                    }
                }
            }
        });
    }

    public void handleGrabbedItem(final SPacketGrabbedItem data, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                player.containerMenu.setCarried(data.stack());
            }
        });
    }
}
