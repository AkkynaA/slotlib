package net.akkynaa.slotlib;

import net.neoforged.neoforge.common.ModConfigSpec;

public class SlotLibConfig {

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.IntValue SLOT_COUNT;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.comment("SlotLib Configuration");
        builder.push("general");
        SLOT_COUNT = builder
                .comment("Number of extra inventory slots (1-9)")
                .defineInRange("slotCount", 3, 1, 9);
        builder.pop();
        SPEC = builder.build();
    }
}
