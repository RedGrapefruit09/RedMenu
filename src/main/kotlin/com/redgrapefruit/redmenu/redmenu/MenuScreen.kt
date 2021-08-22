package com.redgrapefruit.redmenu.redmenu

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text
import net.minecraft.util.Identifier

/**
 * A client-side menu [HandledScreen] providing rendering facilities:
 *
 * - Rendering of a single background texture provided in the abstract [texture] parameter
 * - Title & mouseover rendering
 * - An optional-to-implement [onRender] event allowing to add custom rendered elements
 */
abstract class MenuScreen protected constructor(
    handler: ScreenHandler,
    inventory: PlayerInventory,
    title: Text
) : HandledScreen<ScreenHandler>(handler, inventory, title) {

    /**
     * The GUI texture used for this [MenuScreen]
     */
    protected abstract val texture: Identifier

    /**
     * An event reserved for custom rendering and custom GUI elements.
     *
     * Not abstract, optional to implement.
     */
    protected open fun onRender(matrices: MatrixStack) = Unit

    override fun drawBackground(matrices: MatrixStack, delta: Float, mouseX: Int, mouseY: Int) {
        // This is the replacement of RenderSystem#color4f and binding textures in 1.17
        RenderSystem.setShader(GameRenderer::getPositionTexShader)
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F)
        RenderSystem.setShaderTexture(0, texture)
        // Calculate center position
        val x = (width - backgroundWidth) / 2
        val y = (height - backgroundHeight) / 2
        // Render the texture
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight)
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        // Render background and mouseover tooltip
        renderBackground(matrices)
        super.render(matrices, mouseX, mouseY, delta)
        drawMouseoverTooltip(matrices, mouseX, mouseY)
        // Fire event
        onRender(matrices)
    }

    override fun init() {
        super.init()
        // Center title
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2
    }
}
