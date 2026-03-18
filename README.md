# SlotLib

A heavily modified fork of [Curios API](https://github.com/TheIllusiveC4/Curios), designed specifically for [More Offhand Slots](https://github.com/AkkynaA/moreoffhandslots).
Provides configurable extra inventory slots that accept any item, with a dedicated GUI, networking, and persistence built in.
It is not published as a standalone mod (yet) - it is distributed embedded in More Offhand Slots via JarJar. The source is available here for anyone who wants to build on it.

## Issues
If you are here to give feedback on [More Offhand Slots](https://github.com/AkkynaA/moreoffhandslots), please refer to its own [GitHub issues page](https://github.com/AkkynaA/moreoffhandslots/issues).

## What it provides

- 1–9 extra inventory slots (configurable), rendered below the vanilla inventory
- Every basic slot management functionality from Curios
- Optional Curios compatibility (toggle buttons on each other's screens)

## Using SlotLib as a dependency

SlotLib is hosted on a [GitHub Pages Maven repository](https://maven.akkynaa.net).

```groovy
repositories {
    maven { url = "https://maven.akkynaa.net" }
}

dependencies {
    implementation "net.akkynaa:slotlib:21.1.0"
}
```

### Accessing a player's extra slots

```java
SlotLibInventory inv = player.getData(SlotLibRegistry.INVENTORY);
ItemStack stack = inv.getStackInSlot(0);
int count = inv.getSlotCount();
```

## License

[GPL-3.0-or-later](https://www.gnu.org/licenses/gpl-3.0.html)
