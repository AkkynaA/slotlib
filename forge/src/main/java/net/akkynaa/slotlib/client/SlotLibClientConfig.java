/*
 * This file is a modified version of code originally from Curios API by TheIllusiveC4.
 * Original source: https://github.com/TheIllusiveC4/Curios
 * Licensed under LGPL-3.0-or-later. Modifications are licensed under GPL-3.0-or-later.
 */
package net.akkynaa.slotlib.client;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class SlotLibClientConfig {

    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final Client CLIENT;

    static {
        final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder()
                .configure(Client::new);
        CLIENT_SPEC = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    public static class Client {

        public final ForgeConfigSpec.EnumValue<ButtonCorner> buttonCorner;
        public final ForgeConfigSpec.IntValue buttonXOffset;
        public final ForgeConfigSpec.IntValue buttonYOffset;
        public final ForgeConfigSpec.IntValue creativeButtonXOffset;
        public final ForgeConfigSpec.IntValue creativeButtonYOffset;

        Client(ForgeConfigSpec.Builder builder) {
            builder.comment("Client-side settings for SlotLib").push("client");

            buttonCorner = builder.comment("The corner of the player model to place the SlotLib button")
                    .defineEnum("buttonCorner", ButtonCorner.BOTTOM_RIGHT);
            buttonXOffset = builder.comment("Additional X offset for the button in survival inventory")
                    .defineInRange("buttonXOffset", 0, -100, 100);
            buttonYOffset = builder.comment("Additional Y offset for the button in survival inventory")
                    .defineInRange("buttonYOffset", 0, -100, 100);
            creativeButtonXOffset = builder.comment("Additional X offset for the button in creative inventory")
                    .defineInRange("creativeButtonXOffset", 0, -100, 100);
            creativeButtonYOffset = builder.comment("Additional Y offset for the button in creative inventory")
                    .defineInRange("creativeButtonYOffset", 0, -100, 100);

            builder.pop();
        }

        public enum ButtonCorner {
            TOP_LEFT(28, 10, 75, 8),
            TOP_RIGHT(63, 10, 95, 8),
            BOTTOM_LEFT(28, 66, 75, 39),
            BOTTOM_RIGHT(63, 66, 95, 39);

            private final int xOffset;
            private final int yOffset;
            private final int creativeXOffset;
            private final int creativeYOffset;

            ButtonCorner(int x, int y, int creativeX, int creativeY) {
                this.xOffset = x;
                this.yOffset = y;
                this.creativeXOffset = creativeX;
                this.creativeYOffset = creativeY;
            }

            public int getXOffset() {
                return xOffset;
            }

            public int getYOffset() {
                return yOffset;
            }

            public int getCreativeXOffset() {
                return creativeXOffset;
            }

            public int getCreativeYOffset() {
                return creativeYOffset;
            }
        }
    }
}
