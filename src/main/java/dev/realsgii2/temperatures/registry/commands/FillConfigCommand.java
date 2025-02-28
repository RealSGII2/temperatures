package dev.realsgii2.temperatures.registry.commands;

import com.mojang.brigadier.CommandDispatcher;
import dev.realsgii2.temperatures.boilerplate.ChatUtil;
import dev.realsgii2.temperatures.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Fills missing configuration biomes.
 */
public class FillConfigCommand extends BaseCommand {
    public FillConfigCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        super(
                dispatcher,
                (base, execute) -> base.then(Commands.literal("fill").executes(execute))
        );
    }

    @Override
    public void execute(RunContext context) {
        Player player = context.player();
        ChatUtil response = context.response();
        response.sendInfo("Filling in missing biomes...");

        Optional<Registry<Biome>> biomeRegistryOpt = player.level().registryAccess().registry(Registries.BIOME);
        if (biomeRegistryOpt.isPresent()) {
            Registry<Biome> biomeRegistry = biomeRegistryOpt.get();

            List<String> biomes = new java.util.ArrayList<>(Config.Common.getAllBiomes()
                    .stream()
                    .map(x ->
                            String.format(
                                    "\t[\"%1$s\", %2$s, %3$s]",
                                    x.id(),
                                    x.dayTemperature(),
                                    x.nightTemperature())
                    ).toList());

            List<String> emptyBiomes = biomeRegistry
                    .stream()
                    .map(biomeRegistry::getKey)
                    .filter(Objects::nonNull)
                    .map(ResourceLocation::toString)
                    .map(x -> "\t[\"" + x + "\", 0.0, 0.0]")
                    .toList();

            biomes.addAll(emptyBiomes);
            biomes.sort(String.CASE_INSENSITIVE_ORDER);

            List<String> warmBlocks = Config.Common.getAllWarmBlocks()
                    .stream()
                    .map(x -> String.format("\t[\"%1$s\", %2$s]", x.first, x.second))
                    .toList();

            String config = String.format(
                    """
                            #The biomes with specific temperatures.
                            #Format: [biomeNameOrTag, dayTemperature, nightTemperature][]
                            #  - biomeNameOrTag: A biome name or tag (with namespace) to identify a biome
                            #  - day/nightTemperature: A number between inclusively -2 and 3, determining the temperature during said time
                            #    - -2: Coldest  (2 points of cold resistance required)
                            #    - -1: Cold     (1 point of cold resistance required)
                            #    -  0: Default  (no resistance required)
                            #    -  1: Hot      (1 point of heat resistance required)
                            #    -  2: Hottest  (2 points of heat resistance required)
                            #    -  3: Volcanic (fire resistance required)
                            #Example: [
                            #  ["minecraft:taiga", -1, -2],
                            #  ["minecraft:desert", 1, -1],
                            #  ["minecraft:basalt_deltas", 3, 3],
                            #]
                            #Default: []
                            biomeTemperatures = [
                                %1$s
                            ]
                            
                            #Blocks that raise the temperature when the player gets near them.
                            #Format: [blockName, temperatureDiff][]
                            #Default: [["minecraft:campfire", 0.5], ["minecraft:fire", 1], ["minecraft:lava", 1]]
                            warmBlocks = [
                                %2$s
                            ]
                            
                            #The temperature difference when it is raining.
                            #Default: -0.5
                            #Range: -2.0 ~ 2.0
                            diffInRain = %3$s
                            
                            #The temperature difference when it is snowing.
                            #Default: -0.5
                            #Range: -2.0 ~ 2.0
                            diffInSnow = %4$s
                            """,
                    String.join(",\n", biomes),
                    String.join(", \n", warmBlocks),
                    Config.Common.getDiffInRain(),
                    Config.Common.getDiffInSnow()
            );

            Path path = Minecraft.getInstance().gameDirectory.toPath().resolve("config").resolve("temperatures" +
                    "-common.toml");

            try {
                try (FileWriter writer = new FileWriter(path.toAbsolutePath().toString())) {
                    writer.write(config);
                }

                response.sendSuccess("Successfully wrote temperatures-common.toml");
                response.sendSuccess("Find it in the config folder");
            } catch (IOException e) {
                response.sendError("Failed to write to template file");
            }
        } else response.sendError("Getting biome registry failed");
    }
}
