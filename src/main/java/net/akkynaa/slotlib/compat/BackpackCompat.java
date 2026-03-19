/*
 * Copyright (c) AkkynaA. Licensed under GPL-3.0-or-later.
 */
package net.akkynaa.slotlib.compat;

import com.yyz.yyzsbackpack.base.BackpackMenu;
import com.yyz.yyzsbackpack.util.BackpackHelper;
import com.yyz.yyzsbackpack.util.SlotManager;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class BackpackCompat {

    public static int getSlotIndexOffset() {
        return BackpackHelper.getSlotIndexOffset();
    }

    public static void addBackpackSlots(AbstractContainerMenu menu, Inventory inventory) {
        SlotManager.addBackpackInventorySlots(menu, inventory);
        SlotManager.addBackpackEquipSlot(menu, inventory);
    }

    public static void setBackpackVisible(AbstractContainerMenu menu, boolean visible) {
        ((BackpackMenu) menu).setBackpackVisible(visible);
    }

    public static void setBackpackGuiPos(AbstractContainerMenu menu, int x, int y) {
        ((BackpackMenu) menu).setBackpackGuiPos(x, y);
    }
}
