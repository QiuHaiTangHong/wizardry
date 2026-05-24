//package top.begonia.wizardry.core.recipe;
//
//import com.mojang.serialization.Codec;
//import com.mojang.serialization.MapCodec;
//import com.mojang.serialization.codecs.RecordCodecBuilder;
//import net.minecraft.network.RegistryFriendlyByteBuf;
//import net.minecraft.network.codec.StreamCodec;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.crafting.*;
//import net.minecraft.world.item.crafting.display.RecipeDisplay;
//import net.minecraft.world.level.Level;
//import org.jspecify.annotations.NonNull;
//
//import java.util.List;
//
//public class WizardryRecipe implements Recipe<WizardryRecipeInput> {
//    public static final MapCodec<WizardryRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
//            Ingredient.CODEC.fieldOf("center_ingredient").forGetter(r -> r.centerIngredient),
//            Codec.STRING.listOf().fieldOf("required_elements").forGetter(r -> r.requiredElements),
//            ItemStack.CODEC.fieldOf("result").forGetter(r -> r.result)
//    ).apply(instance, WizardryRecipe::new));
//
//    public static final StreamCodec<RegistryFriendlyByteBuf, WizardryRecipe> STREAM_CODEC = StreamCodec.of(
//            (buf, recipe) -> {
//                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.centerIngredient);
//                buf.writeJsonWithCodec(Codec.STRING.listOf(), recipe.requiredElements);
//                ItemStack.STREAM_CODEC.encode(buf, recipe.result);
//            },
//            buf -> {
//                Ingredient center = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
//                List<String> elements = buf.readJsonWithCodec(Codec.STRING.listOf());
//                ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
//                return new WizardryRecipe(center, elements, result);
//            }
//    );
//
//    private final Ingredient centerIngredient;
//    private final List<String> requiredElements;
//    private final ItemStack result;
//
//    public WizardryRecipe(Ingredient centerIngredient, List<String> requiredElements, ItemStack result) {
//        this.centerIngredient = centerIngredient;
//        this.requiredElements = requiredElements;
//        this.result = result;
//    }
//
//    @Override
//    public boolean matches(@NonNull WizardryRecipeInput wizardryRecipeInput, @NonNull Level level) {
//        return false;
//    }
//
//    @Override
//    public @NonNull ItemStack assemble(@NonNull WizardryRecipeInput wizardryRecipeInput) {
//        return this.result.copy();
//    }
//
//    @Override
//    public boolean isSpecial() {
//        return Recipe.super.isSpecial();
//    }
//
//    @Override
//    public boolean showNotification() {
//        return false;
//    }
//
//    @Override
//    public @NonNull String group() {
//        return "";
//    }
//
//    @Override
//    public @NonNull RecipeSerializer<? extends Recipe<WizardryRecipeInput>> getSerializer() {
//        return null;
//    }
//
//    @Override
//    public @NonNull RecipeType<? extends Recipe<WizardryRecipeInput>> getType() {
//        return null;
//    }
//
//    @Override
//    public @NonNull PlacementInfo placementInfo() {
//        return null;
//    }
//
//    @Override
//    public @NonNull List<RecipeDisplay> display() {
//        return Recipe.super.display();
//    }
//
//    @Override
//    public @NonNull RecipeBookCategory recipeBookCategory() {
//        return null;
//    }
//
//    public static class Serializer extends RecipeSerializer<WizardryRecipe> {
//        @Override
//        public @NonNull MapCodec<WizardryRecipe> codec() {
//            return CODEC;
//        }
//
//        @Override
//        public @NonNull StreamCodec<RegistryFriendlyByteBuf, WizardryRecipe> streamCodec() {
//            return STREAM_CODEC;
//        }
//
//        public static WizardryRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
//            Ingredient center = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
//            List<String> elements = buf.readJsonWithCodec(Codec.STRING.listOf());
//            ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
//            return new WizardryRecipe(center, elements, result);
//        }
//
//        public static void toNetwork(RegistryFriendlyByteBuf buf, WizardryRecipe recipe) {
//            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.centerIngredient);
//            buf.writeJsonWithCodec(Codec.STRING.listOf(), recipe.requiredElements);
//            ItemStack.STREAM_CODEC.encode(buf, recipe.result);
//        }
//    }
//}
