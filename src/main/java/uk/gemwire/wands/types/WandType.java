package uk.gemwire.wands.types;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import uk.gemwire.wands.Wands;

import java.util.Locale;

/**
 * A wand type.
 *
 * Has a name, which is used to find localization and textures.
 *
 * Contains the logic for casting magic when used on a wand.
 */
public abstract class WandType {

    public ResourceLocation toModelLocation() {
        return new ResourceLocation(Wands.MODID, "item/focus_" + getName().toLowerCase(Locale.ROOT));
    }

    public abstract String getName();

    public abstract void performMagic(Entity caster, ItemStack wand);
}
