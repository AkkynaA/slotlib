/*
 * This file is a modified version of code originally from Curios API by TheIllusiveC4.
 * Original source: https://github.com/TheIllusiveC4/Curios
 * Licensed under LGPL-3.0-or-later. Modifications are licensed under GPL-3.0-or-later.
 */
package net.akkynaa.slotlib.common.inventory.container;

import javax.annotation.Nonnull;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractCraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.neoforge.items.SlotItemHandler;

import net.akkynaa.slotlib.common.SlotLibRegistry;
import net.akkynaa.slotlib.common.capability.SlotLibInventory;
import net.akkynaa.slotlib.compat.BackpackCompat;
import net.neoforged.fml.ModList;

public class SlotLibContainer extends AbstractCraftingMenu {

    private static final ResourceLocation[] ARMOR_SLOT_TEXTURES = new ResourceLocation[]{
            InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS, InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS,
            InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE, InventoryMenu.EMPTY_ARMOR_SLOT_HELMET};
    private static final EquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EquipmentSlot[]{
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public final Player player;
    private final SlotLibInventory slotLibInventory;
    private int slotLibStartIndex;

    public SlotLibContainer(int windowId, Inventory playerInventory, FriendlyByteBuf packetBuffer) {
        this(windowId, playerInventory);
    }

    public SlotLibContainer(int windowId, Inventory playerInventory) {
        super(SlotLibRegistry.SLOTLIB_MENU.get(), windowId, 2, 2);
        this.player = playerInventory.player;
        this.slotLibInventory = this.player.getData(SlotLibRegistry.INVENTORY);
        this.setupSlots();
    }

    private void setupSlots() {
        int backpackOffset = 0;
        if (ModList.get().isLoaded("yyzsbackpack")) {
            backpackOffset = BackpackCompat.getSlotIndexOffset();
        }

        // Crafting result slot (index 0)
        this.addSlot(new ResultSlot(player, this.craftSlots, this.resultSlots, 0, 154, 28));

        // 2x2 crafting grid (indices 1-4)
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 2; ++j) {
                this.addSlot(new Slot(this.craftSlots, j + i * 2, 98 + j * 18, 18 + i * 18));
            }
        }

        // 4 armor slots (indices 5-8)
        for (int k = 0; k < 4; ++k) {
            final EquipmentSlot equipmentSlot = VALID_EQUIPMENT_SLOTS[k];
            this.addSlot(new Slot(player.getInventory(), 36 + backpackOffset + (3 - k), 8, 8 + k * 18) {
                @Override
                public void set(@Nonnull ItemStack stack) {
                    ItemStack old = this.getItem();
                    super.set(stack);
                    SlotLibContainer.this.player.onEquipItem(equipmentSlot, old, stack);
                }

                @Override
                public int getMaxStackSize() {
                    return 1;
                }

                @Override
                public boolean mayPlace(@Nonnull ItemStack stack) {
                    return stack.canEquip(equipmentSlot, SlotLibContainer.this.player);
                }

                @Override
                public boolean mayPickup(@Nonnull Player playerIn) {
                    ItemStack itemstack = this.getItem();
                    return (itemstack.isEmpty() || playerIn.isCreative()
                            || !EnchantmentHelper.has(itemstack, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE))
                            && super.mayPickup(playerIn);
                }

                @Override
                public ResourceLocation getNoItemIcon() {
                    return ARMOR_SLOT_TEXTURES[equipmentSlot.getIndex()];
                }
            });
        }

        // Main inventory 27 slots (indices 9-35)
        for (int l = 0; l < 3; ++l) {
            for (int j1 = 0; j1 < 9; ++j1) {
                this.addSlot(new Slot(player.getInventory(), j1 + (l + 1) * 9, 8 + j1 * 18, 84 + l * 18));
            }
        }

        // Hotbar 9 slots (indices 36-44)
        for (int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(new Slot(player.getInventory(), i1, 8 + i1 * 18, 142));
        }

        // Shield slot (index 45)
        this.addSlot(new Slot(player.getInventory(), 40 + backpackOffset, 77, 62) {
            @Override
            public ResourceLocation getNoItemIcon() {
                return InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD;
            }
        });

        // SlotLib slots (indices 46+)
        this.slotLibStartIndex = this.slots.size();
        int slotCount = this.slotLibInventory.getSlots();
        for (int i = 0; i < slotCount; i++) {
            this.addSlot(new SlotItemHandler(this.slotLibInventory.getStacks(), i,
                    8 + i * 18, 176));
        }

        // Backpack slots (after SlotLib slots, when yyzsbackpack is loaded)
        if (ModList.get().isLoaded("yyzsbackpack")) {
            BackpackCompat.addBackpackSlots(this, player.getInventory());
        }
    }

    public int getSlotLibStartIndex() {
        return this.slotLibStartIndex;
    }

    public int getSlotLibSlotCount() {
        return this.slotLibInventory.getSlots();
    }

    @Override
    public void removed(@Nonnull Player playerIn) {
        super.removed(playerIn);
        this.resultSlots.clearContent();
        if (!playerIn.level().isClientSide) {
            this.clearContainer(playerIn, this.craftSlots);
        }
    }

    @Override
    public void setItem(int pSlotId, int pStateId, @Nonnull ItemStack pStack) {
        if (this.slots.size() > pSlotId) {
            super.setItem(pSlotId, pStateId, pStack);
        }
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        return true;
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(@Nonnull Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            EquipmentSlot entityEquipmentSlot = playerIn.getEquipmentSlotForItem(itemstack);

            if (index == 0) {
                // Crafting result -> inventory
                if (!this.moveItemStackTo(itemstack1, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index < 5) {
                // Crafting grid -> inventory
                if (!this.moveItemStackTo(itemstack1, 9, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < 9) {
                // Armor -> inventory
                if (!this.moveItemStackTo(itemstack1, 9, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= this.slotLibStartIndex) {
                // SlotLib slots -> inventory
                if (!this.moveItemStackTo(itemstack1, 9, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (entityEquipmentSlot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR
                    && !this.slots.get(8 - entityEquipmentSlot.getIndex()).hasItem()) {
                // Inventory -> armor
                int i = 8 - entityEquipmentSlot.getIndex();
                if (!this.moveItemStackTo(itemstack1, i, i + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < 46) {
                // Inventory -> slotlib slots
                if (!this.moveItemStackTo(itemstack1, this.slotLibStartIndex,
                        this.slotLibStartIndex + getSlotLibSlotCount(), false)) {
                    // Inventory -> offhand
                    if (entityEquipmentSlot == EquipmentSlot.OFFHAND && !this.slots.get(45).hasItem()) {
                        if (!this.moveItemStackTo(itemstack1, 45, 46, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index < 36) {
                        // Main inventory -> hotbar
                        if (!this.moveItemStackTo(itemstack1, 36, 45, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index < 45) {
                        // Hotbar -> main inventory
                        if (!this.moveItemStackTo(itemstack1, 9, 36, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                }
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(playerIn, itemstack1);

            if (index == 0) {
                playerIn.drop(itemstack1, false);
            }
        }
        return itemstack;
    }

    @Nonnull
    @Override
    public RecipeBookType getRecipeBookType() {
        return RecipeBookType.CRAFTING;
    }

    @Override
    public Slot getResultSlot() {
        return this.slots.getFirst();
    }

    @Override
    public List<Slot> getInputGridSlots() {
        return this.slots.subList(1, 5);
    }

    @Override
    public Player owner() {
        return this.player;
    }

    @Nonnull
    @Override
    public Slot getSlot(int index) {
        if (index < 0) {
            return super.getSlot(0);
        } else if (index >= this.slots.size()) {
            return super.getSlot(this.slots.size() - 1);
        }
        return super.getSlot(index);
    }
}
