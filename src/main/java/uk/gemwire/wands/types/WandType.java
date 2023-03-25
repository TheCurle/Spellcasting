package uk.gemwire.wands.types;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import uk.gemwire.wands.Wands;

/**
 * A wand type.
 *
 * Has a name, which is used to find localization and textures.
 *
 * Contains the logic for casting magic when used on a wand.
 */
public abstract class WandType {

    public ResourceLocation toModelLocation() {
        ResourceLocation typeName = Wands.WAND_TYPE_REGISTRY.get().getKey(this);
        return new ResourceLocation(typeName.getNamespace(), "item/focus_" + typeName.getPath());
    }

    public abstract void performMagic(Entity caster, ItemStack wand);
}
