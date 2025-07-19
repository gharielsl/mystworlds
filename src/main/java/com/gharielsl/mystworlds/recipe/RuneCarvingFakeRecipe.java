package com.gharielsl.mystworlds.recipe;

import com.gharielsl.mystworlds.MystWorlds;
import com.gharielsl.mystworlds.item.MystWorldsItems;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RuneCarvingFakeRecipe implements Recipe<SimpleContainer> {
    public static final Ingredient INPUT = Ingredient.of(MystWorldsItems.TERRAIN_RUNE.get(), MystWorldsItems.STABILITY_RUNE.get(), MystWorldsItems.SKY_RUNE.get(), MystWorldsItems.TIME_RUNE.get());
    public static final Ingredient INPUT_AMETHYST = Ingredient.of(Items.AMETHYST_SHARD);
    public static final Ingredient OUTPUT = Ingredient.of(Items.LAPIS_LAZULI, Items.REDSTONE);
    public static final Ingredient OUTPUT_RUNE = Ingredient.of(
            MystWorldsItems.RUNE_OF_ARCADIA.get(),
            MystWorldsItems.RUNE_OF_MEMPHIS.get(),
            MystWorldsItems.RUNE_OF_PHILAE.get(),
            MystWorldsItems.RUNE_OF_NAUCRATIS.get(),
            MystWorldsItems.RUNE_OF_STAFFA.get(),
            MystWorldsItems.RUNE_OF_SANTORINI.get(),
            MystWorldsItems.RUNE_OF_THESSALY.get(),
            MystWorldsItems.RUNE_OF_OLYMPUS.get(),
            MystWorldsItems.RUNE_OF_PETRA.get(),

            MystWorldsItems.RUNE_OF_ETNA.get(),
            MystWorldsItems.RUNE_OF_DELPHI.get(),
            MystWorldsItems.RUNE_OF_THERA.get(),
            MystWorldsItems.RUNE_OF_LUXOR.get(),
            MystWorldsItems.RUNE_OF_ASWAN.get(),

            MystWorldsItems.RUNE_OF_KNOSSOS.get(),
            MystWorldsItems.RUNE_OF_DELOS.get(),
            MystWorldsItems.RUNE_OF_EROS.get(),
            MystWorldsItems.RUNE_OF_RHODES.get(),
            MystWorldsItems.RUNE_OF_ARGOS.get(),

            MystWorldsItems.RUNE_OF_NYX.get(),
            MystWorldsItems.RUNE_OF_HELIOPOLIS.get(),
            MystWorldsItems.RUNE_OF_HADES.get(),

            MystWorldsItems.MEMORY_STONE.get()
    );

    private final NonNullList<Ingredient> inputItems;
    private final ItemStack output;
    private final ResourceLocation id;

    public RuneCarvingFakeRecipe(NonNullList<Ingredient> inputItems, ItemStack output, ResourceLocation id) {
        this.inputItems = inputItems;
        this.output = output;
        this.id = id;
    }

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        if(pLevel.isClientSide()) {
            return false;
        }

        return inputItems.get(0).test(pContainer.getItem(0));
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return inputItems;
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess pRegistryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<RuneCarvingFakeRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "rune_carving_fake";
    }

    public static class Serializer implements RecipeSerializer<RuneCarvingFakeRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(MystWorlds.MOD_ID, "rune_carving_fake");

        @Override
        public @NotNull RuneCarvingFakeRecipe fromJson(ResourceLocation recipeId, JsonObject serializedRecipe) {
            return new RuneCarvingFakeRecipe(NonNullList.of(INPUT), new ItemStack(Items.STICK), recipeId);
        }

        @Override
        public @Nullable RuneCarvingFakeRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(buffer.readInt(), Ingredient.EMPTY);

            for(int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(buffer));
            }

            ItemStack output = buffer.readItem();
            return new RuneCarvingFakeRecipe(inputs, output, recipeId);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, RuneCarvingFakeRecipe recipe) {
            buffer.writeInt(recipe.inputItems.size());

            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buffer);
            }

            buffer.writeItemStack(recipe.getResultItem(null), false);
        }
    }
}
