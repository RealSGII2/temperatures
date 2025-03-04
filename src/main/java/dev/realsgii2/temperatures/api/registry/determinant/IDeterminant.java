package dev.realsgii2.temperatures.api.registry.determinant;

import dev.realsgii2.temperatures.model.INameable;
import net.minecraft.world.entity.player.Player;

@SuppressWarnings("unused")
public interface IDeterminant {
    /**
     * This Determinant doesn't override any values.
     */
    int NO_OVERRIDE = -1;

    /**
     * This Determinant runs after RUN_FIRST, but before RUN_LAST.
     * It should be used if the execution order doesn't matter.
     */
    int NO_ORDER = -1;

    /**
     * Runs this Determinant before every other one.
     * To offset it, add to this value.
     */
    int RUN_FIRST = -Integer.MAX_VALUE;

    /**
     * Runs this Determinant after every other one.
     * To offset it, subtract from this value.
     */
    int RUN_LAST = Integer.MAX_VALUE;

    /**
     * Modifies the result of the previous returned Determinants.
     * @param player The player to base this calculation off of.
     * @param current The current stored result.
     * @return The value to replace `current` with.
     */
    double getTemperature(Player player, double current);

    /**
     * Determines whether this determinant's result should override if others.
     * If multiple overrides are used, the one that returns the <b>highest</b>
     * value will be used.
     * @param player The player to base this calculation off of.
     * @param oldResult The result before running this determinant.
     * @param newResult The result after running this determinant.
     * @return An integer representing how important this override is. Return {@link #NO_OVERRIDE} to ignore.
     */
    int overridePriority(Player player, double oldResult, double newResult);

    /**
     * Sets the order this Determinant is run in.
     * <p>
     * Predefined values:
     * - {@link #RUN_FIRST}
     * - {@link #NO_ORDER} (ran in between the two other predefined values)
     * - {@link #RUN_LAST}
     */
    int order();

    /**
     * Allows a Determinant to be named.
     */
    interface INameableDeterminant extends IDeterminant, INameable {}
}