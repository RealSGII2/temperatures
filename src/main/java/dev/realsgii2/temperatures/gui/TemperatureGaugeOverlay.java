package dev.realsgii2.temperatures.gui;

import com.ibm.icu.impl.Pair;
import dev.realsgii2.temperatures.Config;
import dev.realsgii2.temperatures.Util;
import dev.realsgii2.temperatures.gui.boilerplate.GraphicsRenderer;
import dev.realsgii2.temperatures.gui.boilerplate.GuiVector;
import dev.realsgii2.temperatures.handler.Temperature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class TemperatureGaugeOverlay implements IGuiOverlay {
    private static final int MAX_RING_TICK = 50;
    private static final double BASE_RING_SIZE = 1.25;

    private static float currentRotation = 0.0f;
    private static int ringTick = -1;

    private static double flameIndex = 0;

    public void render(ForgeGui forgeGui, GuiGraphics guiGraphics, float deltaTime, int i, int i1) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        render(new GraphicsRenderer(guiGraphics, deltaTime));
    }

    /**
     * Debug information, rendered if Config.Client.debug is true.
     */
    public static void renderDebug(GraphicsRenderer renderer, Temperature temperature) {
        assert Minecraft.getInstance().player != null;

        renderer.debug("Rain level: " + Minecraft.getInstance().player.level().rainLevel);
        renderer.debug("Temperature (non-ambient): " + temperature.getCurrentTemperatureWithoutAmbient());
        renderer.debug("Temperature (ambient: " + temperature.getCurrentTemperature());
        renderer.debug("Weights:");

        for (Pair<Biome, Double> biome : Util.World.getNearbyWeightedBiomes(Minecraft.getInstance().player)) {
            renderer.debug("  - " + Util.getBiomeId(biome.first) + "("  + biome.second + ")");
        }
    }

    public static void render(GraphicsRenderer renderer) {
        Temperature temperature = new Temperature();

        if (Config.Client.debug())
            renderDebug(renderer, temperature);

        TemperatureGaugeAssets.ImageMap textures = TemperatureGaugeAssets.getImageMap(temperature);
        double currentTemperature = temperature.getCurrentTemperature();

        GuiVector position = Config.Client.getGaugePosition();

        float goalRotation = (float) (currentTemperature * 60.0F);
        float remainingRotation = goalRotation - currentRotation;
        float newRotation = currentRotation + remainingRotation * 0.1f * renderer.deltaTime();

        if (temperature.isAmbientFreezing())
            newRotation = -130.0f;

        if (temperature.isAmbientBurning() || temperature.isAmbientConsideredBurning())
            newRotation = 130.0f;

        float finalNewRotation = newRotation;
        renderer.useAlphaShader(() -> {
            // Main Body
            renderer.draw(textures.background(), position, 20);
            renderer.transform().rotate(finalNewRotation).draw(textures.rotator(), position, 20);

            // Warning Ring
            ResourceLocation warningRing = textures.warningRing();
            if (warningRing != null && !temperature.isAmbientExtreme()) {
                ringTick++;

                float alpha = Util.Mathf.lerp(1.0f, 0.0f, Math.min((float) (ringTick - 2) / 25f + 2 / 25f, 1));

                renderer.transform()
                        .scale(BASE_RING_SIZE)
                        .draw(warningRing, position, 20);

                renderer.alpha(alpha, () ->
                        renderer.transform()
                                .scale(BASE_RING_SIZE + (double) ringTick / 40 * 0.75)
                                .draw(warningRing, position, 20)
                );

                if (ringTick >= MAX_RING_TICK) ringTick = 0;
            } else ringTick = -1;

            // Ice cube
            if (temperature.isPlayerFreezing())
                renderer.draw(TemperatureGaugeAssets.ICE_CUBE, position.offsetAbsolute(-4, -4), 26);

            // Fire
            if (temperature.isPlayerConsideredBurning()) {
                renderer.draw(TemperatureGaugeAssets.FlameSprites.get((int) Math.floor(flameIndex)), position.offsetAbsolute(-7, -10), 34);

                flameIndex += 0.5;
                if (flameIndex >= TemperatureGaugeAssets.FlameSprites.size()) flameIndex = 0;
            }
        });

        currentRotation = newRotation;
    }
}
