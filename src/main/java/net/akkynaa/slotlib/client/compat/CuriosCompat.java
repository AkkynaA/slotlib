package net.akkynaa.slotlib.client.compat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import top.theillusivec4.curios.api.client.ICuriosScreen;
import top.theillusivec4.curios.client.gui.CuriosButton;
import top.theillusivec4.curios.client.gui.CuriosScreen;
import top.theillusivec4.curios.common.network.client.CPacketOpenCurios;
import net.akkynaa.slotlib.client.gui.SlotLibButton;
import net.akkynaa.slotlib.client.gui.SlotLibScreen;

public class CuriosCompat {

    public static boolean isCuriosScreen(Screen screen) {
        return screen instanceof ICuriosScreen;
    }

    public static void addSlotLibButtonToCuriosScreen(ScreenEvent.Init.Post evt,
                                                       AbstractContainerScreen<?> gui) {
        int size = 10;
        int[] pos = SlotLibButton.getButtonPosition(gui);
        evt.addListener(new SlotLibButton(gui, pos[0], pos[1], size, size, SlotLibButton.BIG));
    }

    public static ImageButton createCuriosButtonForSlotLibScreen(SlotLibScreen screen) {
        return new ImageButton(0, 0, 10, 10, CuriosButton.BIG,
                button -> {
                    Minecraft mc = Minecraft.getInstance();
                    if (mc.player != null) {
                        ItemStack stack = mc.player.containerMenu.getCarried();
                        mc.player.containerMenu.setCarried(ItemStack.EMPTY);
                        mc.player.connection.send(
                                new ServerboundContainerClosePacket(mc.player.containerMenu.containerId));
                        mc.player.containerMenu = mc.player.inventoryMenu;
                        PacketDistributor.sendToServer(new CPacketOpenCurios(stack));
                    }
                }) {
            @Override
            public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
                // Reposition every frame using Curios' own offset config
                Tuple<Integer, Integer> offsets = CuriosScreen.getButtonOffset(false);
                this.setX(screen.getGuiLeft() + offsets.getA() + 2);
                this.setY(screen.getGuiTop() + offsets.getB() + 85);
                super.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);
            }
        };
    }
}
