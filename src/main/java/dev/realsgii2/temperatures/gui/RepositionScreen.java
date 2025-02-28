package dev.realsgii2.temperatures.gui;

import dev.realsgii2.temperatures.Config;
import dev.realsgii2.temperatures.gui.boilerplate.BaseScreen;
import dev.realsgii2.temperatures.gui.boilerplate.GuiVector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.ForgeSlider;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class RepositionScreen extends BaseScreen {
    public RepositionScreen(Component pTitle) {
        super(pTitle);
    }

    private ForgeSlider xSlider = null;
    private ForgeSlider ySlider = null;

    private int xValue = 0;
    private int yValue = 0;

    protected void init() {
        GuiVector windowSize = GuiVector.windowSize();
        GuiVector gaugeOffset = Config.Client.getGaugeOffset();

        xValue = gaugeOffset.x;
        yValue = gaugeOffset.y;

        GuiVector baseAnchorPosition = GuiVector.centre();

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                GuiVector.Anchor anchor = GuiVector.Anchor.of(x, y);

                button(String.join("",
                                Arrays.stream(anchor.name().split("_")).map(s -> String.valueOf(s.charAt(0))).toList()),
                        baseAnchorPosition.offsetRelative(x * 24 - 10, y * 24 - 10), new GuiVector(20, 20), (__) -> Config.Client.setGuiAnchorPoint(anchor));
            }
        }

        button("Finish", GuiVector.bottom().offsetRelative(-120 / 2, 40), new GuiVector(120, 20), (x) -> Minecraft.getInstance().setScreen(null));

        xSlider = slider("X", GuiVector.bottom().offsetRelative(-2 - 120, 64), new GuiVector(120, 20), 0,
                windowSize.x, xValue, 1);
        ySlider = slider("Y", GuiVector.bottom().offsetRelative(2, 64), new GuiVector(120, 20), 0, windowSize.y
                , yValue, 1);
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public void tick() {
        super.tick();

        if (xSlider.getValueInt() != xValue || ySlider.getValueInt() != yValue) {
            xValue = xSlider.getValueInt();
            yValue = ySlider.getValueInt();

            Config.Client.setGaugeOffset(
                    new GuiVector(xSlider.getValueInt(), ySlider.getValueInt())
            );
        }
    }
}
