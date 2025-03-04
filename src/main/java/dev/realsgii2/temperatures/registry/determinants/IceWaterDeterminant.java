package dev.realsgii2.temperatures.registry.determinants;

import dev.realsgii2.temperatures.api.registry.determinant.IDeterminant;
import net.minecraft.world.entity.player.Player;

/**
 * Freezes the player if they're in water in a cold biome.
 */
@SuppressWarnings("unused")
public class IceWaterDeterminant implements IDeterminant.INameableDeterminant {
    @Override
    public String getName() {
        return "ice_water";
    }

    @Override
    public double getTemperature(Player player, double result) {
        if (player.isInWater() && result < -1.0)
            return -3.0;

        return result;
    }

    @Override
    public int overridePriority(Player player, double oldResult, double newResult) {
        return newResult == -3.0 ? 100 : 0;
    }

    @Override
    public int order() {
        // Run right after the biome determinant is run:
        // We want to avoid using ambient calculations as well.
        return RUN_FIRST + 1;
    }
}
