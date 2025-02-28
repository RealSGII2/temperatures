package dev.realsgii2.temperatures.gui.boilerplate;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;

/**
 * A boilerplate class that represents a position with an anchor point.
 * @implNote May be used to store (X, Y) values: use the two-parameter constructor.
 */
public class GuiVector {
    /**
     * The X value of this GuiVector.
     */
    public final int x;

    /**
     * The Y value of this GuiVector.
     */
    public final int y;

    /**
     * The position this GuiVector is anchored to.
     */
    public final Anchor anchor;

    /**
     * Creates a GuiVector at (X, Y), anchored at the top left of the screen.
     * @param x The X value of this GuiVector.
     * @param y The Y value of this GuiVector.
     * @implNote The top left of the screen is considered (0, 0).
     */
    public GuiVector(int x, int y) {
        this(x, y, Anchor.TOP_LEFT);
    }

    /**
     * Creates a GuiVector at (X, Y), anchored at the provided anchor/
     * @param x The X value of this GuiVector.
     * @param y The Y value of this GuiVector.
     * @param anchor The edge of the screen to anchor this at.
     */
    public GuiVector(int x, int y, Anchor anchor) {
        this.x = x;
        this.y = y;
        this.anchor = anchor;
    }

    /**
     * Returns the position of an Anchor.
     * @param anchor The Anchor to get the position of.
     * @return The position of the passed in Anchor.
     */
    public static GuiVector ofAnchor(Anchor anchor) {
        GuiVector result;

        switch (anchor) {
            case TOP -> result = top();
            case BOTTOM -> result = bottom();
            case LEFT -> result = left();
            case RIGHT -> result = right();

            case CENTER -> result = centre();

            case TOP_LEFT -> result = topLeft();
            case TOP_RIGHT -> result = topRight();
            case BOTTOM_LEFT -> result = bottomLeft();
            case BOTTOM_RIGHT -> result = bottomRight();

            default -> result = new GuiVector(0, 0, anchor);
        }

        return result;
    }

    /**
     * @return The GuiVector located at the top left of the screen.
     */
    public static GuiVector topLeft() {
        return new GuiVector(0, 0, Anchor.TOP_LEFT);
    }

    /**
     * @return The GuiVector located at the top of the screen.
     */
    public static GuiVector top() {
        GuiVector windowSize = windowSize();
        return new GuiVector(windowSize.x / 2, 0, Anchor.TOP);
    }

    /**
     * @return The GuiVector located at the top right of the screen.
     */
    public static GuiVector topRight() {
        GuiVector windowSize = windowSize();
        return new GuiVector(windowSize.x, 0, Anchor.TOP_RIGHT);
    }

    /**
     * @return The GuiVector located at the right of the screen.
     */
    public static GuiVector right() {
        GuiVector windowSize = windowSize();
        return new GuiVector(windowSize.x, windowSize.y / 2, Anchor.RIGHT);
    }

    /**
     * @return The GuiVector located at the centre of the screen.
     */
    public static GuiVector centre() {
        GuiVector windowSize = windowSize();
        return new GuiVector(windowSize.x / 2, windowSize.y / 2, Anchor.CENTER);
    }

    /**
     * @return The GuiVector located at the left of the screen.
     */
    public static GuiVector left() {
        GuiVector windowSize = windowSize();
        return new GuiVector(0, windowSize.y / 2, Anchor.LEFT);
    }

    /**
     * @return The GuiVector located at the bottom left of the screen.
     */
    public static GuiVector bottomLeft() {
        GuiVector windowSize = windowSize();
        return new GuiVector(0, windowSize.y, Anchor.BOTTOM_LEFT);
    }

    /**
     * @return The GuiVector located at the bottom of the screen.
     */
    public static GuiVector bottom() {
        GuiVector windowSize = windowSize();
        return new GuiVector(windowSize.x / 2, windowSize.y, Anchor.BOTTOM_LEFT);
    }

    /**
     * @return The GuiVector located at the bottom right of the screen.
     */
    public static GuiVector bottomRight() {
        return windowSize();
    }

    /**
     * Returns a new GuiVector offset relative to the position of the anchor.
     * @param offsetX Shift the X value by this much.
     * @param offsetY Shift the Y value by this much.
     * @return A GuiVector that is offset by (offsetX, offsetY).
     */
    public GuiVector offsetRelative(int offsetX, int offsetY) {
        if (anchor.block.equals(AnchorBlock.BOTTOM)) offsetY = -offsetY;
        if (anchor.side.equals(AnchorSide.RIGHT)) offsetX = -offsetX;

        return offsetAbsolute(offsetX, offsetY);
    }

    /**
     * Returns a new GuiVector offset by (offsetX, offsetY)
     * @param offsetX Shift the X value by this much.
     * @param offsetY Shift the Y value by this much.
     * @return A GuiVector that is offset by (offsetX, offsetY).
     */
    public GuiVector offsetAbsolute(int offsetX, int offsetY) {
        return new GuiVector(x + offsetX, y + offsetY, anchor);
    }

    /**
     * Returns a new GuiVector offset enough to centre its element.
     * @param size The size to consider.
     * @return A GuiVector offset by (-size / 2, -size / 2).
     */
    public GuiVector accountSize(int size) {
        return offsetAbsolute(-size / 2, -size / 2);
    }

    /**
     * Returns the size of the window.
     * @return A GuiVector that contains the size of the window.
     */
    public static GuiVector windowSize() {
        final Minecraft mc = Minecraft.getInstance();
        final Window window = mc.getWindow();

        final int windowWidth = window.getGuiScaledWidth();
        final int windowHeight = window.getGuiScaledHeight();

        return new GuiVector(windowWidth, windowHeight, Anchor.BOTTOM_RIGHT);
    }

    /**
     * Represents a position where a GuiVector may be anchored to.
     */
    public enum Anchor {
        BOTTOM_LEFT(AnchorBlock.BOTTOM, AnchorSide.LEFT),
        BOTTOM(AnchorBlock.BOTTOM, AnchorSide.CENTER),
        BOTTOM_RIGHT(AnchorBlock.BOTTOM, AnchorSide.RIGHT),
        LEFT(AnchorBlock.CENTER, AnchorSide.LEFT),
        CENTER(AnchorBlock.CENTER, AnchorSide.CENTER),
        RIGHT(AnchorBlock.CENTER, AnchorSide.RIGHT),
        TOP_LEFT(AnchorBlock.TOP, AnchorSide.LEFT),
        TOP(AnchorBlock.TOP, AnchorSide.CENTER),
        TOP_RIGHT(AnchorBlock.TOP, AnchorSide.RIGHT);

        /**
         * The Y position of this anchor.
         */
        public final AnchorBlock block;

        /**
         * The X position of this anchor.
         */
        public final AnchorSide side;

        Anchor(AnchorBlock block, AnchorSide side) {
            this.block = block;
            this.side = side;
        }

        /**
         * Get the Anchor represented by (side, block).
         * @param side The X value of this anchor.
         * @param block The Y value of this anchor.
         */
        public static Anchor of(AnchorSide side, AnchorBlock block) {
            for (Anchor anchor : values()) {
                if (anchor.block == block && anchor.side == side)
                    return anchor;
            }

            return Anchor.TOP_LEFT;
        }

        /**
         * Gets the Anchor represented by (x, y) where x and y are either -1, 0, or 1.
         * @param x The X value of this anchor.
         * @param y The Y value of this anchor.
         * @return The Anchor representing (x, y).
         */
        public static Anchor of(int x, int y) {
            return of(AnchorSide.of(x), AnchorBlock.of(y));
        }
    }

    /**
     * Represents a vertical anchor.
     */
    public enum AnchorBlock {
        TOP,
        CENTER,
        BOTTOM;

        /**
         * Gets an anchor from a number.
         * @param y The Y value of this anchor (-1, 0, 1).
         * @return The anchor representing Y.
         */
        public static AnchorBlock of(int y) {
            return values()[y + 1];
        }
    }

    public enum AnchorSide {
        LEFT,
        CENTER,
        RIGHT;

        /**
         * Gets an anchor from a number.
         * @param x The X value of this anchor (-1, 0, 1).
         * @return The anchor representing X.
         */
        public static AnchorSide of(int x) {
            return values()[x + 1];
        }
    }
}