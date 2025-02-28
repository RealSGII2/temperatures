package dev.realsgii2.temperatures.registry;

import dev.realsgii2.temperatures.TemperaturesMod;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.event.TickEvent;

public class ModDamageSources {
    private final Registry<DamageType> damageTypes;

    public final DamageSource FREEZE;
    public final DamageSource COLD;
    public final DamageSource HEAT;

    public ModDamageSources(RegistryAccess registry) {
        damageTypes = registry.registryOrThrow(Registries.DAMAGE_TYPE);

        FREEZE = source(ModDamageTypes.FREEZE);
        COLD = source(ModDamageTypes.COLD);
        HEAT = source(ModDamageTypes.HEAT);
    }

    public static ModDamageSources fromEvent(TickEvent.PlayerTickEvent event) {
        return new ModDamageSources(event.player.level().registryAccess());
    }

    private DamageSource source(ResourceKey<DamageType> damageTypeKey) {
        return new DamageSource(damageTypes.getHolderOrThrow(damageTypeKey));
    }

    private static class ModDamageTypes {
        // TODO: Consider moving to the native freezing DamageType
        public static final ResourceKey<DamageType> FREEZE = create("freeze");
        public static final ResourceKey<DamageType> COLD = create("cold");
        public static final ResourceKey<DamageType> HEAT = create("heat");

        private static ResourceKey<DamageType> create(String name) {
            return ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(TemperaturesMod.MOD_ID, name));
        }
    }
}
