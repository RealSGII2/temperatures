package dev.realsgii2.temperatures;

import com.ibm.icu.impl.Pair;
import dev.realsgii2.temperatures.boilerplate.ChatUtil;
import dev.realsgii2.temperatures.gui.TemperatureGaugeOverlay;
import dev.realsgii2.temperatures.handler.Temperature;
import dev.realsgii2.temperatures.registry.ModEnchantments;
import dev.realsgii2.temperatures.registry.ModPotions;
import dev.realsgii2.temperatures.registry.ModRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import org.antlr.v4.runtime.misc.Triple;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Mod(TemperaturesMod.MOD_ID)
public class TemperaturesMod {
    public static final String MOD_ID = "temperatures";

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public TemperaturesMod() {
        Config.registerConfig();

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModRegistry.register(bus);

        CREATIVE_MODE_TABS.register(
                "temperatures", () -> CreativeModeTab.builder()
                        .withTabsBefore(CreativeModeTabs.COMBAT)
                        .icon(() -> new ItemStack(Blocks.ICE))
                        .title(Component.translatable("itemGroup.temperatures"))
                        .displayItems((parameters, output) -> output.acceptAll(
                                ModPotions.getRegisteredObjects()
                                        .map(Items.POTION, PotionUtils::setPotion)
                                        .with(
                                                ModEnchantments.getRegisteredObjects()
                                                        .map(Items.ENCHANTED_BOOK, Util::enchant)
                                        )
                        )).build());

        CREATIVE_MODE_TABS.register(bus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void registerGUI(RegisterGuiOverlaysEvent event) {
            event.registerAboveAll("temperatures_gauge_gui", new TemperatureGaugeOverlay());
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID)
    public static class ModEvents {
        /**
         * Handles informing players of mismatched config.
         */
        @SubscribeEvent
        public static void playerJoined(PlayerEvent.PlayerLoggedInEvent event) {
            Player player = event.getEntity();
            ChatUtil chat = new ChatUtil(player);

            if (!Config.Client.showConfigWarning()) return;

            if (Minecraft.getInstance().isSingleplayer() || player.hasPermissions(2)) {
                String warningMessage = null;
                String fixMessage = null;

                Optional<Registry<Biome>> biomeRegistryHolder =
                        player.level().registryAccess().registry(Registries.BIOME);
                if (biomeRegistryHolder.isEmpty()) {
                    chat.sendError("loading biomes");
                    return;
                }

                if (Config.Common.getAllBiomes().isEmpty()) {
                    warningMessage = "There doesn't seem to be any configured biomes.";
                    fixMessage = "prefill the configuration file";
                }

                Registry<Biome> biomeRegistry = biomeRegistryHolder.get();
                List<? extends Triple<String, Double, Double>> biomes = Config.Common.getBiomeTemperatures();
                if (
                        biomeRegistry.stream().anyMatch(
                                x -> biomes.stream().noneMatch(
                                        y -> y.a.equals(
                                                Objects.requireNonNull(
                                                        biomeRegistry.getKey(x)).toString()
                                        )
                                )
                        )
                ) {
                    warningMessage = "The configuration doesn't specify all biomes.";
                    fixMessage = "fill in missing biomes";
                }

                if (warningMessage == null) {
                    chat.sendError("no warnings");
                    return;
                }

                chat.sendWarning("Temperatures configuration warning:");
                chat.sendWarning(warningMessage);
                chat.sendWarning(
                        chat.text("Do you want to ")
                                .append(
                                        chat.commandSuggestionLink(
                                                fixMessage,
                                                "/temperatures fill"
                                        )
                                ).append("?"));
                chat.sendWarning(
                        chat.commandOption("Mute this warning", "/temperatures mute-config-warning true")
                );
            }
        }

        /**
         * Damages players that are in uncomfortable or extreme temperatures.
         */
        @SubscribeEvent
        public static void damageUncomfortablePlayers(TickEvent.PlayerTickEvent event) {
            if (event.phase == TickEvent.Phase.START && event.side.isServer()) {
                Player player = event.player;
                Temperature temperature = new Temperature(player);

                if (!player.isCreative() && !player.isSpectator()) {
                    Pair<DamageSource, Integer> possibleDamage = temperature.getPossibleDamage(event);

                    if (possibleDamage != null)
                        player.hurt(possibleDamage.first, possibleDamage.second);

                    if (temperature.isPlayerBurning())
                        player.setRemainingFireTicks(Config.Server.getBurnTicks());
                }
            }
        }
    }
}
