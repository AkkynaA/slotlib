package net.akkynaa.slotlib.client;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class SlotLibClientConfig {

    public static final ModConfigSpec CLIENT_SPEC;
    public static final Client CLIENT;

    static {
        final Pair<Client, ModConfigSpec> specPair = new ModConfigSpec.Builder()
                .configure(Client::new);
        CLIENT_SPEC = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    public static class Client {

        public final ModConfigSpec.EnumValue<ButtonCorner> buttonCorner;
        public final ModConfigSpec.IntValue buttonXOffset;
        public final ModConfigSpec.IntValue buttonYOffset;
        public final ModConfigSpec.IntValue creativeButtonXOffset;
        public final ModConfigSpec.IntValue creativeButtonYOffset;

        Client(ModConfigSpec.Builder builder) {
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
            // Offsets relative to guiLeft/guiTop for survival and creative inventories
            // Survival player model area: roughly x=26..75, y=8..78
            // Creative player model area: roughly x=73..105, y=8..45
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
