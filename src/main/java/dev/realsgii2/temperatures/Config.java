package dev.realsgii2.temperatures;

import dev.realsgii2.temperatures.gui.boilerplate.GuiVector;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.antlr.v4.runtime.misc.Triple;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
public class Config {
    public static void registerConfig() {
        ModLoadingContext context = ModLoadingContext.get();
        context.registerConfig(ModConfig.Type.COMMON, Config.Common.SPEC);
        context.registerConfig(ModConfig.Type.SERVER, Config.Server.SPEC);
        context.registerConfig(ModConfig.Type.CLIENT, Config.Client.SPEC);
    }

    @Mod.EventBusSubscriber(modid = TemperaturesMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Common {
        private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

        private static final ForgeConfigSpec.ConfigValue<List<? extends List<?>>> BIOME_TEMPERATURES = BUILDER
                .comment("The biomes with specific temperatures.",
                        "Format: [biomeName, dayTemperature, nightTemperature][]",
                        "  - biomeNameOrTag: A biome name or tag (with namespace) to identify a biome",

                        "  - day/nightTemperature: A number between inclusively -2 and 3, determining the temperature" +
                                " " +
                                "during said time",
                        "    - -2: Coldest  (2 points of cold resistance required)",
                        "    - -1: Cold     (1 point of cold resistance required)",
                        "    -  0: Default  (no resistance required)",
                        "    -  1: Hot      (1 point of heat resistance required)",
                        "    -  2: Hottest  (2 points of heat resistance required)",
                        "    -  3: Volcanic (fire resistance required)",
                        "Example: [",
                        "  [\"minecraft:taiga\", -1, -2],",
                        "  [\"minecraft:desert\", 1, -1],",
                        "  [\"minecraft:basalt_deltas\", 3, 3],",
                        "]",
                        "Default: []")
                .defineList("biomeTemperatures", List.of(), it ->
                        it instanceof List
                                && ((List<?>) it).get(0) instanceof String
                                && ((List<?>) it).get(1) instanceof Double
                                && ((List<?>) it).get(2) instanceof Double
                );

        private static final ForgeConfigSpec.ConfigValue<List<? extends List<?>>> WARM_BLOCKS = BUILDER
                .comment("Blocks that raise the temperature when the player gets near them.",
                        "Format: [blockName, temperatureDiff][]",
                        "Default: [[\"minecraft:campfire\", 0.5], [\"minecraft:fire\", 1], [\"minecraft:lava\", 1]]")
                .defineList("warmBlocks",
                        List.of(List.of("minecraft:campfire", 0.5), List.of("minecraft:fire", 1), List.of("minecraft" +
                                ":lava", 1)),
                        it ->
                                it instanceof List
                                        && ((List<?>) it).get(0) instanceof String
                                        && (((List<?>) it).get(1) instanceof Double || ((List<?>) it).get(1) instanceof Integer)
                );

        private static final ForgeConfigSpec.DoubleValue DIFF_IN_RAIN = BUILDER
                .comment("The temperature difference when it is raining.",
                        "Default: -0.5")
                .defineInRange("diffInRain", -0.5, -2, 2);

        private static final ForgeConfigSpec.DoubleValue DIFF_IN_SNOW = BUILDER
                .comment("The temperature difference when it is snowing.",
                        "Default: -0.5")
                .defineInRange("diffInSnow", -0.5, -2, 2);


        static final ForgeConfigSpec SPEC = BUILDER.build();

        private static List<? extends Util.Pair<String, Double>> warmBlocks;

        public static double getDiffInRain() {
            return DIFF_IN_RAIN.get();
        }

        public static double getDiffInSnow() {
            return DIFF_IN_SNOW.get();
        }

        public static List<? extends Util.Pair<String, Double>> getWarmBlocks() {
            if (warmBlocks == null)
                warmBlocks =
                        WARM_BLOCKS.get().stream().map(t -> Util.Pair.of((String) t.get(0), toDouble(t.get(1)))).collect(Collectors.toList());

            return warmBlocks;
        }

        private static double toDouble(Object value) {
            if (value instanceof Double)
                return (double) value;
            else if (value instanceof String)
                return Double.parseDouble((String) value);
            else if (value instanceof Integer)
                return Double.parseDouble(value.toString());

            return Double.NaN;
        }

        public static boolean isWarmBlock(String blockName) {
            return getWarmBlocks().stream().anyMatch(pair -> pair.first.equals(blockName));
        }

        public static double getWarmth(String blockName) {
            return getWarmBlocks().stream().filter(pair -> pair.first.equals(blockName)).map(x -> x.second).findFirst().orElse(0.0);
        }

        public static List<Util.Pair<String, Double>> getAllWarmBlocks() {
            return getWarmBlocks().stream().map(t -> Util.Pair.of(t.first, t.second)).collect(Collectors.toList());
        }

        public static List<? extends Triple<String, Double, Double>> getBiomeTemperatures() {
            return BIOME_TEMPERATURES.get().stream().map(t -> new Triple<>((String) t.get(0),
                    (Double) t.get(1), (Double) t.get(2))).collect(Collectors.toList());
        }

        public static BiomeData getBiome(String biomeName) {
            Optional<? extends Triple<String, Double, Double>> rawInfo =
                    getBiomeTemperatures().stream().filter(x -> x.a.equals(biomeName)).findFirst();
            return rawInfo.map(BiomeData::of).orElse(null);
        }

        public static BiomeData getBiome(Level level, Biome biome) {
            ResourceLocation biomeId = Util.getBiomeId(level, biome);
            if (biomeId == null) return null;

            return getBiome(biomeId.toString());
        }

        public static List<BiomeData> getAllBiomes() {
            return getBiomeTemperatures().stream().map(BiomeData::of).collect(Collectors.toList());
        }

        public record BiomeData(String id, double dayTemperature, double nightTemperature) {
            public static BiomeData of(Triple<String, Double, Double> rawBiomeInfo) {
                return new BiomeData(rawBiomeInfo.a, rawBiomeInfo.b, rawBiomeInfo.c);
            }
        }
    }

    public static class Server {
        private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

        private static final ForgeConfigSpec.IntValue NORMAL_DAMAGE_TICK_MOD = BUILDER
                .comment("Damage the player every X ticks if they have an uncomfortable temperature.",
                         "Default: 60")
                .defineInRange("normalDamageTick", 60, 1, Integer.MAX_VALUE);

        private static final ForgeConfigSpec.IntValue EXTREME_DAMAGE_TICK_MOD = BUILDER
                .comment("Damage the player every X ticks if they are in extreme conditions.",
                        "Default: 5")
                .defineInRange("extremeDamageTick", 5, 1, Integer.MAX_VALUE);

        private static final ForgeConfigSpec.IntValue NORMAL_DAMAGE = BUILDER
                .comment("Damage the player this much if they have an uncomfortable temperature.",
                        "Default: 2")
                .defineInRange("normalDamageAmount", 2, 1, Integer.MAX_VALUE);

        private static final ForgeConfigSpec.IntValue EXTREME_DAMAGE = BUILDER
                .comment("Damage the player this much if they are in extreme conditions.",
                        "Default: 4")
                .defineInRange("extremeDamageAmount", 4, 1, Integer.MAX_VALUE);

        private static final ForgeConfigSpec.IntValue BURN_TICKS = BUILDER
                .comment("When in extreme heat, set the player on fire for this many ticks.",
                        "Default: 80")
                .defineInRange("burnTicks", 80, 1, Integer.MAX_VALUE);

        public static int getNormalDamageTick() {
            return NORMAL_DAMAGE_TICK_MOD.get();
        }

        public static int getExtremeDamageTick() {
            return EXTREME_DAMAGE_TICK_MOD.get();
        }

        public static int getNormalDamageAmount() {
            return NORMAL_DAMAGE.get();
        }

        public static int getExtremeDamageAmount() {
            return EXTREME_DAMAGE.get();
        }

        public static int getBurnTicks() {
            return BURN_TICKS.get();
        }

        public static final ForgeConfigSpec SPEC = BUILDER.build();
    }

    public static class Client {
        private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

        private static final ForgeConfigSpec.BooleanValue DEBUG = BUILDER
                .comment("Shows internal values and calculations.",
                        "Default: false")
                .define("debug", false);

        private static final ForgeConfigSpec.BooleanValue MUTE_CONFIG_WARNINGS = BUILDER
                .comment("Mute warning emitted when the configuration could be improved.",
                        "Default: false")
                .define("muteConfigWarnings", false);

        private static final ForgeConfigSpec.EnumValue<GuiVector.Anchor> GUI_ANCHOR_POINT = BUILDER
                .push("gui")
                .comment(
                        "The position on screen to align the GUI.",
                        "Default: BOTTOM_LEFT"
                ).defineEnum("anchorPoint", GuiVector.Anchor.BOTTOM_LEFT);

        private static final ForgeConfigSpec.IntValue GUI_OFFSET_X = BUILDER
                .comment(
                        "The X offset of the GUI, properly mirrored according to alignment.",
                        "Default: 20"
                ).defineInRange("offsetX", 20, 0, Integer.MAX_VALUE);

        private static final ForgeConfigSpec.IntValue GUI_OFFSET_Y = BUILDER
                .comment(
                        "The Y offset of the GUI, properly mirrored according to alignment.",
                        "Default: 20"
                ).defineInRange("offsetY", 20, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec SPEC = BUILDER.build();

        public static void setGuiAnchorPoint(GuiVector.Anchor anchor) {
            GUI_ANCHOR_POINT.set(anchor);
        }

        public static GuiVector getGaugeOffset() {
            return new GuiVector(GUI_OFFSET_X.get(), GUI_OFFSET_Y.get());
        }

        public static void setGaugeOffset(GuiVector offset) {
            GUI_OFFSET_X.set(offset.x);
            GUI_OFFSET_Y.set(offset.y);
        }

        public static GuiVector getGaugePosition() {
            return GuiVector.ofAnchor(GUI_ANCHOR_POINT.get()).accountSize(20).offsetRelative(GUI_OFFSET_X.get(),
                    GUI_OFFSET_Y.get());
        }

        public static boolean debug() {
            return DEBUG.get();
        }

        public static void setDebug(boolean value) {
            DEBUG.set(value);
        }

        public static boolean showConfigWarning() {
            return !MUTE_CONFIG_WARNINGS.get();
        }

        public static void muteConfigWarnings(boolean value) {
            MUTE_CONFIG_WARNINGS.set(value);
        }
    }
}
