package com.redgrapefruit.redmenu.redmenu

import net.minecraft.block.Block
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.gui.screen.Screen
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemPlacementContext
import net.minecraft.screen.ScreenHandler
import net.minecraft.state.StateManager
import net.minecraft.state.property.DirectionProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.*
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

/**
 * A menu [Block] providing several features:
 *
 * - Full synchronization with the connected [MenuBlockEntity]
 * - Opening of the [Screen] & [ScreenHandler] connected when right-clicked by the player
 * - Directional block features: rotates when placed to fit the facing of the player.
 */
abstract class MenuBlock protected constructor(settings: Settings) : BlockWithEntity(settings) {
    private lateinit var facing // Facing property
            : DirectionProperty

    init {
        // Defaults facing property
        defaultState =
            stateManager.defaultState.with(
                facing,
                Direction.NORTH
            )
    }

    /**
     * Checks if given [BlockEntity] is instanceof current [BlockEntity]
     *
     * @param blockEntity Given [BlockEntity]
     * @return Yes/No
     */
    protected abstract fun checkBlockEntity(blockEntity: BlockEntity): Boolean

    /**
     * Casts current [BlockEntity] to an inventory
     *
     * @param blockEntity Current block entity
     * @return Cast to inventory
     */
    protected abstract fun castToInventory(blockEntity: BlockEntity): Inventory

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        facing = Properties.HORIZONTAL_FACING
        builder.add(facing)
    }

    override fun rotate(state: BlockState, rotation: BlockRotation): BlockState =
        state.with(facing, rotation.rotate(state.get(facing)))

    override fun mirror(state: BlockState, mirror: BlockMirror): BlockState =
        state.rotate(mirror.getRotation(state.get(facing)))

    override fun getPlacementState(context: ItemPlacementContext): BlockState =
        defaultState.with(facing, context.playerFacing.opposite)

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        // This operation is server-side
        if (!world.isClient) {
            // Create a screen handler factory and open the screen on the player's HUD
            val screenHandlerFactory = state.createScreenHandlerFactory(world, pos)
            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory)
            }
        }
        return ActionResult.SUCCESS
    }

    override fun onStateReplaced(state: BlockState, world: World, pos: BlockPos, newState: BlockState, moved: Boolean) {
        // Proceed if the block has changed
        if (state.block !== newState.block) {
            val blockEntity = world.getBlockEntity(pos)!!

            // If the block entity is current block entity, drop everything and update comparators
            if (checkBlockEntity(blockEntity)) {
                ItemScatterer.spawn(world, pos, castToInventory(blockEntity))
                world.updateComparators(pos, this)
            }
            super.onStateReplaced(state, world, pos, newState, moved)
        }
    }

    override fun hasComparatorOutput(state: BlockState): Boolean = true

    override fun getComparatorOutput(state: BlockState, world: World, pos: BlockPos): Int =
        ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos))

    // BlockWithEntity resets BlockRenderType to INVISIBLE, so we set it back to MODEL
    override fun getRenderType(state: BlockState): BlockRenderType = BlockRenderType.MODEL
}
