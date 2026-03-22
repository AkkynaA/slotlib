/*
 * This file is a modified version of code originally from Curios API by TheIllusiveC4.
 * Original source: https://github.com/TheIllusiveC4/Curios
 * Licensed under LGPL-3.0-or-later. Modifications are licensed under GPL-3.0-or-later.
 */
package net.akkynaa.slotlib.common;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.akkynaa.slotlib.SlotLibConstants;
import net.akkynaa.slotlib.common.inventory.container.SlotLibContainer;

public class SlotLibRegistry {

    private static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, SlotLibConstants.MODID);

    public static final RegistryObject<MenuType<SlotLibContainer>> SLOTLIB_MENU =
            MENU_TYPES.register("slotlib_container",
                    () -> IForgeMenuType.create(SlotLibContainer::new));

    public static void init(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}
