package dev.realsgii2.temperatures.registry.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.realsgii2.temperatures.boilerplate.ChatUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiFunction;

/**
 * Boilerplate to make it easier to make commands.
 */
public abstract class BaseCommand {
    public BaseCommand(CommandDispatcher<CommandSourceStack> dispatcher,
                       BiFunction<LiteralArgumentBuilder<CommandSourceStack>, Command<CommandSourceStack>,
                               LiteralArgumentBuilder<CommandSourceStack>> builder) {
        dispatcher.register(builder.apply(Commands.literal("temperatures"), this::execute));
    }

    @SuppressWarnings("SameReturnValue")
    private int execute(CommandContext<CommandSourceStack> command) {
        if (command.getSource().getEntity() instanceof Player player) {
            execute(new RunContext(command, new ChatUtil(player)));
        }

        return Command.SINGLE_SUCCESS;
    }

    public abstract void execute(RunContext context);

    public record RunContext(CommandContext<CommandSourceStack> command, ChatUtil response) {
        public Player player() {
            return (Player) command.getSource().getEntity();
        }
    }
}
