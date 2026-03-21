package net.akkynaa.slotlib;

import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.eventbus.api.IEventBus;
import net.akkynaa.slotlib.common.SlotLibRegistry;
import net.akkynaa.slotlib.common.event.SlotLibEventHandler;
import net.akkynaa.slotlib.common.network.NetworkHandler;
import org.slf4j.Logger;

@Mod(SlotLib.MODID)
public class SlotLib {

    public static final String MODID = "slotlib";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SlotLib() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        SlotLibRegistry.init(modEventBus);
        NetworkHandler.register();
        MinecraftForge.EVENT_BUS.register(new SlotLibEventHandler());
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SlotLibConfig.SPEC);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> SlotLibClient.init(modEventBus));
    }
}
