package com.redgrapefruit.redmenu.redmenu.libgui

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.inventory.Inventories
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

/**
 * A reimplementation of menu block entity for use with LibGUI
 */
class AdvancedMenuBlockEntity(type: BlockEntityType<*>, pos: BlockPos, state: BlockState, private val size: Int)
    : BlockEntity(type, pos, state), DefaultedSidedInventory {

    private val items: DefaultedList<ItemStack> = DefaultedList.ofSize(size)

    override fun getItems(): DefaultedList<ItemStack> = items

    override fun markDirty() = Unit

    // Serialize the inventory

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        Inventories.readNbt(nbt, items)
    }

    override fun writeNbt(nbt: NbtCompound): NbtCompound {
        Inventories.writeNbt(nbt, items)
        return super.writeNbt(nbt)
    }
}
