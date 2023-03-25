package uk.gemwire.wands.client.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import uk.gemwire.wands.client.debug.screen.FocusConfigScreen;

public class WandDebug {
    public static void openConfigScreen() {
        if (Screen.hasShiftDown())
            Minecraft.getInstance().setScreen(new FocusConfigScreen());
    }
}
