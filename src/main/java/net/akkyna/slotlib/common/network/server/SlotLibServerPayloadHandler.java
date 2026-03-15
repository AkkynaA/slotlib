package net.akkyna.slotlib.common.network.server;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.akkyna.slotlib.common.inventory.container.SlotLibContainerProvider;
import net.akkyna.slotlib.common.network.client.CPacketOpenSlotLib;
import net.akkyna.slotlib.common.network.client.CPacketOpenVanilla;

public class SlotLibServerPayloadHandler {

    private static final SlotLibServerPayloadHandler INSTANCE = new SlotLibServerPayloadHandler();

    public static SlotLibServerPayloadHandler getInstance() {
        return INSTANCE;
    }

    public void handleOpenSlotLib(final CPacketOpenSlotLib data, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();

            if (player instanceof ServerPlayer serverPlayer) {
                ItemStack stack =
                        player.isCreative() ? data.carried() : player.containerMenu.getCarried();
                player.containerMenu.setCarried(ItemStack.EMPTY);
                player.openMenu(new SlotLibContainerProvider());

                if (!stack.isEmpty()) {
                    player.containerMenu.setCarried(stack);
                    PacketDistributor.sendToPlayer(serverPlayer, new SPacketGrabbedItem(stack));
                }
            }
        });
    }

    public void handleOpenVanilla(final CPacketOpenVanilla data, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();

            if (player instanceof ServerPlayer serverPlayer) {
                ItemStack stack =
                        player.isCreative() ? data.carried() : player.containerMenu.getCarried();
                player.containerMenu.setCarried(ItemStack.EMPTY);
                serverPlayer.doCloseContainer();

                if (!stack.isEmpty()) {
                    if (!player.isCreative()) {
                        player.containerMenu.setCarried(stack);
                    }
                    PacketDistributor.sendToPlayer(serverPlayer, new SPacketGrabbedItem(stack));
                }
            }
        });
    }
}
