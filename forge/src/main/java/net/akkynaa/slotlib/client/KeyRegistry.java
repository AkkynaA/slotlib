/*
 * This file is a modified version of code originally from Curios API by TheIllusiveC4.
 * Original source: https://github.com/TheIllusiveC4/Curios
 * Licensed under LGPL-3.0-or-later. Modifications are licensed under GPL-3.0-or-later.
 */
package net.akkynaa.slotlib.client;

import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class KeyRegistry {

    public static KeyMapping openSlotLib =
            new KeyMapping("key.slotlib.open.desc", GLFW.GLFW_KEY_G, "key.slotlib.category");
}
