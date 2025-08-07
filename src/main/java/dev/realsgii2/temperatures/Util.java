package dev.realsgii2.temperatures;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.util.*;
import java.util.stream.Collectors;

public class Util {
    /**
     * Creates a HashMap often used by enchantments with a single key and value.
     */
    public static <K, V> HashMap<K, V> singleKey(K key, V value) {
        return new HashMap<>() {
            {
                put(key, value);
            }
        };
    }

    /**
     * Enchants an item with level 1.
     *
     * @param item        The item to enchant.
     * @param enchantment The enchantment to use.
     * @return The enchanted item.
     */
    public static ItemStack enchant(ItemStack item, Enchantment enchantment) {
        return enchant(item, enchantment, 1);
    }

    /**
     * Enchants an item.
     *
     * @param item        The item to enchant.
     * @param enchantment The enchantment to use.
     * @param level       The enchanted item.
     * @return The enchanted item.
     */
    public static ItemStack enchant(ItemStack item, Enchantment enchantment, int level) {
        EnchantmentHelper.setEnchantments(singleKey(enchantment, level), item);
        return item;
    }

    /**
     * Gets the resource location of a biome.
     *
     * @param biome The biome to get.
     */
    public static ResourceLocation getBiomeId(Level level,  Biome biome) {
        Optional<Registry<Biome>> biomeRegistry =
                level.registryAccess().registry(Registries.BIOME);
        return biomeRegistry.map(biomes -> biomes.getKey(biome)).orElse(null);
    }

    public static class Mathf {
        /**
         * Captures an interpolation of a value between a start a goal.
         *
         * @param a The start.
         * @param b The goal.
         * @param t The progress to capture
         * @return The captured value.
         */
        public static double lerp(double a, double b, double t) {
            return a + (b - a) * t;
        }

        /**
         * Captures an interpolation of a value between a start a goal.
         *
         * @param a The start.
         * @param b The goal.
         * @param t The progress to capture
         * @return The captured value.
         */
        public static float lerp(float a, float b, float t) {
            return a + (b - a) * t;
        }

        /**
         * Ensures that a number is within [lower, higher].
         *
         * @param x      The number to ensure.
         * @param lower  The lower bound.
         * @param higher The higher bound.
         * @return A number within [lower, higher],
         */
        public static double clamp(double x, double lower, double higher) {
            return java.lang.Math.min(java.lang.Math.max(x, lower), higher);
        }

        /**
         * Returns a weighted average.
         *
         * @param dataPoints A list of data values: Pair(Data Value, Weight).
         * @return The weighted average of dataPoints.
         */
        public static double weightedAverage(List<Pair<Double, Double>> dataPoints) {
            return dataPoints.stream().map(x -> x.first * x.second).reduce(0.0, Double::sum) / dataPoints.stream().map(x -> x.second).reduce(0.0, Double::sum);
        }
    }

    public static class World {
        /**
         * Gets all biomes within a 64 block area, then returns how much they cover that area.
         *
         * @param player The player to use.
         * @return The biomes and their corresponding weights.
         */
        public static List<Pair<Biome, Double>> getNearbyWeightedBiomes(Player player) {
            ArrayList<Biome> result = new ArrayList<>();

            for (BlockPos blockPos : getNearbyPositions(player.blockPosition(), 64, 2)) {
                Biome biome = player.level().getBiome(blockPos).get();
                result.add(biome);
            }

            return result.stream().distinct().map(x -> Pair.of(x,
                    (Collections.frequency(result, x) / (double) result.size()))).collect(Collectors.toList());
        }// Credit:

        /**
         * Gets positions nearby a position.
         *
         * @param pos      The centre of the search.
         * @param samples  The amount of samples to take.
         * @param interval The space between samples.
         * @return All positions considered.
         */
        // https://github.com/Momo-Studios/Cold-Sweat/blob/1.16.5-FG/src/main/java/dev/momostudios/coldsweat/util/world/WorldHelper.java#L81
        public static List<BlockPos> getNearbyPositions(BlockPos pos, int samples, int interval) {
            List<BlockPos> posList = new ArrayList<>();
            int sampleRoot = (int) Math.sqrt(samples);

            for (int sx = 0; sx < sampleRoot; sx++) {
                for (int sz = 0; sz < sampleRoot; sz++) {
                    int length = interval * sampleRoot;
                    posList.add(pos.offset(sx * interval - (length / 2), 0, sz * interval - (length / 2)));
                }
            }

            return posList;
        }

        /**
         * Gets three levels of positions nearby a position.
         *
         * @param pos      The centre of the search.
         * @param samples  The amount of samples to take.
         * @param interval The space between samples.
         * @return All positions considered.
         */
        public static List<BlockPos> getNearbyPositionsWithY(BlockPos pos, int samples, int interval) {
            List<BlockPos> result = getNearbyPositions(pos, samples, interval);
            result.addAll(getNearbyPositions(pos.below(), samples, interval));
            result.addAll(getNearbyPositions(pos.above(), samples, interval));

            return result;
        }
    }

    // Copied from IBM ICU's Pair class
    // Reason: Dedicated server crashes when using an import
    public static class Pair<F, S> {
        public final F first;
        public final S second;

        protected Pair(F first, S second) {
            this.first = first;
            this.second = second;
        }

        public static <F, S> Pair<F, S> of(F first, S second) {
            if (first != null && second != null) {
                return new Pair<>(first, second);
            } else {
                throw new IllegalArgumentException("Pair.of requires non null values.");
            }
        }

        public boolean equals(Object other) {
            if (other == this) {
                return true;
            } else if (!(other instanceof Pair<?, ?> rhs)) {
                return false;
            } else {
                return this.first.equals(rhs.first) && this.second.equals(rhs.second);
            }
        }

        public int hashCode() {
            return this.first.hashCode() * 37 + this.second.hashCode();
        }
    }

}
