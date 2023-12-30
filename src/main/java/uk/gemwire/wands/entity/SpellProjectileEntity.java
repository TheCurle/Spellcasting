package uk.gemwire.wands.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
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
    public static final EntityDataAccessor<Spell> spellAccessor = SynchedEntityData.defineId(SpellProjectileEntity.class, Wands.SPELL_SERIALIZER.get());

    public SpellProjectileEntity(EntityType<? extends SpellProjectileEntity> type, Level level) {
        super(type, level, ItemStack.EMPTY);
    }

    public SpellProjectileEntity(Level level, Spell type) {
        super(Wands.SPELL_ENTITY.value(), level, ItemStack.EMPTY);
        entityData.set(spellAccessor, type);
        setNoGravity(true);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(spellAccessor, Wands.FOCUS_NULL.value());
    }

    @Override
    protected void onHit(HitResult res) {
        if (res.getType() == HitResult.Type.MISS) return;
        if ((this.getOwner() instanceof Player p)) {
            if (p.getItemInHand(InteractionHand.MAIN_HAND).is(Wands.WAND_ITEM))
                entityData.get(spellAccessor).performMagic(p, p.getItemInHand(InteractionHand.MAIN_HAND), res);
            else
                entityData.get(spellAccessor).performMagic(p, p.getItemInHand(InteractionHand.OFF_HAND), res);
        }
        this.kill();
    }

    @Override
    protected boolean tryPickup(Player p_150121_) {
        return false;
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }
}
