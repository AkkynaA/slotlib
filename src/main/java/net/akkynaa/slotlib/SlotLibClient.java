package net.akkynaa.slotlib;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.akkynaa.slotlib.client.ClientEventHandler;
import net.akkynaa.slotlib.client.KeyRegistry;
import net.akkynaa.slotlib.client.gui.GuiEventHandler;
import net.akkynaa.slotlib.client.gui.SlotLibScreen;
import net.akkynaa.slotlib.common.SlotLibRegistry;

@Mod(value = SlotLib.MODID, dist = Dist.CLIENT)
public class SlotLibClient {

    public SlotLibClient(IEventBus modEventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(new GuiEventHandler());
        NeoForge.EVENT_BUS.register(new ClientEventHandler());
        modEventBus.addListener(this::onRegisterMenuScreens);
        modEventBus.addListener(this::onRegisterKeyMappings);
    }

    private void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(SlotLibRegistry.SLOTLIB_MENU.get(), SlotLibScreen::new);
    }

    private void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(KeyRegistry.openSlotLib);
    }
}
