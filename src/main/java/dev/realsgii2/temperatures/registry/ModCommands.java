package dev.realsgii2.temperatures.registry;

import com.mojang.brigadier.CommandDispatcher;
import dev.realsgii2.temperatures.registry.commands.DebugCommand;
import dev.realsgii2.temperatures.registry.commands.FillConfigCommand;
import dev.realsgii2.temperatures.registry.commands.MuteConfigWarningCommand;
import dev.realsgii2.temperatures.registry.commands.PositionCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ModCommands {
    @SubscribeEvent
    public static void register(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        new FillConfigCommand(dispatcher);
        new DebugCommand(dispatcher);
        new PositionCommand(dispatcher);
        new MuteConfigWarningCommand(dispatcher);
    }
}
