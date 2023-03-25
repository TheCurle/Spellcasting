package uk.gemwire.wands.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import uk.gemwire.wands.Capabilities;
import uk.gemwire.wands.Wands;
import uk.gemwire.wands.types.WandType;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class WandModelLoader implements IGeometryLoader<WandModelLoader.WandGeometry> {

    @Override
    public WandGeometry read(JsonObject contents, JsonDeserializationContext ctx) {
        return new WandGeometry(ctx.deserialize(contents.get("base_model"), BlockModel.class));
    }

    static class WandGeometry implements IUnbakedGeometry<WandGeometry> {
        private final BlockModel handleModel;

        WandGeometry(BlockModel handleModel) { this.handleModel = handleModel; }

        @Override
        public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
            BakedModel bakedHandle = handleModel.bake(baker, handleModel, spriteGetter, modelState, modelLocation, false);
            return new WandOverrideModel(bakedHandle);
        }
    }

    private static class WandOverrideModel extends BakedModelWrapper<BakedModel> {
        private final ItemOverrides overrideList;

        WandOverrideModel(BakedModel originalModel) {
            super(originalModel);
            this.overrideList = new WandOverrideList();
        }

        @Override
        public ItemOverrides getOverrides() {
            return overrideList;
        }
    }

    private static class WandModel extends BakedModelWrapper<BakedModel> {
        private final List<BakedQuad> quads;

        WandModel(BakedModel handleModel, List<BakedQuad> quads) {
            super(handleModel);
            this.quads = quads;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
            if (side == null) {
                return quads;
            }
            return List.of();
        }

        @Override
        public BakedModel applyTransform(ItemTransforms.TransformType transformType, PoseStack poseStack, boolean applyLeftHandTransform) {
            getTransforms().getTransform(transformType).apply(applyLeftHandTransform, poseStack);
            return this;
        }
    }

    private static class WandOverrideList extends ItemOverrides {
        private static final RandomSource RANDOM = RandomSource.create();
        private final Map<WandType, BakedModel> cachedModels = new HashMap<>();

        @Nullable
        @Override
        public BakedModel resolve(@NotNull BakedModel model, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed)
        {
            //noinspection ConstantConditions
            Capabilities.FocusData data = stack.getCapability(Capabilities.WAND_FOCUS_CAPABILITY).orElseThrow(RuntimeException::new);
            WandType type = Wands.WAND_TYPE_REGISTRY.get().getValue(data.getType());

            return cachedModels.computeIfAbsent(type, mat -> {
                List<BakedQuad> quads = new ArrayList<>(model.getQuads(null, null, RANDOM, ModelData.EMPTY, null));

                BakedModel headModel = Minecraft.getInstance().getModelManager().getModel(mat.toModelLocation());
                quads.addAll(headModel.getQuads(null, null, RANDOM, ModelData.EMPTY, null));

                return new WandModel(model, quads);
            });
        }
    }

    @Mod.EventBusSubscriber(modid = Wands.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onGRegisterLoaders(ModelEvent.RegisterGeometryLoaders event) {
            event.register("wand_loader", new WandModelLoader());
        }

        @SubscribeEvent
        public static void onModelRegister(ModelEvent.RegisterAdditional event) {
            for (WandType type : Wands.WAND_TYPE_REGISTRY.get().getValues()) {
                event.register(type.toModelLocation());
            }
        }
    }
}