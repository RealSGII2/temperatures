package dev.realsgii2.temperatures.gui.boilerplate;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

/**
 * Boilerplate for {@link GuiGraphics} to allow {@link GuiVector}s to be used, and to remove
 * general clutter made when dealing with the Blaze3D RenderSystem.
 */
public class GraphicsRenderer {
    private int debugYValue = 5;

    private final GuiGraphics _graphics;
    private final float _deltaTime;

    public GraphicsRenderer(GuiGraphics graphics, float deltaTime) {
        this._graphics = graphics;
        this._deltaTime = deltaTime;
    }

    /**
     * The amount of seconds passed since the last frame was rendered.
     * @return The deltaTime of this frame.
     */
    public float deltaTime() {
        return this._deltaTime;
    }

    /**
     * Appends a debug message used when Config.Common.debug is true.
     * @param message The message to display.
     */
    public void debug(String message) {
        string(message, 5, debugYValue);

        debugYValue += 10;
    }

    /**
     * Draws a string on the screen.
     * @param text The string to render.
     * @param x The X position of the string.
     * @param y The Y position of the string.
     */
    public void string(String text, int x, int y) {
        string(text, x, y, 16777215);
    }

    /**
     * Draws a string on the screen.
     * @param text The string to render.
     * @param x The X position of the string.
     * @param y The Y position of the string.
     * @param color The color of the string.
     */
    public void string(String text, int x, int y, int color) {
        _graphics.drawString(Minecraft.getInstance().font, text, x, y, color);
    }

    /**
     * Draws a square image on the screen.
     * @param location The location of this texture.
     * @param position The position of the image.
     * @param size The size of the image.
     */
    public void draw(ResourceLocation location, @NotNull GuiVector position, int size) {
        _graphics.blit(location, position.x, position.y, 0, 0, size, size, size, size);
    }

    /**
     * Sets the alpha value of all draw calls after this method.
     * @param alpha The alpha to use: within [0, 1]
     */
    public void alpha(float alpha) {
        RenderSystem.setShaderColor(1, 1, 1, alpha);
    }

    /**
     * Sets the alpha value of all draw calls in a stack.
     * @param alpha The alpha to use: within [0, 1]
     * @param stack The stack to apply this alpha to.
     */
    public void alpha(float alpha, Runnable stack) {
        alpha(alpha);
        stack.run();
        alpha(1);
    }

    /**
     * Allows alpha passthrough of a stack.
     * @param stack The stack to allow passthrough on.
     */
    public void useAlphaShader(Runnable stack) {
        enableAlphaShader();
        stack.run();
        disableAlphaShader();
    }

    /**
     * Enables alpha passthrough.
     */
    private void enableAlphaShader() {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    /**
     * Disables alpha passthrough.
     */
    private void disableAlphaShader() {
        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    /**
     * Create a {@link Transform} to modify the scale and position of draw calls.
     * @return A new Transform.
     */
    public Transform transform() {
        return new Transform(this);
    }

    /**
     * Transforms GraphicsRenderer draw calls.
     */
    public class Transform {
        private final GraphicsRenderer renderer;

        private double _rotation = 0.0;
        private double _scale = 1.0;

        public Transform(GraphicsRenderer renderer) {
            this.renderer = renderer;
        }

        /**
         * Rotate images by this amount in degrees.
         * @param rotation The rotation to apply.
         * @return A chainable Transform.
         */
        public Transform rotate(double rotation) {
            this._rotation += rotation;
            return this;
        }

        /**
         * Scale images by this amount.
         * @param scale The scale to apply.
         * @return A chainable Transform.
         */
        public Transform scale(double scale) {
            this._scale = scale;
            return this;
        }

        /**
         * Draws a square image on the screen.
         * @param location The location of this texture.
         * @param position The position of the image.
         * @param size The size of the image.
         */
        public void draw(ResourceLocation location, @NotNull GuiVector position, int size) {
            PoseStack pose = _graphics.pose();

            pose.pushPose();
            pose.translate(position.x + (float) size / 2, position.y + (float) size / 2, 0);
            pose.mulPose(new Quaternionf().rotateLocalZ((float) Math.toRadians(_rotation)));
            pose.scale((float) _scale, (float) _scale, (float) _scale);
            pose.translate(-(position.x + (float) size / 2), -(position.y + (float) size / 2), 0);

            renderer.draw(location, position, size);

            pose.popPose();
        }
    }
}
