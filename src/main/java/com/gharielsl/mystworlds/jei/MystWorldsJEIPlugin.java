package com.gharielsl.mystworlds.jei;

import com.gharielsl.mystworlds.MystWorlds;
import com.gharielsl.mystworlds.block.MystWorldsBlocks;
import com.gharielsl.mystworlds.recipe.DescriptionWritingFakeRecipe;
import com.gharielsl.mystworlds.recipe.RuneCarvingFakeRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@JeiPlugin
public class MystWorldsJEIPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(MystWorlds.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new RuneCarvingCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new DescriptionWritingCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (Minecraft.getInstance().level == null) {
            return;
        }
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<RuneCarvingFakeRecipe> runeCarvingRecipes = recipeManager.getAllRecipesFor(RuneCarvingFakeRecipe.Type.INSTANCE);
        List<DescriptionWritingFakeRecipe> descriptionWritingRecipes = recipeManager.getAllRecipesFor(DescriptionWritingFakeRecipe.Type.INSTANCE);
        registration.addRecipes(RuneCarvingCategory.RUNE_CARVING_TYPE, runeCarvingRecipes);
        registration.addRecipes(DescriptionWritingCategory.DESCRIPTION_WRITING_TYPE, descriptionWritingRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(
                new ItemStack(MystWorldsBlocks.RUNE_CARVING_STATION.get()),
                RuneCarvingCategory.RUNE_CARVING_TYPE
        );

        registration.addRecipeCatalyst(
                new ItemStack(MystWorldsBlocks.WRITING_TABLE_BLOCK.get()),
                DescriptionWritingCategory.DESCRIPTION_WRITING_TYPE
        );
    }
}
