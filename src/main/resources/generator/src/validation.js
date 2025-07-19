const fs = require("fs");
const { modid, modGroupId } = require("./datapack_context.json");

const assetTypes = ["item", "tool_item", "block_item", "block", "cube_all", "cross", "slab", "stairs", "fence", "fence_gate", "wall", "door", "trapdoor"];
const assetTypeRequiresParent = ["slab", "stairs", "fence", "fence_gate", "wall", "door", "trapdoor"];
const isBlock = ["block", "cube_all", "cross", "slab", "stairs", "fence", "fence_gate", "wall", "door", "trapdoor"];
const hasCommonCraftingRecipe = ["slab", "stairs", "fence", "fence_gate", "wall", "door", "trapdoor"];
const hasCommonStoneCutterRecipe = ["slab", "stairs", "wall"];

function validatePaths(assetConfig) {
    const { assetType, drops, addToJava } = assetConfig;

    const recipePath = `../data/${modid}/recipes`;
    if (!fs.existsSync(recipePath)) {
        const message = "recipes path not found";
        if (hasCommonCraftingRecipe.includes(assetType) || hasCommonStoneCutterRecipe.includes(assetType)) {
            console.error(message);
            return false;
        } else {
            console.warn(message);
        }
    }
    const lootTablePath = `../data/${modid}/loot_tables`;
    if (!fs.existsSync(lootTablePath)) {
        const message = "loot_tables path not found";
        if (drops) {
            console.error(message);
            return false;
        } else {
            console.warn(message);
        }
    }
    const langPath = `../assets/${modid}/lang/en_us.json`;
    if (!fs.existsSync(langPath)) {
        console.error("langPath not found");
        return false;
    }
    const modelsItemPath = `../assets/${modid}/models/item`;
    if (!fs.existsSync(modelsItemPath)) {
        console.error("modelsItemPath not found");
        return false;
    }
    const modelBlockPath = `../assets/${modid}/models/block`;
    if (!fs.existsSync(modelBlockPath)) {
        console.error("modelBlockPath not found");
        return false;
    }
    const blockstatesPath = `../assets/${modid}/blockstates`;
    if (!fs.existsSync(blockstatesPath)) {
        console.error("blockstatesPath not found");
        return false;
    }

    const javaItemsPath = `../../java/${modGroupId.replaceAll(".", "/")}/item/MystWorldsItems.java`;
    console.log(javaItemsPath);
    if (addToJava && !fs.existsSync(javaItemsPath)) {
        console.error("javaItemsPath not found");
        return false;
    }
    const javaBlocksPath = `../../java/${modGroupId.replaceAll(".", "/")}/block/MystWorldsBlocks.java`;
    if (addToJava && !fs.existsSync(javaBlocksPath)) {
        console.error("javaBlocksPath not found");
        return false;
    }
    const javaItemGroupsPath = `../../java/${modGroupId.replaceAll(".", "/")}/item/MystWorldsItemGroups.java`;
    if (addToJava && !fs.existsSync(javaItemGroupsPath)) {
        console.error("javaItemGroupsPath not found");
        return false;
    }
    return true;
}

function validateParentAsset(parentAsset) {
    if (!parentAsset) {
        console.error("assetType must include parentAsset");
        return false;
    }
    if (!/\w+:block\/\w+/.test(parentAsset)) {
        console.error("parentAsset must match /\\w+:block\\/\\w+/");
        return false;
    }
    return true;
}

function validate(assetConfig) {
    if (!validatePaths(assetConfig)) {
        return false;
    }

    const { assetType, parentAsset, stonecutting } = assetConfig;
    if (!assetTypes.includes(assetType)) {
        console.error("assetType invalid");
        return false;
    }
    if (assetTypeRequiresParent.includes(assetType) && !validateParentAsset(parentAsset)) {
        return false;
    }
    if (stonecutting && !hasCommonStoneCutterRecipe.includes(assetType)) {
        console.warn("assetType does not have a stonecutting recipe");
    }
    return true;
}

module.exports = {
    assetTypes,
    assetTypeRequiresParent,
    isBlock,
    hasCommonCraftingRecipe,
    hasCommonStoneCutterRecipe,
    validate
};