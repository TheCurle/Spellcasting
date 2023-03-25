package uk.gemwire.wands.types;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public class NullFocus extends WandType {

    @Override
    public String getName() {
        return "null";
    }

    @Override
    public void performMagic(Entity caster, ItemStack wand) {
        // DO NOTHING
    }
}
