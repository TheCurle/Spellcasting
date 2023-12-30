package uk.gemwire.wands.types;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;

public class TorchSummonFocus extends Spell {

    @Override
    public void performMagic(Entity caster, ItemStack wand, HitResult res) {
        if (res instanceof EntityHitResult) return;
        caster.level().setBlock(((BlockHitResult) res).getBlockPos(), getPlacementState((BlockHitResult) res, (Player) caster), Block.UPDATE_ALL);
    }

    protected BlockState getPlacementState(BlockHitResult res, Player caster) {
        BlockPlaceContext ctx = new BlockPlaceContext(caster.level(), caster, caster.getUsedItemHand(), caster.getItemInHand(caster.getUsedItemHand()), res);
        BlockState blockstate = Blocks.WALL_TORCH.getStateForPlacement(ctx);
        BlockState blockstate1 = null;
        LevelReader levelreader = caster.level();
        BlockPos blockpos = ctx.getClickedPos().above();

        for(Direction direction : ctx.getNearestLookingDirections()) {
            if (direction != res.getDirection().getOpposite()) {
                BlockState blockstate2 = direction == res.getDirection() ? Blocks.TORCH.getStateForPlacement(ctx) : blockstate;
                if (blockstate2 != null && this.canPlace(levelreader, blockstate2, blockpos)) {
                    blockstate1 = blockstate2;
                    break;
                }
            }
        }

        return blockstate1 != null && levelreader.isUnobstructed(blockstate1, blockpos, CollisionContext.empty()) ? blockstate1 : null;
    }

    protected boolean canPlace(LevelReader p_250350_, BlockState p_249311_, BlockPos p_250328_) {
        return p_249311_.canSurvive(p_250350_, p_250328_);
    }

    @Override
    public SpellType getSpell() {
        return SpellType.INSTANTPROJECTILE;
    }
}
