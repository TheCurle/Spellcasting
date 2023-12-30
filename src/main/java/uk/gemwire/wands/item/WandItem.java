package uk.gemwire.wands.item;

import com.mojang.logging.LogUtils;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import uk.gemwire.wands.Wands;
import uk.gemwire.wands.client.debug.WandDebug;
import uk.gemwire.wands.entity.SpellProjectileEntity;
import uk.gemwire.wands.types.Spell;

public class WandItem extends Item {
    public WandItem() {
        super(new Item.Properties().stacksTo(1));
    }

    Logger log = LogUtils.getLogger();

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player p_41433_, InteractionHand p_41434_) {
        if(level.isClientSide) WandDebug.openConfigScreen();

        // Projectile spells
        if(!p_41433_.isCrouching()) {
            Spell type = Wands.WAND_TYPE_REGISTRY.get(p_41433_.getItemInHand(p_41434_).getData(Wands.WAND_SPELL));
            if (type.getSpell() == Spell.SpellType.PROJECTILE) {
                //log.debug("Item in hand is {}, data is {}", p_41433_.getItemInHand(p_41434_), p_41433_.getItemInHand(p_41434_).getData(Wands.WAND_SPELL));
                SpellProjectileEntity spellEntity = new SpellProjectileEntity(level, Wands.WAND_TYPE_REGISTRY.get(p_41433_.getItemInHand(p_41434_).getData(Wands.WAND_SPELL)));
                spellEntity.setOwner(p_41433_);
                log.debug("Entity spell is {}", spellEntity.getEntityData().get(SpellProjectileEntity.spellAccessor));
                spellEntity.setPos(p_41433_.getEyePosition());
                Vec3 movement = p_41433_.getForward().multiply(1.5, 1.5, 1.5).add(p_41433_.getDeltaMovement());
                //log.debug("Entity is moving {}, player is moving {}", movement, p_41433_.getDeltaMovement());
                spellEntity.setDeltaMovement(movement);
                level.addFreshEntity(spellEntity);
            }
        }

        return InteractionResultHolder.pass(p_41433_.getItemInHand(p_41434_));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {

        // Instant spells
        if(!context.getPlayer().isCrouching()) {
            Spell type = Wands.WAND_TYPE_REGISTRY.get(context.getItemInHand().getData(Wands.WAND_SPELL));
            if (type.getSpell() == Spell.SpellType.INSTANT) {
                type.performMagic(context.getPlayer(), context.getItemInHand(), new BlockHitResult(context.getClickLocation(), context.getClickedFace(), context.getClickedPos(), false));
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack item, Player player, LivingEntity entity, InteractionHand p_41401_) {

        // Instant spells
        if(!player.isCrouching()) {
            Spell type = Wands.WAND_TYPE_REGISTRY.get(item.getData(Wands.WAND_SPELL));
            if (type.getSpell() == Spell.SpellType.INSTANT) {
                type.performMagic(player, item, new EntityHitResult(entity));
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

        return Component.translatable(Language.getInstance().getOrDefault(
                "item.spellcasting.wand"), Component.translatable(Language.getInstance().getOrDefault("spell." + type.getNamespace() + "." + type.getPath())));

    }
}
