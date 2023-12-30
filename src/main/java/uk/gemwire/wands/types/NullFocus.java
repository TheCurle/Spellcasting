package uk.gemwire.wands.types;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;

public class NullFocus extends Spell {

    @Override
    public void performMagic(Entity caster, ItemStack wand, HitResult res) {
    }

    @Override
    public SpellType getSpell() {
        return SpellType.INSTANT;
    }
}
