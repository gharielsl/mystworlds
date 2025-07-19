const fs = require("fs");
const { modid } = require("../datapack_context.json");

function createRecipe(blockType, blockName, materialItem) {
    let shape = [];
    let count = 1;

    if (blockType === "slab") {
        shape = ["###"];
        count = 6;
    }
    if (blockType === "stairs") {
        shape = [
            "  #",
            " ##",
            "###"
        ];
        count = 4;
    }
    if (blockType === "fence") {
        shape = [
            "#/#",
            "#/#",
            "#/#"
        ];
        count = 3
    }
    if (blockType === "fence_gate") {
        shape = [
            "/#/",
            "/#/",
            "/#/"
        ];
    }
    if (blockType === "wall") {
        shape = [
            "###",
            "###"
        ];
        count = 6;
    }
    if (blockType === "door") {
        shape = [
            "##",
            "##",
            "##"
        ];
        count = 3;
    }
    if (blockType === "trapdoor") {
        shape = [
            "###",
            "###"
        ];
        count = 2;
    }

    const json = 
`{
  "type": "minecraft:crafting_shaped",
  "key": {
    "/": {
      "item": "minecraft:stick"
    },
    "#": {
      "item": "${materialItem}"
    }
  },
  "pattern": ${JSON.stringify(shape)},
  "result": {
    "count": ${count},
    "item": "${blockName}"
  },
  "show_notification": true
}`;

    const recipePath = `../data/${modid}/recipes/${blockName}.json`;
    fs.writeFileSync(recipePath, json);
}

function createStoneCutterRecipe(blockType, blockName, materialItem) {
    let count = 1;

    if (blockType === "slab") {
        count = 2;
    }
    if (blockType === "stairs") {
        count = 1;
    }
    if (blockType === "wall") {
        count = 1;
    }

    const json = 
`{
  "type": "minecraft:stonecutting",
  "count": ${count},
  "ingredient": {
    "item": "${materialItem}"
  },
  "result": "${blockName}"
}`;

    const recipePath = `../data/${modid}/recipes/${blockName}_from_stonecutting.json`;
    fs.writeFileSync(recipePath, json);
}

module.exports = {
    createRecipe,
    createStoneCutterRecipe
};