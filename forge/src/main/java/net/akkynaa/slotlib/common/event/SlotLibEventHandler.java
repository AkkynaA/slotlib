/*
 * This file is a modified version of code originally from Curios API by TheIllusiveC4.
 * Original source: https://github.com/TheIllusiveC4/Curios
 * Licensed under LGPL-3.0-or-later. Modifications are licensed under GPL-3.0-or-later.
 */
package net.akkynaa.slotlib.common.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;
import net.akkynaa.slotlib.SlotLibConstants;
import net.akkynaa.slotlib.common.capability.SlotLibCapabilityProvider;
import net.akkynaa.slotlib.common.capability.SlotLibInventory;
import net.akkynaa.slotlib.common.network.NetworkHandler;
import net.akkynaa.slotlib.common.network.server.SPacketSyncSlots;

public class SlotLibEventHandler {

    @SubscribeEvent
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(SlotLibInventory.class);
    }

    @SubscribeEvent
    public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(new ResourceLocation(SlotLibConstants.MODID, "inventory"),
                    new SlotLibCapabilityProvider());
        }
    }

    @SubscribeEvent
    public void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent evt) {
        Player player = evt.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            syncInventoryToClient(serverPlayer);
        }
    }

    @SubscribeEvent
    public void entityJoinWorld(EntityJoinLevelEvent evt) {
        Entity entity = evt.getEntity();
        if (entity instanceof ServerPlayer serverPlayer) {
            syncInventoryToClient(serverPlayer);
        }
    }

    @SubscribeEvent
    public void playerStartTracking(PlayerEvent.StartTracking evt) {
        Entity target = evt.getTarget();
        Player player = evt.getEntity();

        if (player instanceof ServerPlayer serverPlayer && target instanceof Player targetPlayer) {
            targetPlayer.getCapability(SlotLibCapabilityProvider.INVENTORY_CAP).ifPresent(inv -> {
                List<ItemStack> stacks = new ArrayList<>();
                for (int i = 0; i < inv.getSlots(); i++) {
                    stacks.add(inv.getStackInSlot(i));
                }
                NetworkHandler.CHANNEL.send(new SPacketSyncSlots(target.getId(), stacks),
                        PacketDistributor.PLAYER.with(serverPlayer));
            });
        }
    }

    @SubscribeEvent
    public void playerClone(PlayerEvent.Clone evt) {
        if (evt.isWasDeath()) {
            evt.getOriginal().reviveCaps();
            evt.getOriginal().getCapability(SlotLibCapabilityProvider.INVENTORY_CAP).ifPresent(oldInv -> {
                evt.getEntity().getCapability(SlotLibCapabilityProvider.INVENTORY_CAP).ifPresent(newInv -> {
                    newInv.deserializeNBT(evt.getEntity().registryAccess(),
                            oldInv.serializeNBT(evt.getEntity().registryAccess()));
                });
            });
            evt.getOriginal().invalidateCaps();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerDrops(LivingDropsEvent evt) {
        LivingEntity livingEntity = evt.getEntity();

        if (livingEntity instanceof Player player && !player.isSpectator()) {
            boolean keepInventory = player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);

            if (!keepInventory) {
                player.getCapability(SlotLibCapabilityProvider.INVENTORY_CAP).ifPresent(inv -> {
                    Collection<ItemEntity> drops = evt.getDrops();

                    for (int i = 0; i < inv.getSlots(); i++) {
                        ItemStack stack = inv.getStackInSlot(i);
                        if (!stack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(stack)) {
                            drops.add(getDroppedItem(stack, livingEntity));
                            inv.setStackInSlot(i, ItemStack.EMPTY);
                        }
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public void playerXPPickUp(PlayerXpEvent.PickupXp evt) {
        Player player = evt.getEntity();
        if (!player.level().isClientSide) {
            player.getCapability(SlotLibCapabilityProvider.INVENTORY_CAP).ifPresent(inv -> {
                for (int i = 0; i < inv.getSlots(); i++) {
                    ItemStack stack = inv.getStackInSlot(i);
                    if (!stack.isEmpty()
                            && stack.getEnchantmentLevel(Enchantments.MENDING) > 0
                            && stack.isDamaged()) {
                        evt.setCanceled(true);
                        ExperienceOrb orb = evt.getOrb();
                        player.takeXpDelay = 2;
                        player.take(orb, 1);
                        int toRepair = Math.min(orb.value * 2, stack.getDamageValue());
                        orb.value -= toRepair / 2;
                        stack.setDamageValue(stack.getDamageValue() - toRepair);

                        if (orb.value > 0) {
                            player.giveExperiencePoints(orb.value);
                        }
                        orb.remove(Entity.RemovalReason.KILLED);
                        return;
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public void tick(TickEvent.PlayerTickEvent evt) {
        if (evt.phase != TickEvent.Phase.END) return;

        Player player = evt.player;
        if (!player.level().isClientSide) {
            player.getCapability(SlotLibCapabilityProvider.INVENTORY_CAP).ifPresent(inv -> {
                boolean dirty = false;

                for (int i = 0; i < inv.getSlots(); i++) {
                    ItemStack current = inv.getStackInSlot(i);
                    ItemStack previous = inv.getPreviousStackInSlot(i);

                    if (!current.isEmpty()) {
                        current.inventoryTick(player.level(), player, -1, false);
                    }

                    if (!ItemStack.matches(current, previous)) {
                        inv.setPreviousStackInSlot(i, current.copy());
                        dirty = true;
                    }
                }

                if (dirty) {
                    syncInventoryToTracking(player);
                }
            });
        }
    }

    private static void syncInventoryToClient(ServerPlayer serverPlayer) {
        serverPlayer.getCapability(SlotLibCapabilityProvider.INVENTORY_CAP).ifPresent(inv -> {
            List<ItemStack> stacks = new ArrayList<>();
            for (int i = 0; i < inv.getSlots(); i++) {
                stacks.add(inv.getStackInSlot(i));
            }
            NetworkHandler.CHANNEL.send(new SPacketSyncSlots(serverPlayer.getId(), stacks),
                    PacketDistributor.PLAYER.with(serverPlayer));
        });
    }

    private static void syncInventoryToTracking(Player player) {
        if (player instanceof ServerPlayer) {
            player.getCapability(SlotLibCapabilityProvider.INVENTORY_CAP).ifPresent(inv -> {
                List<ItemStack> stacks = new ArrayList<>();
                for (int i = 0; i < inv.getSlots(); i++) {
                    stacks.add(inv.getStackInSlot(i));
                }
                NetworkHandler.CHANNEL.send(new SPacketSyncSlots(player.getId(), stacks),
                        PacketDistributor.TRACKING_ENTITY_AND_SELF.with(player));
            });
        }
    }

    private static ItemEntity getDroppedItem(ItemStack droppedItem, LivingEntity livingEntity) {
        double d0 = livingEntity.getY() - 0.30000001192092896D + livingEntity.getEyeHeight();
        ItemEntity entityItem = new ItemEntity(livingEntity.level(), livingEntity.getX(), d0,
                livingEntity.getZ(), droppedItem);
        entityItem.setPickUpDelay(40);
        float f = livingEntity.level().random.nextFloat() * 0.5F;
        float f1 = livingEntity.level().random.nextFloat() * ((float) Math.PI * 2F);
        entityItem.setDeltaMovement((-Mth.sin(f1) * f), 0.20000000298023224D, (Mth.cos(f1) * f));
        return entityItem;
    }
}
