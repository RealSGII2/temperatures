package dev.realsgii2.temperatures.api.registry;

import dev.realsgii2.temperatures.TemperaturesMod;
import dev.realsgii2.temperatures.api.registry.determinant.IDeterminant;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

/**
 * Custom resource keys exposed by the mod that point to the mods' registries.
 */
public class TemperatureRegistries {
    public static final ResourceKey<Registry<IDeterminant>> DETERMINANTS =
            ResourceKey.createRegistryKey(
                    TemperaturesMod.location("determinants")
            );
}
