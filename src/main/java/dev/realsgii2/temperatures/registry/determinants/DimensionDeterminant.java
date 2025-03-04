package dev.realsgii2.temperatures.registry.determinants;

import dev.realsgii2.temperatures.api.registry.determinant.IDeterminant;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Overrides the temperature if the dimension is extreme.
 */
@SuppressWarnings("unused")
public class DimensionDeterminant implements IDeterminant.INameableDeterminant {
    @Override
    public String getName() {
        return "dimension";
    }

    @Override
    public double getTemperature(Player player, double result) {
        ResourceKey<Level> dimension = player.level().dimension();

        if (dimension == Level.NETHER)
            return 3.0;

        return result;
    }

    @Override
    public int overridePriority(Player player, double oldResult, double newResult) {
        return oldResult != newResult ? 100 : 0;
    }

    @Override
    public int order() {
        return RUN_LAST;
    }
}
