/*
 * This file is a modified version of code originally from Curios API by TheIllusiveC4.
 * Original source: https://github.com/TheIllusiveC4/Curios
 * Licensed under LGPL-3.0-or-later. Modifications are licensed under GPL-3.0-or-later.
 */
package net.akkynaa.slotlib.common.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.akkynaa.slotlib.common.SlotLibRegistry;
import net.akkynaa.slotlib.common.capability.SlotLibInventory;
import net.akkynaa.slotlib.common.network.server.SPacketSyncSlots;

public class SlotLibEventHandler {

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
            if (targetPlayer.hasData(SlotLibRegistry.INVENTORY)) {
                SlotLibInventory inv = targetPlayer.getData(SlotLibRegistry.INVENTORY);
                List<ItemStack> stacks = new ArrayList<>();
                for (int i = 0; i < inv.getSlots(); i++) {
                    stacks.add(inv.getStackInSlot(i));
                }
                PacketDistributor.sendToPlayer(serverPlayer,
                        new SPacketSyncSlots(target.getId(), stacks));
            }
        }
    }

    @SubscribeEvent
    public void playerClone(PlayerEvent.Clone evt) {
        // Data is automatically copied via copyOnDeath on the attachment type
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerDrops(LivingDropsEvent evt) {
        LivingEntity livingEntity = evt.getEntity();

        if (livingEntity instanceof Player player && !player.isSpectator()) {
            boolean keepInventory = player.level().getServer().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);

            if (!keepInventory && player.hasData(SlotLibRegistry.INVENTORY)) {
                SlotLibInventory inv = player.getData(SlotLibRegistry.INVENTORY);
                Collection<ItemEntity> drops = evt.getDrops();

                for (int i = 0; i < inv.getSlots(); i++) {
                    ItemStack stack = inv.getStackInSlot(i);
                    if (!stack.isEmpty()
                            && !EnchantmentHelper.has(stack, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)) {
                        drops.add(getDroppedItem(stack, livingEntity));
                        inv.setStackInSlot(i, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void playerXPPickUp(PlayerXpEvent.PickupXp evt) {
        Player player = evt.getEntity();
        if (!player.level().isClientSide && player.hasData(SlotLibRegistry.INVENTORY)) {
            SlotLibInventory inv = player.getData(SlotLibRegistry.INVENTORY);
            Holder<Enchantment> mendingHolder =
                    player.level().registryAccess()
                            .lookupOrThrow(Registries.ENCHANTMENT)
                            .getOrThrow(Enchantments.MENDING);

            for (int i = 0; i < inv.getSlots(); i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if (!stack.isEmpty() && stack.getEnchantmentLevel(mendingHolder) > 0 && stack.isDamaged()) {
                    evt.setCanceled(true);
                    ExperienceOrb orb = evt.getOrb();
                    player.takeXpDelay = 2;
                    player.take(orb, 1);
                    int xpValue = orb.getValue();
                    int toRepair = Math.min(xpValue * 2, stack.getDamageValue());
                    stack.setDamageValue(stack.getDamageValue() - toRepair);
                    int remaining = xpValue - toRepair / 2;

                    if (remaining > 0) {
                        player.giveExperiencePoints(remaining);
                    }
                    orb.discard();
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public void tick(EntityTickEvent.Post evt) {
        Entity entity = evt.getEntity();

        if (entity instanceof Player player && !player.level().isClientSide
                && player.hasData(SlotLibRegistry.INVENTORY)) {
            SlotLibInventory inv = player.getData(SlotLibRegistry.INVENTORY);
            boolean dirty = false;

            for (int i = 0; i < inv.getSlots(); i++) {
                ItemStack current = inv.getStackInSlot(i);
                ItemStack previous = inv.getPreviousStackInSlot(i);

                if (!current.isEmpty()) {
                    current.inventoryTick(player.level(), player, EquipmentSlot.MAINHAND);
                }

                if (!ItemStack.matches(current, previous)) {
                    inv.setPreviousStackInSlot(i, current.copy());
                    dirty = true;
                }
            }

            if (dirty) {
                syncInventoryToTracking(player);
            }
        }
    }

    private static void syncInventoryToClient(ServerPlayer serverPlayer) {
        if (serverPlayer.hasData(SlotLibRegistry.INVENTORY)) {
            SlotLibInventory inv = serverPlayer.getData(SlotLibRegistry.INVENTORY);
            List<ItemStack> stacks = new ArrayList<>();
            for (int i = 0; i < inv.getSlots(); i++) {
                stacks.add(inv.getStackInSlot(i));
            }
            PacketDistributor.sendToPlayer(serverPlayer,
                    new SPacketSyncSlots(serverPlayer.getId(), stacks));
        }
    }

    private static void syncInventoryToTracking(Player player) {
        if (player instanceof ServerPlayer && player.hasData(SlotLibRegistry.INVENTORY)) {
            SlotLibInventory inv = player.getData(SlotLibRegistry.INVENTORY);
            List<ItemStack> stacks = new ArrayList<>();
            for (int i = 0; i < inv.getSlots(); i++) {
                stacks.add(inv.getStackInSlot(i));
            }
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                    player, new SPacketSyncSlots(player.getId(), stacks));
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
