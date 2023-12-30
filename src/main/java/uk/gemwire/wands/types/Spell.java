package uk.gemwire.wands.types;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import uk.gemwire.wands.Wands;

/**
 * A wand type.
 *
 * Has a name, which is used to find localization and textures.
 *
 * Contains the logic for casting magic when used on a wand.
 *
 * May be a Projectile type spell, an Instant type spell, or a Beam type spell.
 */
public abstract class Spell {
    public enum SpellType {
        INSTANT,    // Touch Spell; cast directly on a block or an entity.
        PROJECTILE, // Projectile Spell; fire a ball that travels without gravity, spell is applied at the contact point.
        BEAM,       // Beam Spell; effect is applied to every entity in a specified area in front of the player.
    }

    public ResourceLocation toModelLocation() {
        ResourceLocation typeName = Wands.WAND_TYPE_REGISTRY.getKey(this);
        return new ResourceLocation(typeName.getNamespace(), "item/focus_" + typeName.getPath());
    }

    public ResourceLocation toEntityTextureLocation() {
        ResourceLocation typeName = Wands.WAND_TYPE_REGISTRY.getKey(this);
        return new ResourceLocation(typeName.getNamespace(), "textures/entity/spell_projectile/" + typeName.getPath() + ".png");
    }

    // Cast on when the spell lands; projectile hits surface, instant touch, or beam affects entity.
    public abstract void performMagic(Entity caster, ItemStack wand, HitResult res);

    public abstract SpellType getSpell();
}
