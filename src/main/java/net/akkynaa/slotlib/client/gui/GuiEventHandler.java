/*
 * This file is a modified version of code originally from Curios API by TheIllusiveC4.
 * Original source: https://github.com/TheIllusiveC4/Curios
 * Licensed under LGPL-3.0-or-later. Modifications are licensed under GPL-3.0-or-later.
 */
package net.akkynaa.slotlib.client.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.akkynaa.slotlib.client.compat.CuriosCompat;

public class GuiEventHandler {

    @SubscribeEvent
    public void onInventoryGuiInit(ScreenEvent.Init.Post evt) {
        Screen screen = evt.getScreen();

        if (screen instanceof InventoryScreen || screen instanceof CreativeModeInventoryScreen) {
            AbstractContainerScreen<?> gui = (AbstractContainerScreen<?>) screen;
            boolean isCreative = screen instanceof CreativeModeInventoryScreen;
            int size = isCreative ? 8 : 10;
            int[] pos = SlotLibButton.getButtonPosition(gui);
            evt.addListener(
                    new SlotLibButton(gui, pos[0], pos[1], size, size, isCreative));
        } else if (ModList.get().isLoaded("curios")
                && CuriosCompat.isCuriosScreen(screen)
                && screen instanceof AbstractContainerScreen<?> gui) {
            CuriosCompat.addSlotLibButtonToCuriosScreen(evt, gui);
        }
    }
}
