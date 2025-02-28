package dev.realsgii2.temperatures.gui;

import com.ibm.icu.impl.Pair;
import dev.realsgii2.temperatures.TemperaturesMod;
import dev.realsgii2.temperatures.handler.Temperature;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

/**
 * Reference class storing all the possible textures used by the Temperature Gauge.
 */
public class TemperatureGaugeAssets {
    /**
     * The ice cube texture used when the in freezing temperatures.
     */
    public static final ResourceLocation ICE_CUBE = new ResourceLocation(TemperaturesMod.MOD_ID, "textures/gui/gauge/ice_cube.png");

    /**
     * Gets the background of the gauge, displaying what temperatures are safe.
     * @param temperature The Temperature to pull protection values from.
     * @return The texture of the background used by the temperature object.
     */
    public static ResourceLocation getBackground(Temperature temperature) {
        if (temperature.isAmbientFreezing()) return ExtremeBackgroundTexture.TOO_COLD.texture;
        if (temperature.isAmbientConsideredBurning() || temperature.isAmbientBurning()) return ExtremeBackgroundTexture.TOO_HOT.texture;

        return GaugeBackgroundTextures.get(Pair.of(temperature.getColdResistance(), temperature.getHeatResistance()));
    }

    /**
     * Gets the rotator of the gauge, displaying whether values are extreme.
     * @param temperature The Temperature to pull data from.
     * @return The texture of the dial of the gauge.
     */
    public static ResourceLocation getRotator(Temperature temperature) {
        if (temperature.isAmbientFreezing()) return RotatorTexture.ICY.texture;
        else if (temperature.isAmbientConsideredBurning() || temperature.isAmbientBurning()) return RotatorTexture.VOLCANIC.texture;

        return RotatorTexture.NORMAL.texture;
    }

    /**
     * Gets the texture of a ring indicating bad conditions.
     * Null returned if there is no bad condition.
     * @param temperature The Temperature to pull data from.
     * @return The texture of the warning ring to use, if any.
     */
    public static ResourceLocation getWarningRing(Temperature temperature) {
        if (temperature.isPlayerCold()) return WarningRingTexture.COLD.texture;
        else if (temperature.isPlayerHot()) return WarningRingTexture.HOT.texture;

        return null;
    }

    /**
     * Combines all the textures needed into one data class.
     * @param temperature The Temperature to pull data from.
     * @return A container of all the textures this Temperature represents.
     */
    public static ImageMap getImageMap(Temperature temperature) {
        return new ImageMap(getBackground(temperature), getRotator(temperature), getWarningRing(temperature));
    }

    /**
     * The possible warning ring textures.
     */
    public enum WarningRingTexture {
        COLD("textures/gui/gauge/cold_ring.png"), HOT("textures/gui/gauge/heat_ring.png");

        /**
         * The texture of this WarningRing.
         */
        public final ResourceLocation texture;

        WarningRingTexture(String path) {
            this.texture = new ResourceLocation(TemperaturesMod.MOD_ID, path);
        }
    }

    /**
     * The possible dial textures.
     */
    public enum RotatorTexture {
        NORMAL("textures/gui/gauge/rotator_normal.png"), VOLCANIC("textures/gui/gauge/rotator_volcanic.png"), ICY("textures/gui/gauge/rotator_icy.png");

        /**
         * The texture of this Rotator.
         */
        public final ResourceLocation texture;

        RotatorTexture(String path) {
            this.texture = new ResourceLocation(TemperaturesMod.MOD_ID, path);
        }
    }

    /**
     * Represents backgrounds used for extreme temperatures.
     */
    public enum ExtremeBackgroundTexture {
        TOO_HOT("textures/gui/gauge/bg/too_hot.png"), TOO_COLD("textures/gui/gauge/bg/too_cold.png");

        /**
         * The texture of this ExtremeBackground.
         */
        public final ResourceLocation texture;

        ExtremeBackgroundTexture(String path) {
            this.texture = new ResourceLocation(TemperaturesMod.MOD_ID, path);
        }
    }

    /**
     * A map of [coldProtection, heatProtection] to the background representing those protection levels.
     */
    public static final Map<Pair<Double, Double>, ResourceLocation> GaugeBackgroundTextures = new HashMap<>() {
        {
            final List<Double> map = List.of(1.0, 1.5, 2.0);

            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    put(Pair.of(map.get(x), map.get(y)), new ResourceLocation(TemperaturesMod.MOD_ID, "textures/gui/gauge/bg/cold" + x + "_hot" + y + ".png"));
                }
            }
        }
    };

    /**
     * The list of sprites used by the flame.
     * TODO: Consider making an actual sprite
     */
    public static final List<ResourceLocation> FlameSprites = new ArrayList<>() {{
        for (int i = 1; i < 30; i++)
            add(new ResourceLocation(TemperaturesMod.MOD_ID, "textures/gui/gauge/flame/" + i + ".png"));
    }};

    /**
     * A data class containing all the textures needed by the TemperatureGauge.
     * @param background The background of the gauge.
     * @param rotator The spindle of the gauge.
     * @param warningRing The warning image, if needed.
     */
    public record ImageMap(ResourceLocation background, ResourceLocation rotator,
                           ResourceLocation warningRing) { }
}
