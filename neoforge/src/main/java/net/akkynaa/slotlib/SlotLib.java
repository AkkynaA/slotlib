package net.akkynaa.slotlib;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.akkynaa.slotlib.client.ClientEventHandler;
import net.akkynaa.slotlib.client.KeyRegistry;
import net.akkynaa.slotlib.client.SlotLibClientConfig;
import net.akkynaa.slotlib.client.gui.GuiEventHandler;
import net.akkynaa.slotlib.client.gui.SlotLibScreen;
import net.akkynaa.slotlib.common.SlotLibRegistry;
import net.akkynaa.slotlib.common.event.SlotLibEventHandler;
import net.akkynaa.slotlib.common.network.NetworkHandler;

@Mod(SlotLibConstants.MODID)
public class SlotLib {

    public SlotLib(IEventBus eventBus, ModContainer modContainer) {
        SlotLibRegistry.init(eventBus);
        eventBus.addListener(this::registerPayloadHandler);
        eventBus.addListener(this::registerCaps);
        NeoForge.EVENT_BUS.register(new SlotLibEventHandler());
        modContainer.registerConfig(ModConfig.Type.COMMON, SlotLibConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, SlotLibClientConfig.CLIENT_SPEC);
    }

    private void registerPayloadHandler(final RegisterPayloadHandlersEvent evt) {
        NetworkHandler.register(evt.registrar("1.0"));
    }

    private void registerCaps(RegisterCapabilitiesEvent evt) {
        // SlotLib uses AttachmentType for data storage, no entity capability registration needed
    }

    @EventBusSubscriber(modid = SlotLibConstants.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static class ClientProxy {

        @SubscribeEvent
        public static void registerKeys(final RegisterKeyMappingsEvent evt) {
            evt.register(KeyRegistry.openSlotLib);
        }

        @SubscribeEvent
        public static void setupClient(FMLClientSetupEvent evt) {
            NeoForge.EVENT_BUS.register(new ClientEventHandler());
            NeoForge.EVENT_BUS.register(new GuiEventHandler());
        }

        @SubscribeEvent
        public static void registerMenuScreens(final RegisterMenuScreensEvent evt) {
            evt.register(SlotLibRegistry.SLOTLIB_MENU.get(), SlotLibScreen::new);
        }
    }
}
