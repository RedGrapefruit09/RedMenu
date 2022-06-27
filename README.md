## RedMenu

**WARNING**: This library is under _maintenance_ with no further features being added.\
Prefer using built-in [LibGUI](https://github.com/CottonMC/LibGUI) APIs
and [Kotlin](https://kotlinlang.org) instead, where possible.

**NOTICE**: The library is written in [Kotlin](https://kotlinlang.org) and Java interoperability
hasn't been tested.

Features:

- A simple API for reducing tons of boilerplate when creating container/menu blocks
- Abstract and extensible implementations of:
    - `Block`
    - `BlockEntity`
    - `Screen`
    - `ScreenHandler`
- Default storage implementations of an:
    - `Inventory`
    - `SidedInventory`
- Dedicated support for [LibGUI](https://github.com/CottonMC/LibGUI) in `com.redgrapefruit.redmenu.redmenu.libgui`
- `ScreenHandler` slot management API for a scenario without using LibGUI's `WPanel`s:
    - Add player inventory slots
    - Add player hotbar slots
    - Place down slots in a dispenser-like handled grid
