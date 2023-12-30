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
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.NotNull;
import uk.gemwire.wands.Wands;
import uk.gemwire.wands.client.entity.SpellProjectileRenderer;
import uk.gemwire.wands.entity.SpellProjectileEntity;
import uk.gemwire.wands.types.Spell;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WandModelLoader implements IGeometryLoader<WandModelLoader.WandGeometry> {

    @Override
    public WandGeometry read(JsonObject contents, JsonDeserializationContext ctx) {
        return new WandGeometry(ctx.deserialize(contents.get("base_model"), BlockModel.class));
    }

    static class WandGeometry implements IUnbakedGeometry<WandGeometry> {
        private final BlockModel handleModel;

        WandGeometry(BlockModel handleModel) { this.handleModel = handleModel; }

        @Override
        public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context) {
            IUnbakedGeometry.super.resolveParents(modelGetter, context);
            handleModel.resolveParents(modelGetter);
        }

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
        public BakedModel applyTransform(ItemDisplayContext transformType, PoseStack poseStack, boolean applyLeftHandTransform) {
            getTransforms().getTransform(transformType).apply(applyLeftHandTransform, poseStack);
            return this;
        }

        @Override
        public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous) {
            return List.of(this);
        }
    }

    private static class WandOverrideList extends ItemOverrides {
        private static final RandomSource RANDOM = RandomSource.create();
        private final Map<Spell, BakedModel> cachedModels = new HashMap<>();

        @Nullable
        @Override
        public BakedModel resolve(@NotNull BakedModel model, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed)
        {
            //noinspection ConstantConditions

            Spell type = Wands.WAND_TYPE_REGISTRY.get(stack.getData(Wands.WAND_SPELL));

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
            event.register(new ResourceLocation(Wands.MODID, "wand_loader"), new WandModelLoader());
        }

        @SubscribeEvent
        public static void onModelRegister(ModelEvent.RegisterAdditional event) {
            for (Spell type : Wands.WAND_TYPE_REGISTRY.stream().collect(Collectors.toSet())) {
                event.register(type.toModelLocation());
            }
        }

        @SubscribeEvent
        public static void onEntityRendererRegister(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(Wands.SPELL_ENTITY.value(), SpellProjectileRenderer::new);
        }
    }
}