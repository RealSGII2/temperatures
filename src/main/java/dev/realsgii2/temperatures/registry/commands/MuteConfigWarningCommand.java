package dev.realsgii2.temperatures.registry.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import dev.realsgii2.temperatures.Config;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

/**
 * Toggles showing the player configuration improvement warnings.
 */
public class MuteConfigWarningCommand extends BaseCommand {
    private static final RequiredArgumentBuilder<CommandSourceStack, Boolean> ENABLED_ARG
            = Commands.argument("enabled", BoolArgumentType.bool());

    public MuteConfigWarningCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        super(
                dispatcher,
                (base, execute) -> base.then(Commands.literal("mute-config-warning").then(ENABLED_ARG.executes(execute)))
        );
    }

    @Override
    public void execute(RunContext context) {
        boolean enabled = context.command().getArgument("enabled", Boolean.class);
        Config.Client.muteConfigWarnings(enabled);

        context.response().sendSuccess("Set temperatures-client.toml/muteConfigWarning to: " + enabled);

        if (enabled)
            context.response().sendSuccess("You won't see configuration warnings anymore.");
        else
            context.response().sendSuccess("You'll see configuration warnings again.");
    }
}
