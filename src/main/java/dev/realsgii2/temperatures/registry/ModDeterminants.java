package dev.realsgii2.temperatures.registry;

import dev.realsgii2.temperatures.TemperaturesMod;
import dev.realsgii2.temperatures.api.registry.determinant.DeterminantRegistry;
import dev.realsgii2.temperatures.registry.determinants.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("unused")
public class ModDeterminants {
    public static final DeterminantRegistry registry = new DeterminantRegistry(TemperaturesMod.MOD_ID);

    public static final RegistryObject<AmbientDeterminant> AMBIENT_DETERMINANT = registry.register(AmbientDeterminant::new);
    public static final RegistryObject<BiomeDeterminant> BIOME_DETERMINANT = registry.register(BiomeDeterminant::new);
    public static final RegistryObject<DimensionDeterminant> DIMENSION_DETERMINANT = registry.register(DimensionDeterminant::new);
    public static final RegistryObject<IceWaterDeterminant> ICE_WATER_DETERMINANT = registry.register(IceWaterDeterminant::new);
    public static final RegistryObject<WeatherDeterminant> WEATHER_DETERMINANT = registry.register(WeatherDeterminant::new);

    public static final ResourceLocation AMBIENT_KEY = AMBIENT_DETERMINANT.getId();
    public static ResourceLocation BIOME_KEY = BIOME_DETERMINANT.getId();
    public static ResourceLocation DIMENSION_KEY = DIMENSION_DETERMINANT.getId();
    public static ResourceLocation ICE_WATER_KEY = ICE_WATER_DETERMINANT.getId();
    public static ResourceLocation WEATHER_KEY = WEATHER_DETERMINANT.getId();

    public static void register(IEventBus eventBus) {
        registry.register(eventBus);
    }
}
