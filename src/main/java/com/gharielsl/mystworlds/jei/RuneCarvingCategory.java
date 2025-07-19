package com.gharielsl.mystworlds.jei;

import com.gharielsl.mystworlds.MystWorlds;
import com.gharielsl.mystworlds.block.MystWorldsBlocks;
import com.gharielsl.mystworlds.recipe.RuneCarvingFakeRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class RuneCarvingCategory implements IRecipeCategory<RuneCarvingFakeRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(MystWorlds.MOD_ID, "rune_carving_fake");
    public static final ResourceLocation TEXTURE = new ResourceLocation(MystWorlds.MOD_ID,
            "textures/gui/rune_carving_jei.png");

    public static final RecipeType<RuneCarvingFakeRecipe> RUNE_CARVING_TYPE =
            new RecipeType<>(UID, RuneCarvingFakeRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public RuneCarvingCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 83);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(MystWorldsBlocks.RUNE_CARVING_STATION.get()));
    }

    @Override
    public RecipeType<RuneCarvingFakeRecipe> getRecipeType() {
        return RUNE_CARVING_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal("Rune Carving");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RuneCarvingFakeRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 26, 22).addIngredients(RuneCarvingFakeRecipe.INPUT);
        builder.addSlot(RecipeIngredientRole.INPUT, 26, 48).addIngredients(RuneCarvingFakeRecipe.INPUT_AMETHYST);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 80, 22).addIngredients(RuneCarvingFakeRecipe.OUTPUT_RUNE);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 106, 22).addIngredients(RuneCarvingFakeRecipe.OUTPUT);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 132, 22);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 80, 48);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 106, 48);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 132, 48);
    }
}
