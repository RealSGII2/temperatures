package dev.realsgii2.temperatures.registry.determinants;

import dev.realsgii2.temperatures.Config;
import dev.realsgii2.temperatures.api.registry.determinant.IDeterminant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;

/**
 * Offsets the temperature if there is weather.
 */
@SuppressWarnings("unused")
public class WeatherDeterminant implements IDeterminant.INameableDeterminant {
    @Override
    public String getName() {
        return "weather";
    }

    @Override
    public double getTemperature(Player player, double result) {
        // Determine if it's raining or snowing
        Biome currentBiome = player.level().getBiome(player.blockPosition()).get();

        if (player.level().isRaining() && currentBiome.hasPrecipitation())
            if (currentBiome.coldEnoughToSnow(player.blockPosition()))
                result += Config.Common.getDiffInSnow() * player.level().rainLevel;
            else
                result += Config.Common.getDiffInRain() * player.level().rainLevel;

        return result;
    }

    @Override
    public int overridePriority(Player player, double oldResult, double newResult) {
        return NO_OVERRIDE;
    }

    @Override
    public int order() {
        return NO_ORDER;
    }
}
