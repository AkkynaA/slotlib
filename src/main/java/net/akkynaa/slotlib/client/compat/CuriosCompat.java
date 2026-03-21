package net.akkynaa.slotlib.client.compat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.network.PacketDistributor;
import top.theillusivec4.curios.api.client.ICuriosScreen;
import top.theillusivec4.curios.client.gui.CuriosScreen;
import top.theillusivec4.curios.common.network.client.CPacketOpenCurios;
import top.theillusivec4.curios.common.network.NetworkHandler;
import net.akkynaa.slotlib.client.gui.SlotLibButton;
import net.akkynaa.slotlib.client.gui.SlotLibScreen;

public class CuriosCompat {

    private static final ResourceLocation CURIOS_INVENTORY =
            new ResourceLocation("curios", "textures/gui/inventory.png");

    public static boolean isCuriosScreen(Screen screen) {
        return screen instanceof ICuriosScreen;
    }

    public static void addSlotLibButtonToCuriosScreen(ScreenEvent.Init.Post evt,
                                                       AbstractContainerScreen<?> gui) {
        int size = 10;
        int[] pos = SlotLibButton.getButtonPosition(gui);
        evt.addListener(new SlotLibButton(gui, pos[0], pos[1], size, size, false));
    }

    public static AbstractWidget createCuriosButtonForSlotLibScreen(SlotLibScreen screen) {
        return new AbstractWidget(0, 0, 14, 14, Component.empty()) {

            @Override
            public void onClick(double mouseX, double mouseY) {
                Minecraft mc = Minecraft.getInstance();
                if (mc.player != null) {
                    ItemStack stack = mc.player.containerMenu.getCarried();
                    mc.player.containerMenu.setCarried(ItemStack.EMPTY);
                    mc.player.connection.send(
                            new ServerboundContainerClosePacket(mc.player.containerMenu.containerId));
                    mc.player.containerMenu = mc.player.inventoryMenu;
                    NetworkHandler.INSTANCE.send(new CPacketOpenCurios(stack), PacketDistributor.SERVER.noArg());
                }
            }

            @Override
            public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
                Tuple<Integer, Integer> offsets = CuriosScreen.getButtonOffset(false);
                this.setX(screen.getGuiLeft() + offsets.getA());
                this.setY(screen.getGuiTop() + offsets.getB() + 83);

                int yTex = this.isHovered ? 14 : 0;
                guiGraphics.blit(CURIOS_INVENTORY, this.getX(), this.getY(), 50, yTex,
                        this.width, this.height, 256, 256);
            }

            @Override
            protected void updateWidgetNarration(NarrationElementOutput output) {
            }
        };
    }
}
