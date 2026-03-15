package net.akkyna.slotlib.client;

import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class KeyRegistry {

    public static KeyMapping openSlotLib =
            new KeyMapping("key.slotlib.open.desc", GLFW.GLFW_KEY_G, "key.slotlib.category");
}
