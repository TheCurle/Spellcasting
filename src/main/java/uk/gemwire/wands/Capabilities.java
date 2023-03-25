package uk.gemwire.wands;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid=Wands.MODID)
public class Capabilities {
    public static final Capability<FocusData> WAND_FOCUS_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    @SubscribeEvent
    public static void registerCaps(RegisterCapabilitiesEvent event) {
        event.register(FocusData.class);
    }

    @SubscribeEvent
    public static void attachCaps(AttachCapabilitiesEvent<ItemStack> e) {
        if(e.getObject().getItem() == Wands.WAND_ITEM.get()) {
            FocusData data = new FocusData();
            LazyOptional<FocusData> lazyData = LazyOptional.of(() -> data);

            ICapabilitySerializable<StringTag> provider = new ICapabilitySerializable<StringTag>() {
                public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                    if (cap == WAND_FOCUS_CAPABILITY)
                        return lazyData.cast();
                    return LazyOptional.empty();
                }

                @Override
                public StringTag serializeNBT() {
                    return StringTag.valueOf(data.getType().toString());
                }

                @Override
                public void deserializeNBT(StringTag nbt) {
                    data.setFocus(new ResourceLocation(nbt.getAsString()));
                }
            };

            e.addCapability(new ResourceLocation(Wands.MODID, "type"), provider);
        }
    }

    public static class FocusData {
        ResourceLocation data = new ResourceLocation(Wands.MODID, "null");
        public void setFocus(ResourceLocation type) { data = type; }
        public ResourceLocation getType() { return data; }
    }

}
