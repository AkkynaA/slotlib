package net.akkynaa.slotlib;

import net.minecraftforge.common.ForgeConfigSpec;

public class SlotLibConfig {

    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.IntValue SLOT_COUNT;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("SlotLib Configuration");
        builder.push("general");
        SLOT_COUNT = builder
                .comment("Number of extra inventory slots (1-9)")
                .defineInRange("slotCount", 3, 1, 9);
        builder.pop();
        SPEC = builder.build();
    }
}
