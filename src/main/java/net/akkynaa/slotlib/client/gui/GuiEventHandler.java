package net.akkynaa.slotlib.client.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

public class GuiEventHandler {

    @SubscribeEvent
    public void onInventoryGuiInit(ScreenEvent.Init.Post evt) {
        Screen screen = evt.getScreen();

        if (screen instanceof InventoryScreen || screen instanceof CreativeModeInventoryScreen) {
            AbstractContainerScreen<?> gui = (AbstractContainerScreen<?>) screen;
            boolean isCreative = screen instanceof CreativeModeInventoryScreen;
            int size = isCreative ? 8 : 10;
            int x = gui.getGuiLeft() + 126;
            int y = gui.getGuiTop() + (isCreative ? 38 : 63);
            evt.addListener(
                    new SlotLibButton(gui, x, y, size, size,
                            isCreative ? SlotLibButton.SMALL : SlotLibButton.BIG));
        }
    }
}
