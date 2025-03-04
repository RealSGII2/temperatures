package dev.realsgii2.temperatures.api.registry.determinant;

import com.ibm.icu.impl.Pair;
import dev.realsgii2.temperatures.TemperaturesMod;
import dev.realsgii2.temperatures.Util;
import dev.realsgii2.temperatures.api.registry.TemperatureRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A wrapper class that handles:
 * - Registering {@link IDeterminant}s with support for {@link dev.realsgii2.temperatures.model.INameable}s.
 * - Getting subsets of or all IDeterminants.
 * - Executing Determinants to get the temperature of the player's position.
 */
public class DeterminantRegistry {
    /**
     * The internal DeferredRegister.
     */
    public final DeferredRegister<IDeterminant> register;

    /**
     * The IForgeRegistry used internally.
     */
    private static Supplier<IForgeRegistry<IDeterminant>> primaryRegistry = () -> null;

    public DeterminantRegistry(String namespace) {
        register = DeferredRegister.create(TemperatureRegistries.DETERMINANTS, TemperaturesMod.MOD_ID);

        if (namespace.equals(TemperaturesMod.MOD_ID)) {
            primaryRegistry = register.makeRegistry(() -> new RegistryBuilder<IDeterminant>()
                    .disableOverrides()
            );
        }
    }

    /**
     * Registers this instance to an event bus.
     * @param bus The bus to register to.
     */
    public void register(IEventBus bus) {
        register.register(bus);
    }

    /**
     * Registers an {@link IDeterminant} to this registry.
     * @param name The name of this registry object.
     * @param determinant The determinant to register.
     * @return The registry object created.
     * @param <T> The extension of IDeterminant to add.
     */
    public <T extends IDeterminant> RegistryObject<T> register(String name, Supplier<T> determinant) {
        return register.register(name, determinant);
    }

    /**
     * Registers a {@link IDeterminant.INameableDeterminant} to this registry.
     * @param determinant The determinant to register.
     * @return The registry object created.
     * @param <T> The extension of IDeterminant to add.
     */
    public <T extends IDeterminant.INameableDeterminant> RegistryObject<T> register(Supplier<T> determinant) {
        return register(determinant.get().getName(), determinant);
    }

    /**
     * Gets all keys this registry knows.
     */
    public static Set<ResourceLocation> keys() {
        return primaryRegistry.get().getKeys();
    }

    /**
     * Gets all the IDeterminants this registry knows.
     */
    public static List<IDeterminant> getAll() {
        return getAll(keys());
    }

    /**
     * Gets a subset of IDeterminants based on their IDs.
     * @param ids The IDs to get.
     */
    public static List<IDeterminant> getAll(Set<ResourceLocation> ids) {
        Objects.requireNonNull(ids);

        List<IDeterminant> result = new ArrayList<>();
        for (ResourceLocation id : ids) result.add(primaryRegistry.get().getValue(id));

        return result;
    }

    /**
     * Gets the subset of IDeterminants that do not match these IDs.
     * @param excludedIds The IDs to exclude.
     */
    public static List<IDeterminant> getExcluded(ResourceLocation... excludedIds) {
        return getAll(keys().stream().filter(x -> !Arrays.asList(excludedIds).contains(x)).collect(Collectors.toSet()));
    }

    /**
     * Executes all passed IDeterminants to get the current temperature.
     * @param player The player to base the determinant off of.
     * @param determinants The determinants to use.
     * @return A number from [-3, 3] representing the combined result of all determinants.
     */
    public static double compute(Player player, List<IDeterminant> determinants) {
        Objects.requireNonNull(determinants);

        determinants.sort(Comparator.comparingInt(IDeterminant::order));

        double result = 0.0;
        Map<Integer, Double> possibleOverrides = new HashMap<>();

        for (IDeterminant determinant : determinants) {
            double oldResult = result;
            result = determinant.getTemperature(player, result);

            int overridePriority = determinant.overridePriority(player, oldResult, result);
            if (overridePriority != IDeterminant.NO_OVERRIDE) {
                possibleOverrides.put(overridePriority, result);
            }
        }

        if (possibleOverrides.isEmpty())
            return Util.Mathf.clamp(result, -2.0, 2.0);
        else {
            Pair<Integer, Double> largestOverride = Pair.of(IDeterminant.NO_OVERRIDE, 0.0);

            for (int key : possibleOverrides.keySet()) {
                double value = possibleOverrides.get(key);
                if (key > largestOverride.first) {
                    largestOverride = Pair.of(key, value);
                }
            }

            return Util.Mathf.clamp(largestOverride.second, -3.0, 3.0);
        }
    }
}
