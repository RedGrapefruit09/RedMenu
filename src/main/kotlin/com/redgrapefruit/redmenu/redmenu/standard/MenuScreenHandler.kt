package com.redgrapefruit.redmenu.redmenu.standard

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.ScreenHandlerListener
import net.minecraft.screen.slot.Slot

/**
 * A menu [ScreenHandler] providing different functions and utilities:
 *
 * - A built-in server constructor. The client constructor should contain the same, but create an empty inventory which will then be synced
 * - Embedded inventory taken from [MenuBlockEntity]
 * - Several events: [onSlotInit] allows you to add in slots, [onListenerInit] allows adding [ScreenHandlerListener]s (optional to implement)
 * - Built-in slot transfer (shift-clicking to move items in/out of the menu)
 * - Slot API: [addPlayerInventorySlots], [addPlayerHotbarSlots], [addGridSlot]. **Be warned: ** [addGridSlot] works in a tricky way, so be careful when using it
 */
abstract class MenuScreenHandler protected constructor(
    syncId: Int,
    playerInventory: PlayerInventory,
    inventory: Inventory,
    size: Int,
    type: ScreenHandlerType<*>
) : ScreenHandler(type, syncId){

    // Embedded inventory
    protected var inventory: Inventory

    /**
     * Server-side screen handler constructor
     */
    init {

        // Setup inventory
        checkSize(inventory, size)
        this.inventory = inventory
        this.inventory.onOpen(playerInventory.player)

        // Fire events
        onSlotInit(inventory, playerInventory)
        onListenerInit()
    }

    /**
     * An event reserved for putting slots on the screen handler
     *
     * @param inventory       Embedded inventory
     * @param playerInventory Player inventory
     */
    protected abstract fun onSlotInit(inventory: Inventory, playerInventory: PlayerInventory)

    /**
     * An event reserved for adding screen handler listeners onto the screen handler.
     *
     * Optional and not abstract, unlike [onSlotInit]
     */
    protected open fun onListenerInit() = Unit

    override fun canUse(player: PlayerEntity): Boolean {
        return inventory.canPlayerUse(player)
    }

    override fun transferSlot(player: PlayerEntity, invSlot: Int): ItemStack {
        // Some wizardry code to transfer the selected slot once ShiftClicked
        var newStack = ItemStack.EMPTY
        val slot = slots[invSlot]
        if (slot.hasStack()) {
            val originalStack = slot.stack
            newStack = originalStack.copy()
            if (invSlot < inventory.size()) {
                if (!insertItem(originalStack, inventory.size(), slots.size, true)) {
                    return ItemStack.EMPTY
                }
            } else if (!insertItem(originalStack, 0, inventory.size(), false)) {
                return ItemStack.EMPTY
            }
            if (originalStack.isEmpty) {
                slot.stack = ItemStack.EMPTY
            } else {
                slot.markDirty()
            }
        }
        return newStack
    }

    /**
     * Places a slot according to the **exact** grid (see `textures/gui/container/dispenser`)
     *
     * @param inventory Embedded inventory
     * @param index     Slot index
     * @param x         Grid X
     * @param y         Grid Y
     */
    protected fun addGridSlot(inventory: Inventory, index: Int, x: Int, y: Int) {
        addSlot(Slot(inventory, index, 62 + x * 18, 17 + y * 18))
    }

    /**
     * Adds all the slots from the player's inventory to the screen handler
     *
     * @param playerInventory Player's inventory
     */
    protected fun addPlayerInventorySlots(playerInventory: PlayerInventory) {
        for (m in 0..2) {
            for (l in 0..8) {
                addSlot(Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18))
            }
        }
    }

    /**
     * Adds all the slots from the player's hotbar to the screen handler
     *
     * @param playerInventory Player's inventory
     */
    protected fun addPlayerHotbarSlots(playerInventory: PlayerInventory) {
        for (m in 0..8) {
            addSlot(Slot(playerInventory, m, 8 + m * 18, 142))
        }
    }
}
