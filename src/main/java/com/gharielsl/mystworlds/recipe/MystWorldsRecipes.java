package com.gharielsl.mystworlds.recipe;

import com.gharielsl.mystworlds.MystWorlds;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MystWorldsRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MystWorlds.MOD_ID);

    public static final RegistryObject<RecipeSerializer<RuneCarvingFakeRecipe>> RUNE_CARVING_SERIALIZER =
            RECIPE_SERIALIZERS.register("rune_carving_fake", () -> RuneCarvingFakeRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<DescriptionWritingFakeRecipe>> DESCRIPTION_WRITING_SERIALIZER =
            RECIPE_SERIALIZERS.register("description_writing_fake", () -> DescriptionWritingFakeRecipe.Serializer.INSTANCE);
}
