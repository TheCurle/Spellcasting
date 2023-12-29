package uk.gemwire.wands.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import uk.gemwire.wands.Wands;
import uk.gemwire.wands.types.Spell;

public class SpellProjectileEntity extends AbstractArrow {

    private Spell spell;

    public SpellProjectileEntity(EntityType<SpellProjectileEntity> type, Level level) {
        super(type, level, ItemStack.EMPTY);
        this.spell = spell;
    }

    @Override
    protected void onHit(HitResult res) {
        if (res.getType() == HitResult.Type.MISS) return;
        if ((this.getOwner() instanceof Player p)) {
            if (p.getItemInHand(InteractionHand.MAIN_HAND).is(Wands.WAND_ITEM))
                spell.performMagic(p, p.getItemInHand(InteractionHand.MAIN_HAND));
            else
                spell.performMagic(p, p.getItemInHand(InteractionHand.OFF_HAND));
        }
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }
}
