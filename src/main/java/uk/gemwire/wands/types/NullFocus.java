package uk.gemwire.wands.types;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public class NullFocus extends Spell {
    @Override
    public void performMagic(Entity caster, ItemStack wand) {
        // DO NOTHING
    }

    @Override
    public SpellType getSpell() {
        return SpellType.INSTANT;
    }
}
