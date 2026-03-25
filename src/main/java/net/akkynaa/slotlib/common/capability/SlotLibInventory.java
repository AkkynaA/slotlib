/*
 * This file is a modified version of code originally from Curios API by TheIllusiveC4.
 * Original source: https://github.com/TheIllusiveC4/Curios
 * Licensed under LGPL-3.0-or-later. Modifications are licensed under GPL-3.0-or-later.
 */
package net.akkynaa.slotlib.common.capability;

import javax.annotation.Nonnull;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.akkynaa.slotlib.SlotLibConfig;

public class SlotLibInventory implements INBTSerializable<CompoundTag> {

    private ItemStackHandler stackHandler;
    private ItemStackHandler previousStacks;
    private int syncCountdown = 0;

    public SlotLibInventory() {
        int slots = getSlotCount();
        this.stackHandler = new ItemStackHandler(slots);
        this.previousStacks = new ItemStackHandler(slots);
    }

    public static int getSlotCount() {
        try {
            return SlotLibConfig.SLOT_COUNT.get();
        } catch (Exception e) {
            return 4;
        }
    }

    public ItemStackHandler getStacks() {
        return this.stackHandler;
    }

    public int getSlots() {
        return this.stackHandler.getSlots();
    }

    public ItemStack getStackInSlot(int slot) {
        return this.stackHandler.getStackInSlot(slot);
    }

    public void setStackInSlot(int slot, ItemStack stack) {
        this.stackHandler.setStackInSlot(slot, stack);
    }

    public ItemStack getPreviousStackInSlot(int slot) {
        if (slot < this.previousStacks.getSlots()) {
            return this.previousStacks.getStackInSlot(slot);
        }
        return ItemStack.EMPTY;
    }

    public void setPreviousStackInSlot(int slot, ItemStack stack) {
        if (slot < this.previousStacks.getSlots()) {
            this.previousStacks.setStackInSlot(slot, stack);
        }
    }

    public int getSyncCountdown() {
        return this.syncCountdown;
    }

    public void decrementSyncCountdown() {
        if (this.syncCountdown > 0) {
            this.syncCountdown--;
        }
    }

    public void resize(int newSize) {
        ItemStackHandler newHandler = new ItemStackHandler(newSize);
        ItemStackHandler newPrevious = new ItemStackHandler(newSize);
        int copyCount = Math.min(this.stackHandler.getSlots(), newSize);
        for (int i = 0; i < copyCount; i++) {
            newHandler.setStackInSlot(i, this.stackHandler.getStackInSlot(i));
            newPrevious.setStackInSlot(i, this.previousStacks.getStackInSlot(i));
        }
        this.stackHandler = newHandler;
        this.previousStacks = newPrevious;
    }

    @Override
    public CompoundTag serializeNBT(@Nonnull HolderLookup.Provider provider) {
        CompoundTag compound = new CompoundTag();
        ListTag tagList = new ListTag();
        for (int i = 0; i < this.stackHandler.getSlots(); i++) {
            ItemStack stack = this.stackHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                CompoundTag tag = new CompoundTag();
                tag.putInt("Slot", i);
                tagList.add(stack.save(provider, tag));
            }
        }
        compound.put("Items", tagList);
        compound.putInt("Size", this.stackHandler.getSlots());
        return compound;
    }

    @Override
    public void deserializeNBT(@Nonnull HolderLookup.Provider provider, @Nonnull CompoundTag nbt) {
        int size = nbt.contains("Size") ? nbt.getInt("Size") : getSlotCount();
        int targetSize = getSlotCount();
        this.stackHandler = new ItemStackHandler(targetSize);
        this.previousStacks = new ItemStackHandler(targetSize);

        ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++) {
            CompoundTag tag = tagList.getCompound(i);
            int slot = tag.getInt("Slot");
            if (slot >= 0 && slot < targetSize) {
                ItemStack.parse(provider, tag).ifPresent(stack ->
                        this.stackHandler.setStackInSlot(slot, stack));
            }
        }
        this.syncCountdown = 20;
    }
}
