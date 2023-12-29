package uk.gemwire.wands.client.debug.screen;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import uk.gemwire.wands.Wands;
import uk.gemwire.wands.types.Spell;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class FocusWidget extends ObjectSelectionList<FocusWidget.FocusWidgetEntry> {

    private final Font font;

    public FocusWidget(Screen parent, int width, int height, int top, int bottom, @Nullable ResourceLocation entry) {
        this(parent, parent.getMinecraft().font, width, height, top, bottom, entry);
    }

    public FocusWidget(Screen parent, Font font, int width, int height, int top, int bottom, @Nullable ResourceLocation entry) {
        super(parent.getMinecraft(), height, top, bottom, font.lineHeight + 8);
        this.font = font;

        this.refreshList(entry);

        this.setRenderBackground(false);
        this.setRenderHeader(false, 0);
    }

    public void refreshList(@Nullable ResourceLocation selected) {
        this.clearEntries();

        Wands.WANDS.getEntries().forEach(it -> {
            FocusWidgetEntry entry = new FocusWidgetEntry(it, this);

            this.addEntry(entry);

            if (Wands.WAND_TYPE_REGISTRY.getKey(entry.getType()).equals(selected))
                setSelected(entry);
        });
    }

    @Override
    public int getScrollbarPosition() {
        return this.getX() + this.width;
    }

    @Override
    public int getRowWidth() {
        return this.width;
    }

    public class FocusWidgetEntry extends ObjectSelectionList.Entry<FocusWidgetEntry> {
        private final Spell type;
        private final FocusWidget parent;

        FocusWidgetEntry(Holder<Spell> type, FocusWidget parent) {
            this.type = type.value();
            this.parent = parent;
        }

        @Override
        public void render(GuiGraphics stack, int idx, int top, int left, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isMouseOver, float partialTicks) {
            boolean isSelected = getSelected() == this;

            if (!isSelected) {
                Tesselator tessellator = Tesselator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuilder();

                // j,   k,   j2,   k1,         j1
                // idx, top, left, entryWidth, entryHeight

                int p3 = getY() + 4 - (int)getScrollAmount();
                int i1 = p3 + idx * itemHeight + headerHeight;
                int j1 = itemHeight - 4;
                int l1 = getX() + width / 2 - entryWidth / 2;
                int i2 = getX() + width / 2 + entryWidth / 2;

                float f = parent.isFocused() ? 0.25F : 0.125F;
                //TODO: RenderSystem.color4f(f, f, f, 1.0F);
                bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
                bufferbuilder.vertex(l1, (i1 + j1 + 2), 0.0D).endVertex();
                bufferbuilder.vertex(i2, (i1 + j1 + 2), 0.0D).endVertex();
                bufferbuilder.vertex(i2, (i1 - 2), 0.0D).endVertex();
                bufferbuilder.vertex(l1, (i1 - 2), 0.0D).endVertex();
                tessellator.end();

                //TODO: RenderSystem.color4f(0.0F, 0.0F, 0.0F, 1.0F);
                bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
                bufferbuilder.vertex((l1 + 1), (i1 + j1 + 1), 0.0D).endVertex();
                bufferbuilder.vertex((i2 - 1), (i1 + j1 + 1), 0.0D).endVertex();
                bufferbuilder.vertex((i2 - 1), (i1 - 1), 0.0D).endVertex();
                bufferbuilder.vertex((l1 + 1), (i1 - 1), 0.0D).endVertex();
                tessellator.end();
            }

            String name = Wands.WAND_TYPE_REGISTRY.getKey(type).toString();
            stack.drawString(parent.font, name, left + 3, top + 2, isSelected ? 0xFFFF55 : 0xFFFFFF);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            setSelected(this);

            return false;
        }

        public Spell getType() {
            return type;
        }

        @Override
        public Component getNarration() {
            return Component.translatable("narrator.select", getType().toString());
        }
    }

}