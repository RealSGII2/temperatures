package dev.realsgii2.temperatures.registry;

import dev.realsgii2.temperatures.TemperaturesMod;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class ModPotions {
    private static final DeferredRegister<Potion> POTION_REGISTRY = DeferredRegister.create(ForgeRegistries.POTIONS, TemperaturesMod.MOD_ID);

    public static final int SHORT_DURATION = 9600; // 8 minutes
    public static final int LONG_DURATION = 28800; // 24 minutes

    public static final NamedPotion COLD_RESISTANCE_I_SHORT_ITEM = NamedPotion.basic("cold_resistance_i_short",
            ModEffects.COLD_RESISTANCE_EFFECT, SHORT_DURATION);

    public static final NamedPotion COLD_RESISTANCE_I_LONG_ITEM = NamedPotion.basic("cold_resistance_i_long",
            ModEffects.COLD_RESISTANCE_EFFECT, LONG_DURATION);

    public static final NamedPotion COLD_RESISTANCE_II_SHORT_ITEM = NamedPotion.strong("cold_resistance_ii_short",
            ModEffects.COLD_RESISTANCE_EFFECT, SHORT_DURATION);

    public static final NamedPotion COLD_RESISTANCE_II_LONG_ITEM = NamedPotion.strong("cold_resistance_ii_long",
            ModEffects.COLD_RESISTANCE_EFFECT, LONG_DURATION);

    public static final NamedPotion HEAT_RESISTANCE_I_SHORT_ITEM = NamedPotion.basic("heat_resistance_i_short",
            ModEffects.HEAT_RESISTANCE_EFFECT, SHORT_DURATION);

    public static final NamedPotion HEAT_RESISTANCE_I_LONG_ITEM = NamedPotion.basic("heat_resistance_i_long",
            ModEffects.HEAT_RESISTANCE_EFFECT, LONG_DURATION);

    public static final NamedPotion HEAT_RESISTANCE_II_SHORT_ITEM = NamedPotion.strong("heat_resistance_ii_short",
            ModEffects.HEAT_RESISTANCE_EFFECT, SHORT_DURATION);

    public static final NamedPotion HEAT_RESISTANCE_II_LONG_ITEM = NamedPotion.strong("heat_resistance_ii_long",
            ModEffects.HEAT_RESISTANCE_EFFECT, LONG_DURATION);

    public static final NamedPotion ICE_BREAKER_I_SHORT_ITEM = NamedPotion.basic("ice_breaker_i_short",
            ModEffects.ICE_BREAKER_EFFECT, SHORT_DURATION);

    public static final NamedPotion ICE_BREAKER_I_LONG_ITEM = NamedPotion.basic("ice_breaker_i_long",
            ModEffects.ICE_BREAKER_EFFECT, LONG_DURATION);

    public static final RegistryObject<NamedPotion> COLD_RESISTANCE_I_SHORT = register(COLD_RESISTANCE_I_SHORT_ITEM);
    public static final RegistryObject<NamedPotion> COLD_RESISTANCE_I_LONG = register(COLD_RESISTANCE_I_LONG_ITEM);
    public static final RegistryObject<NamedPotion> COLD_RESISTANCE_II_SHORT = register(COLD_RESISTANCE_II_SHORT_ITEM);
    public static final RegistryObject<NamedPotion> COLD_RESISTANCE_II_LONG = register(COLD_RESISTANCE_II_LONG_ITEM);
    public static final RegistryObject<NamedPotion> HEAT_RESISTANCE_I_SHORT = register(HEAT_RESISTANCE_I_SHORT_ITEM);
    public static final RegistryObject<NamedPotion> HEAT_RESISTANCE_I_LONG = register(HEAT_RESISTANCE_I_LONG_ITEM);
    public static final RegistryObject<NamedPotion> HEAT_RESISTANCE_II_SHORT = register(HEAT_RESISTANCE_II_SHORT_ITEM);
    public static final RegistryObject<NamedPotion> HEAT_RESISTANCE_II_LONG = register(HEAT_RESISTANCE_II_LONG_ITEM);
    public static final RegistryObject<NamedPotion> ICE_BREAKER_I_SHORT = register(ICE_BREAKER_I_SHORT_ITEM);
    public static final RegistryObject<NamedPotion> ICE_BREAKER_I_LONG = register(ICE_BREAKER_I_LONG_ITEM);

    private static RegistryObject<NamedPotion> register(@NotNull NamedPotion potion) {
        return POTION_REGISTRY.register(potion.publicName, () -> potion);
    }

    public static void register(IEventBus eventBus) {
        POTION_REGISTRY.register(eventBus);
    }

    public static ModRegistry.RegistryObjectMapper<Potion> getRegisteredObjects() {
        return ModRegistry.RegistryObjectMapper.ofRegistry(POTION_REGISTRY);
    }

    public static class NamedPotion extends Potion {
        public final String publicName;

        private NamedPotion(@Nullable String name, MobEffectInstance... effects) {
            super(name, effects);
            this.publicName = name;
        }

        @Contract("_, _, _ -> new")
        public static @NotNull NamedPotion basic(@Nullable String name, MobEffect effect, int duration) {
            return new NamedPotion(name, new MobEffectInstance(effect, duration));
        }

        @Contract("_, _, _ -> new")
        public static @NotNull NamedPotion strong(@Nullable String name, MobEffect effect, int duration) {
            return new NamedPotion(name, new MobEffectInstance(effect, duration, 1));
        }
    }
}
