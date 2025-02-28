package dev.realsgii2.temperatures.boilerplate;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.player.Player;

/**
 * A boilerplate class to make it easy to send chat messages to a player.
 * @param player The player to send messages to.
 */
public record ChatUtil(Player player) {
    /**
     * Send an info message to the player.
     * @param text The string or {@link Component} to send.
     */
    public void sendInfo(Object text) {
        player.sendSystemMessage(
                keyword("info", Styles.BLUE)
                        .append(resolveComponent(text))
        );
    }

    /**
     * Send a success message to the player.
     * @param text The string or {@link Component} to send.
     */
    public void sendSuccess(Object text) {
        player.sendSystemMessage(
                keyword("success", Styles.GREEN)
                        .append(resolveComponent(text))
        );
    }

    /**
     * Send a warning message to the player.
     * @param text The string or {@link Component} to send.
     */
    public void sendWarning(Object text) {
        player.sendSystemMessage(
                keyword("warning", Styles.YELLOW)
                        .append(resolveComponent(text))
        );
    }

    /**
     * Send an error message to the player.
     * @param text The string or {@link Component} to send.
     */
    public void sendError(Object text) {
        player.sendSystemMessage(
                keyword("error", Styles.RED)
                        .append(resolveComponent(text))
        );
    }

    /**
     * Creates a plain, white span of text.
     * @param text The text to use.
     * @return A Component with no style.
     */
    public MutableComponent text(Object text) {
        return resolveComponent(text).withStyle(Styles.DEFAULT);
    }

    /**
     * Creates the text used by the tooltip shown if a link runs a command.
     * @param command The command that is being run.
     * @return A Component consumed by tooltips.
     */
    public MutableComponent commandTooltip(Object command) {
        return Component.literal("Run ").withStyle(Styles.DEFAULT)
                .append(resolveComponent(command).withStyle(ChatFormatting.AQUA));
    }

    /**
     * Creates a clickable link.
     * @param text The text of this link.
     * @param toolTip The tooltip of this link.
     * @param onClick The action to do when this link is clicked.
     * @return A clickable and styled Component representing a link.
     */
    public MutableComponent link(Object text, Object toolTip, ClickEvent onClick) {
        return resolveComponent(text)
                .withStyle(
                        Styles.LINK
                                .withClickEvent(onClick)
                                .withHoverEvent(
                                        new HoverEvent(HoverEvent.Action.SHOW_TEXT, resolveComponent(toolTip))
                                )
                );
    }

    /**
     * An alternative link style, used as options for messages.
     * @param text The text of this option.
     * @param toolTip The tooltip of this option.
     * @param onClick The action to do when this link is clicked.
     * @return A clickable and styled Component representing a link styled as an option.
     */
    public MutableComponent option(Object text, Object toolTip, ClickEvent onClick) {
        return resolveComponent("[" + text.toString() + "]")
                .withStyle(
                        Styles.OPTION
                                .withClickEvent(onClick)
                                .withHoverEvent(
                                        new HoverEvent(HoverEvent.Action.SHOW_TEXT, resolveComponent(toolTip))
                                )
                );
    }

    /**
     * A special link that suggests a command in the chat bar.
     * @param text The text of this link.
     * @param command The command to paste.
     * @return A clickable and styled Component.
     */
    public MutableComponent commandSuggestionLink(Object text, String command) {
        return link(text, commandTooltip(command), new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
    }

    /**
     * A special option that suggests a command in the chat bar.
     * @param text The text of this option.
     * @param command The command to paste.
     * @return A clickable and styled Component.
     */
    public MutableComponent commandOption(Object text, String command) {
        return option(text, commandTooltip(command), new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
    }

    /**
     * Turns any object into a Component.
     * @param object The object to turn into a Component.
     * @return The Component representation of this object.
     */
    public MutableComponent resolveComponent(Object object) {
        if (object instanceof MutableComponent component) return component;
        if (object instanceof String string) return Component.literal(string).withStyle(Styles.DEFAULT);
        return Component.literal(object.toString()).withStyle(Styles.DEFAULT);
    }

    /**
     * Creates a styled key word.
     * @param text The text of this key word.
     * @param style The style of this key word.
     * @return A Component that will not affect the style of other Components.
     */
    public MutableComponent keyword(String text, Style style) {
        return Component.literal(text).withStyle(style).append(Component.literal(" ").withStyle(Styles.DEFAULT));
    }

    /**
     * Predefined, custom styles used for this mod.
     */
    public static class Styles {
        /** Plain, white text. */
        public static final Style DEFAULT = Style.EMPTY.withColor(ChatFormatting.WHITE);

        public static final Style RED = Style.EMPTY.withColor(ChatFormatting.RED);
        public static final Style GREEN = Style.EMPTY.withColor(ChatFormatting.GREEN);
        public static final Style BLUE = Style.EMPTY.withColor(ChatFormatting.BLUE);
        public static final Style YELLOW = Style.EMPTY.withColor(ChatFormatting.YELLOW);

        /** Aqua text with an underline. */
        public static final Style LINK = Style.EMPTY.withColor(ChatFormatting.AQUA).withUnderlined(true);
        /** Gray text. */
        public static final Style OPTION = Style.EMPTY.withColor(ChatFormatting.DARK_GRAY);
    }
}
