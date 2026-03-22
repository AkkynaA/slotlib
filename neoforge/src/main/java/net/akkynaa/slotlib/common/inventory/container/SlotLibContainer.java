/*
 * This file is a modified version of code originally from Curios API by TheIllusiveC4.
 * Original source: https://github.com/TheIllusiveC4/Curios
 * Licensed under LGPL-3.0-or-later. Modifications are licensed under GPL-3.0-or-later.
 */
package net.akkynaa.slotlib.common.inventory.container;

import com.mojang.datafixers.util.Pair;
import javax.annotation.Nonnull;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.SlotItemHandler;
import java.util.Objects;
import java.util.Optional;

import net.akkynaa.slotlib.common.SlotLibRegistry;
import net.akkynaa.slotlib.common.capability.SlotLibInventory;

public class SlotLibContainer extends RecipeBookMenu<CraftingContainer> {

    private static final ResourceLocation[] ARMOR_SLOT_TEXTURES = new ResourceLocation[]{
            InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS, InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS,
            InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE, InventoryMenu.EMPTY_ARMOR_SLOT_HELMET};
    private static final EquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EquipmentSlot[]{
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public final Player player;
    private final boolean isLocalWorld;
    private final CraftingContainer craftMatrix = new TransientCraftingContainer(this, 2, 2);
    private final ResultContainer craftResult = new ResultContainer();
    private final SlotLibInventory slotLibInventory;
    private int slotLibStartIndex;

    public SlotLibContainer(int windowId, Inventory playerInventory, FriendlyByteBuf packetBuffer) {
        this(windowId, playerInventory);
    }

    public SlotLibContainer(int windowId, Inventory playerInventory) {
        super(SlotLibRegistry.SLOTLIB_MENU.get(), windowId);
        this.player = playerInventory.player;
        this.isLocalWorld = this.player.level().isClientSide;
        this.slotLibInventory = this.player.getData(SlotLibRegistry.INVENTORY);
        this.setupSlots();
    }

    private void setupSlots() {
        int backpackOffset = 0;

        // Crafting result slot (index 0)
        this.addSlot(new ResultSlot(player, this.craftMatrix, this.craftResult, 0, 154, 28));

        // 2x2 crafting grid (indices 1-4)
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 2; ++j) {
                this.addSlot(new Slot(this.craftMatrix, j + i * 2, 98 + j * 18, 18 + i * 18));
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
                            || !EnchantmentHelper.hasBindingCurse(itemstack))
                            && super.mayPickup(playerIn);
                }

                @OnlyIn(Dist.CLIENT)
                @Override
                public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                    return Pair.of(InventoryMenu.BLOCK_ATLAS,
                            ARMOR_SLOT_TEXTURES[equipmentSlot.getIndex()]);
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
            @OnlyIn(Dist.CLIENT)
            @Override
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD);
            }
        });

        // SlotLib slots (indices 46+)
        this.slotLibStartIndex = this.slots.size();
        int slotCount = this.slotLibInventory.getSlots();
        for (int i = 0; i < slotCount; i++) {
            this.addSlot(new SlotItemHandler(this.slotLibInventory.getStacks(), i,
                    8 + i * 18, 176));
        }
    }

    public int getSlotLibStartIndex() {
        return this.slotLibStartIndex;
    }

    public int getSlotLibSlotCount() {
        return this.slotLibInventory.getSlots();
    }

    @Override
    public void slotsChanged(@Nonnull Container inventoryIn) {
        if (!this.player.level().isClientSide) {
            ServerPlayer serverPlayer = (ServerPlayer) this.player;
            ItemStack itemstack = ItemStack.EMPTY;
            Optional<RecipeHolder<CraftingRecipe>> optional =
                    Objects.requireNonNull(this.player.level().getServer()).getRecipeManager()
                            .getRecipeFor(RecipeType.CRAFTING, this.craftMatrix,
                                    this.player.level());

            if (optional.isPresent()) {
                RecipeHolder<CraftingRecipe> recipeholder = optional.get();
                CraftingRecipe craftingrecipe = recipeholder.value();

                if (this.craftResult.setRecipeUsed(this.player.level(), serverPlayer, recipeholder)) {
                    ItemStack result = craftingrecipe.assemble(this.craftMatrix,
                            this.player.level().registryAccess());

                    if (result.isItemEnabled(this.player.level().enabledFeatures())) {
                        itemstack = result;
                    }
                }
            }
            this.craftResult.setItem(0, itemstack);
            this.setRemoteSlot(0, itemstack);
            serverPlayer.connection.send(
                    new ClientboundContainerSetSlotPacket(this.containerId, this.incrementStateId(), 0,
                            itemstack));
        }
    }

    @Override
    public void removed(@Nonnull Player playerIn) {
        super.removed(playerIn);
        this.craftResult.clearContent();
        if (!playerIn.level().isClientSide) {
            this.clearContainer(playerIn, this.craftMatrix);
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
            EquipmentSlot entityEquipmentSlot = Mob.getEquipmentSlotForItem(itemstack);

            if (index == 0) {
                if (!this.moveItemStackTo(itemstack1, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index < 5) {
                if (!this.moveItemStackTo(itemstack1, 9, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < 9) {
                if (!this.moveItemStackTo(itemstack1, 9, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= this.slotLibStartIndex) {
                if (!this.moveItemStackTo(itemstack1, 9, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (entityEquipmentSlot.getType() == EquipmentSlot.Type.ARMOR
                    && !this.slots.get(8 - entityEquipmentSlot.getIndex()).hasItem()) {
                int i = 8 - entityEquipmentSlot.getIndex();
                if (!this.moveItemStackTo(itemstack1, i, i + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < 46) {
                if (!this.moveItemStackTo(itemstack1, this.slotLibStartIndex,
                        this.slotLibStartIndex + getSlotLibSlotCount(), false)) {
                    if (entityEquipmentSlot == EquipmentSlot.OFFHAND && !this.slots.get(45).hasItem()) {
                        if (!this.moveItemStackTo(itemstack1, 45, 46, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index < 36) {
                        if (!this.moveItemStackTo(itemstack1, 36, 45, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index < 45) {
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
    public boolean shouldMoveToInventory(int index) {
        return index != this.getResultSlotIndex();
    }

    @Override
    public void fillCraftSlotsStackedContents(@Nonnull StackedContents itemHelperIn) {
        this.craftMatrix.fillStackedContents(itemHelperIn);
    }

    @Override
    public void clearCraftingContent() {
        this.craftMatrix.clearContent();
        this.craftResult.clearContent();
    }

    @Override
    public boolean recipeMatches(RecipeHolder<? extends Recipe<CraftingContainer>> p_297792_) {
        return false;
    }

    @Override
    public int getResultSlotIndex() {
        return 0;
    }

    @Override
    public int getGridWidth() {
        return this.craftMatrix.getWidth();
    }

    @Override
    public int getGridHeight() {
        return this.craftMatrix.getHeight();
    }

    @Override
    public int getSize() {
        return 5;
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
