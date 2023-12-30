package uk.gemwire.wands;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.gemwire.wands.client.entity.SpellProjectileRenderer;
import uk.gemwire.wands.entity.SpellProjectileEntity;
import uk.gemwire.wands.item.WandItem;
import uk.gemwire.wands.network.Network;
import uk.gemwire.wands.types.FireballFocus;
import uk.gemwire.wands.types.NullFocus;
import uk.gemwire.wands.types.Spell;

import javax.swing.text.html.parser.Entity;
import java.util.function.Supplier;

@Mod(Wands.MODID)
public class Wands {

    public static final Logger LOGGER = LogManager.getLogger(Wands.class);
    public static final String MODID = "spellcasting";

    /**
     * Wand Type registry stuff.
     */

    public static final DeferredRegister<Spell> WANDS = DeferredRegister.create(new ResourceLocation(MODID, "wand_types"), MODID);
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MODID);
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, MODID);
    private static final DeferredRegister<EntityDataSerializer<?>> SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, MODID);


    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<Spell>> SPELL_SERIALIZER = SERIALIZERS.register("spell", () -> EntityDataSerializer.simple((FriendlyByteBuf buf, Spell s) -> buf.writeResourceLocation(Wands.WAND_TYPE_REGISTRY.getKey(s)), (FriendlyByteBuf buf) -> Wands.WAND_TYPE_REGISTRY.get(buf.readResourceLocation())));

    public static final Supplier<AttachmentType<ResourceLocation>> WAND_SPELL = ATTACHMENT_TYPES.register(
            "wand_spell", () -> AttachmentType.builder(() -> new ResourceLocation(MODID, "null")).serialize(ResourceLocation.CODEC).build());


    public static Registry<Spell> WAND_TYPE_REGISTRY = WANDS.makeRegistry((builder) ->
                    builder
                    .maxId(Integer.MAX_VALUE - 1).onAdd((reg, id, key, val) ->
                    LOGGER.info("WandType Added: " + key.location() + " ")
            ).defaultKey(new ResourceLocation(MODID, "null"))
    );

    /**
     * Deferred Registers.
     */

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);

    /**
     * Registry Objects.
     */

    public static final Holder<Item> WAND_ITEM = ITEMS.register("wand", WandItem::new);

    // Do Nothing.
    public static final Holder<Spell> FOCUS_NULL = WANDS.register("null", NullFocus::new);

    public static final Holder<Spell> FOCUS_FIREBALL = WANDS.register("fireball", FireballFocus::new);


    public static final DeferredHolder<EntityType<?>, EntityType<SpellProjectileEntity>> SPELL_ENTITY = ENTITIES.register("spell", () ->
            EntityType.Builder.<SpellProjectileEntity>of(SpellProjectileEntity::new, MobCategory.MISC).build("spell")
    );

    public static final Holder<CreativeModeTab> WANDS_TAB = TABS.register("wands", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("item_group." + MODID + ".wands"))
                    // Set icon of creative tab
                    .icon(() -> new ItemStack(WAND_ITEM))
                    // Add default items to tab
                    .displayItems((params, output) -> {
                        for(Holder<Spell> type : WANDS.getEntries()) {
                            ItemStack newItem = new ItemStack(WAND_ITEM);
                            newItem.setData(Wands.WAND_SPELL,WAND_TYPE_REGISTRY.getKey(type.value()));
                            output.accept(newItem);
                        }
                    })
                    .build()
    );

    public Wands(IEventBus bus) {
        WANDS.register(bus);
        ITEMS.register(bus);
        ENTITIES.register(bus);
        ATTACHMENT_TYPES.register(bus);
        TABS.register(bus);
        SERIALIZERS.register(bus);
        Network.setup();
    }
}
