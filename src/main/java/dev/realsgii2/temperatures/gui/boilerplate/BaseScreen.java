package dev.realsgii2.temperatures.gui.boilerplate;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import net.minecraftforge.client.gui.widget.ForgeSlider;

/**
 * A boilerplate class to use {@link GuiVector}s when rendering screens.
 */
public abstract class BaseScreen extends Screen {
    protected BaseScreen(Component pTitle) {
        super(pTitle);
    }

    @SuppressWarnings("SameParameterValue")
    protected ForgeSlider slider(String label, GuiVector position, GuiVector size, double min, double max,
                                 double value, double step) {
        ForgeSlider slider = new ForgeSlider(
                position.x, position.y, size.x, size.y, Component.literal(label + ": "),
                Component.empty(), min, max, value, step, 0, true
        );
        addRenderableWidget(slider);
        return slider;
    }

    protected void button(String label, GuiVector position, GuiVector size, Button.OnPress onPress) {
        ExtendedButton button = new ExtendedButton(position.x, position.y, size.x, size.y, Component.literal(label), onPress);
        addRenderableWidget(button);
    }
}
