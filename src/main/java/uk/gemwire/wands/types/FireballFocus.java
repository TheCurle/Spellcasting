package uk.gemwire.wands.types;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class FireballFocus extends Spell {

    @Override
    public void performMagic(Entity caster, ItemStack wand, HitResult res) {
        if (res.getType() == HitResult.Type.BLOCK) {
            BlockHitResult bhr = (BlockHitResult) res;
            BlockState blockstate = caster.level().getBlockState(bhr.getBlockPos());
            BlockPos blockpos = bhr.getBlockPos();
            Level level = caster.level();
            if (!CampfireBlock.canLight(blockstate) && !CandleBlock.canLight(blockstate) && !CandleCakeBlock.canLight(blockstate)) {
                BlockPos blockpos1 = blockpos.relative(bhr.getDirection());
                if (BaseFireBlock.canBePlacedAt(caster.level(), blockpos1, bhr.getDirection())) {
                    level.playSound(caster, blockpos1, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
                    BlockState blockstate1 = BaseFireBlock.getState(caster.level(), blockpos1);
                    level.setBlock(blockpos1, blockstate1, 11);
                    level.gameEvent(caster, GameEvent.BLOCK_PLACE, blockpos);
                    ItemStack itemstack = wand;
                    if (caster instanceof ServerPlayer) {
                        CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) caster, blockpos1, itemstack);
                        itemstack.hurtAndBreak(1, (LivingEntity) caster, p_41300_ -> p_41300_.broadcastBreakEvent(((ServerPlayer) caster).getUsedItemHand()));
                    }
                }
            } else {
                level.playSound(caster, blockpos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
                level.setBlock(blockpos, blockstate.setValue(BlockStateProperties.LIT, Boolean.valueOf(true)), 11);
                level.gameEvent(caster, GameEvent.BLOCK_CHANGE, blockpos);
            }
        } else {
            EntityHitResult ehr = (EntityHitResult) res;
            ehr.getEntity().setSecondsOnFire(5);
        }

    }

    @Override
    public SpellType getSpell() {
        return SpellType.PROJECTILE;
    }
}
