package uk.gemwire.wands;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.gemwire.wands.item.WandItem;
import uk.gemwire.wands.network.Network;
import uk.gemwire.wands.types.NullFocus;
import uk.gemwire.wands.types.WandType;

import java.util.function.Supplier;

@Mod(Wands.MODID)
public class Wands {

    public static final Logger LOGGER = LogManager.getLogger(Wands.class);
    public static final String MODID = "spellcasting";

    /**
     * Wand Type registry stuff.
     */

    public static final DeferredRegister<WandType> WANDS = DeferredRegister.create(new ResourceLocation(MODID, "wand_types"), MODID);

    public static Supplier<IForgeRegistry<WandType>> WAND_TYPE_REGISTRY = WANDS.makeRegistry(() ->
            new RegistryBuilder<WandType>().setMaxID(Integer.MAX_VALUE - 1).onAdd((owner, stage, id, key, obj, old) ->
                    LOGGER.info("WandType Added: " + key.location() + " ")
            ).setDefaultKey(new ResourceLocation(MODID, "null"))
    );

    /**
     * Deferred Registers.
     */

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    /**
     * Registry Objects.
     */

    public static final RegistryObject<Item> WAND_ITEM = ITEMS.register("wand", WandItem::new);

    // Do Nothing.
    public static final RegistryObject<WandType> FOCUS_NULL = WANDS.register("null", NullFocus::new);

    public void buildContents(CreativeModeTabEvent.Register event) {
        event.registerCreativeModeTab(new ResourceLocation(MODID, "wands"), builder ->
                // Set name of tab to display
                builder.title(Component.translatable("item_group." + MODID + ".wands"))
                        // Set icon of creative tab
                        .icon(() -> new ItemStack(WAND_ITEM.get()))
                        // Add default items to tab
                        .displayItems((params, output, abc) -> {
                            for(RegistryObject<WandType> type : WANDS.getEntries()) {
                                ItemStack newItem = new ItemStack(WAND_ITEM.get());
                                newItem.getCapability(Capabilities.WAND_FOCUS_CAPABILITY).ifPresent(cap ->
                                        cap.setFocus(WAND_TYPE_REGISTRY.get().getKey(type.get())));
                                output.accept(newItem);
                            }
                        })
        );
    }

    public Wands() {
        WANDS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::buildContents);
        Network.setup();
    }
}
