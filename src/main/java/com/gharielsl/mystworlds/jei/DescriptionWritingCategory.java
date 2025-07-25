package com.gharielsl.mystworlds.jei;

import com.gharielsl.mystworlds.MystWorlds;
import com.gharielsl.mystworlds.block.MystWorldsBlocks;
import com.gharielsl.mystworlds.recipe.DescriptionWritingFakeRecipe;
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

public class DescriptionWritingCategory implements IRecipeCategory<DescriptionWritingFakeRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(MystWorlds.MOD_ID, "description_writing_fake");
    public static final ResourceLocation TEXTURE = new ResourceLocation(MystWorlds.MOD_ID,
            "textures/gui/writing_jei.png");

    public static final RecipeType<DescriptionWritingFakeRecipe> DESCRIPTION_WRITING_TYPE =
            new RecipeType<>(UID, DescriptionWritingFakeRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public DescriptionWritingCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 121);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(MystWorldsBlocks.WRITING_TABLE_BLOCK.get()));
    }

    @Override
    public RecipeType<DescriptionWritingFakeRecipe> getRecipeType() {
        return DESCRIPTION_WRITING_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal("Description Writing");
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
    public void setRecipe(IRecipeLayoutBuilder builder, DescriptionWritingFakeRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 8, 95).addIngredients(DescriptionWritingFakeRecipe.INPUT_PAPER);

        builder.addSlot(RecipeIngredientRole.INPUT, 26, 95).addIngredients(DescriptionWritingFakeRecipe.INPUT_UNLINKED_DESCRIPTION);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 80, 95).addIngredients(DescriptionWritingFakeRecipe.OUTPUT);

        builder.addSlot(RecipeIngredientRole.INPUT, 116, 95).addIngredients(DescriptionWritingFakeRecipe.INPUT_INK);
        builder.addSlot(RecipeIngredientRole.INPUT, 134, 95).addIngredients(DescriptionWritingFakeRecipe.INPUT_INK);
        builder.addSlot(RecipeIngredientRole.INPUT, 152, 95).addIngredients(DescriptionWritingFakeRecipe.INPUT_INK);

        if (recipe.getId().getPath().equals("linked_description_from_greater_rune")) {
            builder.addSlot(RecipeIngredientRole.INPUT, 8, 27).addIngredients(DescriptionWritingFakeRecipe.INPUT_GREATER_RUNE);
            builder.addSlot(RecipeIngredientRole.INPUT, 8, 45);
            builder.addSlot(RecipeIngredientRole.INPUT, 8, 63);
            builder.addSlot(RecipeIngredientRole.INPUT, 152, 27);
            builder.addSlot(RecipeIngredientRole.INPUT, 152, 45);
            builder.addSlot(RecipeIngredientRole.INPUT, 152, 63);
        } else {
            builder.addSlot(RecipeIngredientRole.INPUT, 8, 27).addIngredients(DescriptionWritingFakeRecipe.INPUT_RUNE);
            builder.addSlot(RecipeIngredientRole.INPUT, 8, 45).addIngredients(DescriptionWritingFakeRecipe.INPUT_RUNE);
            builder.addSlot(RecipeIngredientRole.INPUT, 8, 63).addIngredients(DescriptionWritingFakeRecipe.INPUT_RUNE);
            builder.addSlot(RecipeIngredientRole.INPUT, 152, 27).addIngredients(DescriptionWritingFakeRecipe.INPUT_RUNE);
            builder.addSlot(RecipeIngredientRole.INPUT, 152, 45).addIngredients(DescriptionWritingFakeRecipe.INPUT_RUNE);
            builder.addSlot(RecipeIngredientRole.INPUT, 152, 63).addIngredients(DescriptionWritingFakeRecipe.INPUT_RUNE);
        }
    }
}
