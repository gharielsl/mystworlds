const fs = require("fs");
const { modid } = require("../datapack_context.json");
const { createTranslation } = require("./lang");
const { hasCommonCraftingRecipe, hasCommonStoneCutterRecipe } = require("../validation");
const { createRecipe, createStoneCutterRecipe } = require("./recipe");

function createItem(name, type, blockContext, stonecutting) {
    createTranslation(`${type === "block_item" ? "block" : "item"}.${modid}.${name}`, name.split('_').map(w => w.charAt(0).toUpperCase() + w.slice(1)).join(' '));
    
    if (blockContext && hasCommonCraftingRecipe.includes(blockContext.type)) {
        createRecipe(blockContext.type, name, blockContext.parent.replace(":block/", ":"));
    }
    if (stonecutting && blockContext && hasCommonStoneCutterRecipe.includes(blockContext.type)) {
        createStoneCutterRecipe(blockContext.type, name, blockContext.parent.replace(":block/", ":"));
    }
    if (hasCommonCraftingRecipe.includes(type)) {
        createRecipe(type, name, blockContext.parent.replace(":block/", ":"));
    }
    if (stonecutting && hasCommonStoneCutterRecipe.includes(type)) {
        createStoneCutterRecipe(type, name, blockContext.parent.replace(":block/", ":"));
    }

    let model = "";

    if (type === "item") {
        model = `{
  "parent": "minecraft:item/generated",
  "textures": {
    "layer0": "${modid}:item/${name}"
  }
}`;
    }
    if (type === "tool_item") {
        model = `{
  "parent": "minecraft:item/handheld",
  "textures": {
    "layer0": "${modid}:item/${name}"
  }
}`;
    }
    if (type === "block_item") {
        if (["fence", "wall", "button"].includes(blockContext.type)) {
            model = `{
    "parent": "${modid}:block/${name}_inventory"
}`;
        } else if (blockContext.type === "trapdoor") {
            model = `{
    "parent": "${modid}:block/${name}_bottom"
}`;
        } else {
            model = `{
    "parent": "${modid}:block/${name}"
}`;
        }
    }

    const modelPath = `../assets/${modid}/models/item/${name}.json`;
    fs.writeFileSync(modelPath, model);
}

module.exports = {
    createItem
};