package net.akkynaa.slotlib;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.akkynaa.slotlib.client.ClientEventHandler;
import net.akkynaa.slotlib.client.KeyRegistry;
import net.akkynaa.slotlib.client.SlotLibClientConfig;
import net.akkynaa.slotlib.client.gui.GuiEventHandler;
import net.akkynaa.slotlib.client.gui.SlotLibScreen;
import net.akkynaa.slotlib.common.SlotLibRegistry;

public class SlotLibClient {

    public static void init(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        MinecraftForge.EVENT_BUS.register(new GuiEventHandler());
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        modEventBus.addListener(SlotLibClient::onClientSetup);
        modEventBus.addListener(SlotLibClient::onRegisterKeyMappings);
        context.registerConfig(ModConfig.Type.CLIENT, SlotLibClientConfig.CLIENT_SPEC);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() ->
                MenuScreens.register(SlotLibRegistry.SLOTLIB_MENU.get(), SlotLibScreen::new));
    }

    private static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(KeyRegistry.openSlotLib);
    }
}
