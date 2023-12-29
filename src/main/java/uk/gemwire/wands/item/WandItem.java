package uk.gemwire.wands.item;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import uk.gemwire.wands.Wands;
import uk.gemwire.wands.client.debug.WandDebug;
import uk.gemwire.wands.types.Spell;

public class WandItem extends Item {
    public WandItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level p_41432_, Player p_41433_, InteractionHand p_41434_) {
        if(p_41432_.isClientSide) WandDebug.openConfigScreen();

        // Projectile spells
        if(!p_41433_.isCrouching()) {
            Spell type = Wands.WAND_TYPE_REGISTRY.get(p_41433_.getItemInHand(p_41434_).getData(Wands.WAND_SPELL));
            if (type.getSpell() == Spell.SpellType.PROJECTILE) {

            }
        }

        return InteractionResultHolder.pass(p_41433_.getItemInHand(p_41434_));
    }

    @Override
    public InteractionResult useOn(UseOnContext p_41427_) {

        // Instant spells
        if(!p_41427_.getPlayer().isCrouching()) {
            Spell type = Wands.WAND_TYPE_REGISTRY.get(p_41427_.getItemInHand().getData(Wands.WAND_SPELL));
            if (type.getSpell() == Spell.SpellType.PROJECTILE) {
                type.performMagic(p_41427_.getPlayer(), p_41427_.getItemInHand());
            }
        }

        return InteractionResult.SUCCESS;
    }

    /**
     * Override the display name to contain the name of the chosen explosion.
     * This is finnicky, and we deal with null pointers in bootstrap, so this is a little long winded.
     */
    @Override
    public Component getName(ItemStack stack) {
        ResourceLocation type = stack.getData(Wands.WAND_SPELL);

        Component stackConfigComponent = Component.literal(type.getPath());
        return Component.translatable(this.getDescriptionId(stack)).append(Component.literal(" - ")).append(stackConfigComponent);
    }
}
