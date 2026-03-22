/*
 * Copyright (c) AkkynaA. Licensed under GPL-3.0-or-later.
 */
package net.akkynaa.slotlib.client.compat;

import com.yyz.yyzsbackpack.Backpack;
import com.yyz.yyzsbackpack.base.BackpackMenu;
import com.yyz.yyzsbackpack.util.BackpackHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class BackpackClientCompat {

    public static void renderEquipSlotBackground(AbstractContainerMenu menu,
                                                  GuiGraphics guiGraphics,
                                                  int leftPos, int topPos) {
        if (BackpackHelper.isTrinketModLoaded() && !Backpack.getConfig().use_dedicated_slot) return;
        BackpackMenu backpackMenu = (BackpackMenu) menu;
        int x = leftPos + 8 + 69 - 1 + backpackMenu.getBackpackEquipSlotX();
        int y = topPos + 8 - 1 + 18 * 2 + backpackMenu.getBackpackEquipSlotY();
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED,
                ResourceLocation.fromNamespaceAndPath(Backpack.MOD_ID, "textures/gui/slot.png"),
                x, y, 0, 0, 18, 18, 18, 18);
    }
}
