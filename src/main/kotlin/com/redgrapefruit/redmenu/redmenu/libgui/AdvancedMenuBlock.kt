package com.redgrapefruit.redmenu.redmenu.libgui

import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos

/**
 * A reimplementation of menu block for use with LibGUI
 */
abstract class AdvancedMenuBlock(settings: Settings) : Block(settings), BlockEntityProvider {
    abstract fun makeEntity(pos: BlockPos, state: BlockState): BlockEntity

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = makeEntity(pos, state)
}
