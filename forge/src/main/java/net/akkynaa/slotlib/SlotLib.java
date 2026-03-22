package net.akkynaa.slotlib;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.eventbus.api.IEventBus;
import net.akkynaa.slotlib.common.SlotLibRegistry;
import net.akkynaa.slotlib.common.event.SlotLibEventHandler;
import net.akkynaa.slotlib.common.network.NetworkHandler;

@Mod(SlotLibConstants.MODID)
public class SlotLib {

    public SlotLib(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        SlotLibRegistry.init(modEventBus);
        NetworkHandler.register();
        MinecraftForge.EVENT_BUS.register(new SlotLibEventHandler());
        context.registerConfig(ModConfig.Type.COMMON, SlotLibConfig.SPEC);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> SlotLibClient.init(context));
    }
}
