package dev.realsgii2.temperatures.registry;

import dev.realsgii2.temperatures.TemperaturesMod;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collection;

@SuppressWarnings("unused")
public class ModEffects {
    private static final DeferredRegister<MobEffect> EFFECT_REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, TemperaturesMod.MOD_ID);

    public static final NamedEffect HEAT_RESISTANCE_EFFECT = new NamedEffect(MobEffectCategory.BENEFICIAL, 16747017);
    public static final NamedEffect COLD_RESISTANCE_EFFECT = new NamedEffect(MobEffectCategory.BENEFICIAL, 9762812);
    public static final NamedEffect ICE_BREAKER_EFFECT = new NamedEffect(MobEffectCategory.BENEFICIAL, 9762812);

    public static final RegistryObject<MobEffect> HEAT_RESISTANCE = EFFECT_REGISTRY.register("heat_resistance", () -> HEAT_RESISTANCE_EFFECT);
    public static final RegistryObject<MobEffect> COLD_RESISTANCE = EFFECT_REGISTRY.register("cold_resistance", () -> COLD_RESISTANCE_EFFECT);
    public static final RegistryObject<MobEffect> ICE_BREAKER = EFFECT_REGISTRY.register("ice_breaker", () -> ICE_BREAKER_EFFECT);

    public static void register(IEventBus eventBus) {
        EFFECT_REGISTRY.register(eventBus);
    }

    public static Collection<RegistryObject<MobEffect>> getRegisteredObjects() {
        return EFFECT_REGISTRY.getEntries();
    }

    public static class NamedEffect extends MobEffect {
        public NamedEffect(MobEffectCategory pCategory, int pColor) {
            super(pCategory, pColor);
        }
    }
}
