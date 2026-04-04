/*
 * This file is a modified version of code originally from Curios API by TheIllusiveC4.
 * Original source: https://github.com/TheIllusiveC4/Curios
 * Licensed under LGPL-3.0-or-later. Modifications are licensed under GPL-3.0-or-later.
 */
package net.akkynaa.slotlib.common.capability;

import javax.annotation.Nonnull;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.items.ItemStackHandler;

import net.akkynaa.slotlib.SlotLibConfig;

public class SlotLibInventory implements ValueIOSerializable {

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
    public void serialize(@Nonnull ValueOutput output) {
        this.stackHandler.serialize(output);
    }

    @Override
    public void deserialize(@Nonnull ValueInput input) {
        int targetSize = getSlotCount();
        this.stackHandler = new ItemStackHandler(targetSize);
        this.previousStacks = new ItemStackHandler(targetSize);
        this.stackHandler.deserialize(input);
        this.syncCountdown = 20;
    }
}
