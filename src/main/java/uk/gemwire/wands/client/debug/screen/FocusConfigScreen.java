package uk.gemwire.wands.client.debug.screen;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import uk.gemwire.wands.Wands;
import uk.gemwire.wands.network.Network;

public class FocusConfigScreen extends Screen {
    private static final int SCREEN_WIDTH = 176;
    private static final int SCREEN_HEIGHT = 166;

    private static final ResourceLocation BACKGROUND = new ResourceLocation(Wands.MODID, "textures/gui/wand_config.png");

    private int guiLeft;
    private int guiTop;

    private FocusWidget list;

    public FocusConfigScreen() {
        super(Component.translatable("wands.title.focusconfig"));
    }

    @Override
    protected void init() {
        // Due to the way the Screen / Gui System is designed we can safely assume that `minecraft` is non null here
        // The player is another story, we can assume in all normal cases it would be, the only time this can possible fail is
        // if this screen gets opened without a world.
        assert minecraft != null;
        assert minecraft.player != null;

        this.guiLeft = (this.width - SCREEN_WIDTH) / 2;
        this.guiTop = (this.height - SCREEN_HEIGHT) / 2;

        ItemStack heldItem = minecraft.player.getMainHandItem();
        ResourceLocation selected = heldItem.getData(Wands.WAND_SPELL);

        int width = SCREEN_WIDTH - 10 - 5;
        int height = SCREEN_HEIGHT - 10;

        list = new FocusWidget(this, width, height, guiTop + 5, guiTop + height, selected);
        list.setX(guiLeft + 5);
        addWidget(list);
    }

    @Override
    public void render(GuiGraphics stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);

        stack.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);

        RenderSystem.setShaderTexture(0, Screen.BACKGROUND_LOCATION);
        double scale = minecraft.getWindow().getGuiScale();
        int posY   = guiTop + 5;
        int height = SCREEN_HEIGHT - 10;
        RenderSystem.enableScissor((int) (guiLeft * scale), (int) (posY * scale), (int) (SCREEN_WIDTH * scale), (int) (height * scale));
        list.render(stack, mouseX, mouseY, partialTicks);
        RenderSystem.disableScissor();

    }

    @Override
    public void onClose() {
        FocusWidget.FocusWidgetEntry entry = list.getSelected();
        if (entry != null)
            config(Wands.WAND_TYPE_REGISTRY.getKey(entry.getType()));

        super.onClose();
    }

    private void config(ResourceLocation registryName) {
        // Due to the way the Screen / Gui System is designed we can safely assume that `minecraft` is non null here
        // The player is another story, we can assume in all normal cases it would be, the only time this can possible fail is
        // if this screen gets opened without a world.
        assert minecraft != null;
        assert minecraft.player != null;

        minecraft.player.getMainHandItem().setData(Wands.WAND_SPELL, registryName);
        Network.sendToServer(new Network.FocusChangedPacket(registryName));
    }
}