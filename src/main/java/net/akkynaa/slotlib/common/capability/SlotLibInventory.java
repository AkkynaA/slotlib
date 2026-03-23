/*
 * This file is a modified version of code originally from Curios API by TheIllusiveC4.
 * Original source: https://github.com/TheIllusiveC4/Curios
 * Licensed under LGPL-3.0-or-later. Modifications are licensed under GPL-3.0-or-later.
 */
package net.akkynaa.slotlib.common.capability;

import javax.annotation.Nonnull;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import net.akkynaa.slotlib.SlotLibConfig;

public class SlotLibInventory implements ValueIOSerializable {

    private ItemStacksResourceHandler stackHandler;
    private NonNullList<ItemStack> previousStacks;

    public SlotLibInventory() {
        int slots = getSlotCount();
        this.stackHandler = new ItemStacksResourceHandler(slots);
        this.previousStacks = NonNullList.withSize(slots, ItemStack.EMPTY);
    }

    public static int getSlotCount() {
        try {
            return SlotLibConfig.SLOT_COUNT.get();
        } catch (Exception e) {
            return 4;
        }
    }

    public ItemStacksResourceHandler getStacks() {
        return this.stackHandler;
    }

    public int getSlots() {
        return this.stackHandler.size();
    }

    public ItemStack getStackInSlot(int slot) {
        ItemResource resource = this.stackHandler.getResource(slot);
        return resource.toStack(this.stackHandler.getAmountAsInt(slot));
    }

    public void setStackInSlot(int slot, ItemStack stack) {
        this.stackHandler.set(slot, ItemResource.of(stack), stack.getCount());
    }

    public ItemStack getPreviousStackInSlot(int slot) {
        if (slot < this.previousStacks.size()) {
            return this.previousStacks.get(slot);
        }
        return ItemStack.EMPTY;
    }

    public void setPreviousStackInSlot(int slot, ItemStack stack) {
        if (slot < this.previousStacks.size()) {
            this.previousStacks.set(slot, stack);
        }
    }

    public void resize(int newSize) {
        ItemStacksResourceHandler newHandler = new ItemStacksResourceHandler(newSize);
        NonNullList<ItemStack> newPrevious = NonNullList.withSize(newSize, ItemStack.EMPTY);
        int copyCount = Math.min(this.stackHandler.size(), newSize);
        for (int i = 0; i < copyCount; i++) {
            newHandler.set(i, this.stackHandler.getResource(i), this.stackHandler.getAmountAsInt(i));
            newPrevious.set(i, this.previousStacks.get(i));
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
        this.stackHandler = new ItemStacksResourceHandler(targetSize);
        this.previousStacks = NonNullList.withSize(targetSize, ItemStack.EMPTY);
        this.stackHandler.deserialize(input);
    }
}
