package net.akkynaa.slotlib;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.akkynaa.slotlib.common.SlotLibRegistry;
import net.akkynaa.slotlib.common.event.SlotLibEventHandler;
import net.akkynaa.slotlib.common.network.NetworkHandler;
import org.slf4j.Logger;

@Mod(SlotLib.MODID)
public class SlotLib {

    public static final String MODID = "slotlib";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SlotLib(IEventBus modEventBus, ModContainer modContainer) {
        SlotLibRegistry.init(modEventBus);
        modEventBus.addListener(this::registerPayloads);
        NeoForge.EVENT_BUS.register(new SlotLibEventHandler());
        modContainer.registerConfig(ModConfig.Type.COMMON, SlotLibConfig.SPEC);
    }

    private void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(MODID).versioned("1.0.0");
        NetworkHandler.register(registrar);
    }
}
