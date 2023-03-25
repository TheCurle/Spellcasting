package uk.gemwire.wands.item;

import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import uk.gemwire.wands.Capabilities;
import uk.gemwire.wands.Wands;
import uk.gemwire.wands.client.debug.WandDebug;
import uk.gemwire.wands.types.WandType;

public class WandItem extends Item {
    public WandItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level p_41432_, Player p_41433_, InteractionHand p_41434_) {
        if(p_41432_.isClientSide) WandDebug.openConfigScreen();

        // If we're on the client and we're sneaking, open the configuration menu.
        if(!(p_41433_ == null) && !p_41433_.isCrouching()) {
            // If we're just right clicking a block, trigger an immediate explosion with the configured type.
            p_41433_.getItemInHand(p_41434_).getCapability(Capabilities.WAND_FOCUS_CAPABILITY).ifPresent(cap -> {
                WandType type = Wands.WAND_TYPE_REGISTRY.get().getValue(cap.getType());

                type.performMagic(p_41433_, p_41433_.getItemInHand(p_41434_));
            });
        }

        return InteractionResultHolder.pass(p_41433_.getItemInHand(p_41434_));
    }


    /**
     * Override the display name to contain the name of the chosen explosion.
     * This is finnicky, and we deal with null pointers in bootstrap, so this is a little long winded.
     */
    @Override
    public Component getName(ItemStack stack) {
        ResourceLocation type = stack.getCapability(Capabilities.WAND_FOCUS_CAPABILITY).orElseThrow(RuntimeException::new).getType();

        Component stackConfigComponent = Component.literal(Wands.WAND_TYPE_REGISTRY.get().getValue(type).getName());
        return Component.translatable(this.getDescriptionId(stack)).append(Component.literal(" - ")).append(stackConfigComponent);
    }
}
