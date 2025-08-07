package dev.realsgii2.temperatures.handler;

import dev.realsgii2.temperatures.Config;
import dev.realsgii2.temperatures.Util;
import dev.realsgii2.temperatures.api.registry.determinant.DeterminantRegistry;
import dev.realsgii2.temperatures.registry.ModDamageSources;
import dev.realsgii2.temperatures.registry.ModDeterminants;
import dev.realsgii2.temperatures.registry.ModEffects;
import dev.realsgii2.temperatures.registry.ModEnchantments;
import net.minecraft.client.Minecraft;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.event.TickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Handles temperature operations, such as getting temperature, protection levels, and
 * provides many methods to determine how the player should be affected by it.
 * <p>
 * Temperature is represented by a number from -3 to 3, where:
 * |x| ==  3 : The player is in an extreme temperature
 * |x| > 1.5 : The player is in very uncomfortable temperatures.
 * |x| > 1.0 : The player is in uncomfortable temperatures.
 * Otherwise, the player is comfortable.
 */
public class Temperature {
    public static final double MAX_VALUE = 3.0;
    public static final double MIN_VALUE = -MAX_VALUE;

    private final Player player;

    /**
     * Gets a Temperature object based on the current player.
     */
    public Temperature() {
        this(Objects.requireNonNull(Minecraft.getInstance().player));
    }

    /**
     * Gets a Temperature object of a specific player.
     *
     * @param player The player to base this Temperature off of.
     */
    public Temperature(@NotNull Player player) {
        this.player = player;
    }

    /**
     * Gets the damage type and amount that should be applied to the player based on current conditions.
     * Intended to be used in a PlayerTickEvent listener.
     *
     * @param event The event to extract data from.
     * @return [DamageSource, Amount to be applied] or null if none should be applied.
     * @implNote Also call {@link #isPlayerBurning()} to see if the player should be set on fire.
     */
    public Util.Pair<DamageSource, Integer> getPossibleDamage(TickEvent.PlayerTickEvent event) {
        ModDamageSources damageSources = ModDamageSources.fromEvent(event);

        if (isPlayerFreezing() && event.player.tickCount % Config.Server.getExtremeDamageTick() == 0)
            return Util.Pair.of(damageSources.FREEZE, Config.Server.getExtremeDamageAmount());

        if (event.player.tickCount % Config.Server.getNormalDamageTick() == 0)
            if (isPlayerCold() && !isAmbientFreezing())
                return Util.Pair.of(damageSources.COLD, Config.Server.getNormalDamageAmount());
            else if (isPlayerHot()) {
                if (isPlayerBurning())
                    // Set the player on fire instead; don't apply custom damage.
                    return null;
                else if (!isAmbientBurning()) return Util.Pair.of(damageSources.HEAT, Config.Server.getNormalDamageAmount());
            }

        return null;
    }

    /**
     * Gets the current temperature using all available {@link dev.realsgii2.temperatures.api.registry.determinant.IDeterminant}s.
     * @return The temperature of the player.
     */
    public double compute() {
        return DeterminantRegistry.compute(player, DeterminantRegistry.getAll());
    }

    /**
     * Determines whether the temperature is freezing enough to damage unprotected players.
     */
    public boolean isAmbientFreezing() {
        return DeterminantRegistry.compute(
                player, DeterminantRegistry.getExcluded(ModDeterminants.AMBIENT_KEY)
        ) == MIN_VALUE;
    }

    /**
     * Determines whether the temperature is hot enough to set unprotected players on fire.
     */
    public boolean isAmbientBurning() {
        return compute() == MAX_VALUE;
    }

    /**
     * Determines whether this player is uncomfortably cold.
     */
    public boolean isPlayerCold() {
        return compute() + getColdResistance() < 0;
    }

    /**
     * Determines whether this player is uncomfortably hot.
     */
    public boolean isPlayerHot() {
        return compute() - getHeatResistance() > 0;
    }

    /**
     * Determines whether this player is freezing.
     */
    public boolean isPlayerFreezing() {
        return isAmbientFreezing() && !isFreezeResistant();
    }

    /**
     * Determines whether this player should be set on fire.
     */
    public boolean isPlayerBurning() {
        return isAmbientBurning() && !isBurnResistant();
    }

    /**
     * Determines whether the current temperature is an extreme temperature.
     */
    public boolean isAmbientExtreme() {
        return Math.abs(compute()) == MAX_VALUE || isPlayerConsideredBurning();
    }

    /**
     * Determines whether this temperature can set players on fire,
     * or if the player is already on fire.
     */
    public boolean isAmbientConsideredBurning() {
        return isPlayerBurning() || player.isOnFire();
    }

    /**
     * Determines whether this player can be, or is already set on fire.
     */
    public boolean isPlayerConsideredBurning() {
        return isPlayerBurning() || (player.isOnFire() && !isBurnResistant());
    }

    /**
     * Gets the lowest temperature the player can survive.
     */
    public double getColdResistance() {
        return getResistance(ModEffects.COLD_RESISTANCE_EFFECT, ModEnchantments.COLD_RESISTANCE_ENCHANTMENT);
    }

    /**
     * Gets the highest temperature the player can survive.
     */
    public double getHeatResistance() {
        return getResistance(ModEffects.HEAT_RESISTANCE_EFFECT, ModEnchantments.HEAT_RESISTANCE_ENCHANTMENT);
    }

    /**
     * Gets whether this player can survive being set on fire.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isBurnResistant() {
        return getResistance(MobEffects.FIRE_RESISTANCE, ModEnchantments.FLAME_BREAKER_ENCHANTMENT) > 1;
    }

    /**
     * Gets whether this player can survive being frozen.
     */
    public boolean isFreezeResistant() {
        return getResistance(ModEffects.ICE_BREAKER_EFFECT, ModEnchantments.ICE_BREAKER_ENCHANTMENT) > 1;
    }

    /**
     * Determines the player's resistance to a temperature with their current effects and enchantments.
     *
     * @param resistanceEffect      A MobEffect that provides resistance against this temperature.
     * @param resistanceEnchantment An enchantment that provides resistance against this temperature.
     */
    private double getResistance(MobEffect resistanceEffect, Enchantment resistanceEnchantment) {
        double result = 1;

        if (player.hasEffect(resistanceEffect))
            // noinspection DataFlowIssue: #hasEffect() solves this
            result += player.getEffect(resistanceEffect).getAmplifier() + 0.5;

        for (ItemStack armor : player.getArmorSlots())
            if (armor.getEnchantmentLevel(resistanceEnchantment) == 1)
                result += 0.5;

        return Util.Mathf.clamp(result, 0, 2);
    }
}
