/*
 * This file is a modified version of code originally from Curios API by TheIllusiveC4.
 * Original source: https://github.com/TheIllusiveC4/Curios
 * Licensed under LGPL-3.0-or-later. Modifications are licensed under GPL-3.0-or-later.
 */
package net.akkynaa.slotlib.common;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.akkynaa.slotlib.SlotLib;
import net.akkynaa.slotlib.common.capability.SlotLibInventory;
import net.akkynaa.slotlib.common.inventory.container.SlotLibContainer;

public class SlotLibRegistry {

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, SlotLib.MODID);
    private static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(Registries.MENU, SlotLib.MODID);

    public static final Supplier<MenuType<SlotLibContainer>> SLOTLIB_MENU =
            MENU_TYPES.register("slotlib_container",
                    () -> IMenuTypeExtension.create(SlotLibContainer::new));

    public static final Supplier<AttachmentType<SlotLibInventory>> INVENTORY =
            ATTACHMENT_TYPES.register("inventory",
                    () -> AttachmentType.serializable(SlotLibInventory::new)
                            .copyOnDeath()
                            .build());

    public static void init(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
        MENU_TYPES.register(eventBus);
    }
}
