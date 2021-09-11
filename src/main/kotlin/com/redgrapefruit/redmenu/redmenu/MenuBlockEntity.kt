package com.redgrapefruit.redmenu.redmenu

import net.minecraft.block.BlockState
import net.minecraft.block.InventoryProvider
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.WorldAccess

/**
 * A menu [BlockEntity] acting as an [ImplementedInventory] implementation and managing an inventory.
 *
 * This class can be easily extended to add more NBT-serialized properties, custom functionality, ticking etc.
 */
abstract class MenuBlockEntity protected constructor(type: BlockEntityType<*>, pos: BlockPos, state: BlockState, size: Int)
    : BlockEntity(type, pos, state), ImplementedSidedInventory, NamedScreenHandlerFactory, InventoryProvider {

    // Embedded inventory represented through a DefaultedList
    protected val inventory: DefaultedList<ItemStack> = DefaultedList.ofSize(size, ItemStack.EMPTY)

    override fun getDisplayName(): Text = TranslatableText(cachedState.block.translationKey)

    override fun markDirty() = Unit

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        Inventories.readNbt(nbt, inventory)
    }

    override fun writeNbt(nbt: NbtCompound): NbtCompound {
        super.writeNbt(nbt)
        Inventories.writeNbt(nbt, inventory)
        return nbt
    }

    override fun getInventory(state: BlockState, world: WorldAccess, pos: BlockPos): SidedInventory = this
}
