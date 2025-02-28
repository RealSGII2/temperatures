package dev.realsgii2.temperatures.registry;

import dev.realsgii2.temperatures.TemperaturesMod;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class ModEnchantments {
    private static final DeferredRegister<Enchantment> ENCHANTMENT_REGISTRY = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, TemperaturesMod.MOD_ID);

    public static final NamedEnchantment COLD_RESISTANCE_ENCHANTMENT = NamedEnchantment.armor("cold_resistance", Enchantment.Rarity.UNCOMMON);
    public static final NamedEnchantment HEAT_RESISTANCE_ENCHANTMENT = NamedEnchantment.armor("heat_resistance", Enchantment.Rarity.UNCOMMON);
    public static final NamedEnchantment ICE_BREAKER_ENCHANTMENT = NamedEnchantment.armor("ice_breaker", Enchantment.Rarity.RARE);
    public static final NamedEnchantment FLAME_BREAKER_ENCHANTMENT = NamedEnchantment.armor("flame_breaker", Enchantment.Rarity.RARE);

    public static final RegistryObject<NamedEnchantment> COLD_RESISTANCE = register(COLD_RESISTANCE_ENCHANTMENT);
    public static final RegistryObject<NamedEnchantment> HEAT_RESISTANCE = register(HEAT_RESISTANCE_ENCHANTMENT);
    public static final RegistryObject<NamedEnchantment> ICE_BREAKER = register(ICE_BREAKER_ENCHANTMENT);
    public static final RegistryObject<NamedEnchantment> FLAME_BREAKER = register(FLAME_BREAKER_ENCHANTMENT);

    private static RegistryObject<NamedEnchantment> register(@NotNull NamedEnchantment enchantment) {
        return ENCHANTMENT_REGISTRY.register(enchantment.publicName, () -> enchantment);
    }

    public static void register(IEventBus eventBus) {
        ENCHANTMENT_REGISTRY.register(eventBus);
    }

    public static ModRegistry.RegistryObjectMapper<Enchantment> getRegisteredObjects() {
        return ModRegistry.RegistryObjectMapper.ofRegistry(ENCHANTMENT_REGISTRY);
    }

    public static class NamedEnchantment extends Enchantment {
        public final String publicName;

        protected NamedEnchantment(String publicName, Rarity pRarity, EquipmentSlot[] pApplicableSlots) {
            super(pRarity, EnchantmentCategory.ARMOR, pApplicableSlots);
            this.publicName = publicName;
        }

        public static NamedEnchantment armor(String publicName, Rarity pRarity) {
            return new NamedEnchantment(publicName, pRarity, new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});
        }
    }
}
