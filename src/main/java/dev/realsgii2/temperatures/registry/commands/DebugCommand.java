package dev.realsgii2.temperatures.registry.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import dev.realsgii2.temperatures.Config;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

/**
 * Sets the client's debug option.
 */
public class DebugCommand extends BaseCommand {
    private static final RequiredArgumentBuilder<CommandSourceStack, Boolean> ENABLED_ARG
            = Commands.argument("enabled", BoolArgumentType.bool());

    public DebugCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        super(
                dispatcher,
                (base, execute) -> base.then(Commands.literal("debug").then(ENABLED_ARG.executes(execute)))
        );
    }

    @Override
    public void execute(RunContext context) {
        boolean enabled = context.command().getArgument("enabled", Boolean.class);
        Config.Client.setDebug(enabled);

        context.response().sendSuccess("Set temperatures-client.toml/debug to: " + enabled);
    }
}
