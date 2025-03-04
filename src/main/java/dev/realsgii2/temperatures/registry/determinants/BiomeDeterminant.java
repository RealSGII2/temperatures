package dev.realsgii2.temperatures.registry.determinants;

import com.ibm.icu.impl.Pair;
import dev.realsgii2.temperatures.Config;
import dev.realsgii2.temperatures.Util;
import dev.realsgii2.temperatures.api.registry.determinant.IDeterminant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The basis of Temperatures: gets the temperature of biomes, and blends
 * between multiple if needed.
 */
@SuppressWarnings("unused")
public class BiomeDeterminant implements IDeterminant.INameableDeterminant {
    @Override
    public String getName() {
        return "biome";
    }

    @Override
    public double getTemperature(Player player, double result) {
        Level level = player.level();

        List<Pair<Biome, Double>> nearbyBiomes = Util.World.getNearbyWeightedBiomes(player);
        result += Util.Mathf.weightedAverage(nearbyBiomes.stream().map(x -> Pair.of(getBiomeTemperature(level, x.first), x.second)).collect(Collectors.toList()));

        return result;
    }

    @Override
    public int overridePriority(Player player, double oldResult, double newResult) {
        return NO_OVERRIDE;
    }

    /**
     * Gets the current temperature of a biome, at the current time of day.
     * @param biome The biome to get the temperature of.
     */
    public static double getBiomeTemperature(Level level, Biome biome) {
        Config.Common.BiomeData biomeData = Config.Common.getBiome(biome);
        if (biomeData == null) return 0.0;
        else return getBiomeTemperature(level, biomeData);
    }

    /**
     * Gets the current temperature of a biome, at the current time of day.
     * @param biomeData The BiomeData of the biome to get the temperature of.
     */
    public static double getBiomeTemperature(Level level, Config.Common.BiomeData biomeData) {
        return Util.Mathf.lerp(biomeData.nightTemperature(), biomeData.dayTemperature(), getTimeValue(level));
    }

    /**
     * Gets the progress of the current day, from 0 to 1.
     * @param level The level to consider.
     */
    private static double getTimeValue(Level level) {
        return Math.sin(level.getDayTime() / (12000 / Math.PI)) / 2 + 0.5;
    }

    @Override
    public int order() {
        return RUN_FIRST;
    }
}
