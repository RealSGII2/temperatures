package dev.realsgii2.temperatures.registry.commands;

import com.mojang.brigadier.CommandDispatcher;
import dev.realsgii2.temperatures.gui.RepositionScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

/**
 * Shows the screen to reposition the gauge.
 */
public class PositionCommand extends BaseCommand {
    public PositionCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        super(
                dispatcher,
                (base, execute) -> base.then(Commands.literal("position").executes(execute))
        );
    }

    @Override
    public void execute(RunContext context) {
        Minecraft.getInstance().setScreen(new RepositionScreen(Component.literal("Reposition Temperature Gauge")));
    }
}
