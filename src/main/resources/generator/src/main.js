const { addBlock } = require("./java/block");
const { addItem } = require("./java/item");
const { addItemToGroup } = require("./java/item_group");
const { createBlock } = require("./json/block");
const { createItem } = require("./json/item");
const { isBlock, validate } = require("./validation");

const assetConfig = {
    assetName: "rune_of_saya", // e.g. fancy_stone_stairs
    assetType: "item", // stairs
    parentAsset: "", // minecraft:block/stone
    javaBlockParent: "", // stone_stairs
    drops: false,
    stonecutting: false,
    addToJava: true,
    hasBlockGroup: false
}

function main() {
    console.log(assetConfig.assetName);
    if (!validate(assetConfig)) return;

    const { assetName, assetType, parentAsset, drops, stonecutting, javaBlockParent, addToJava, hasBlockGroup } = assetConfig;
    if (isBlock.includes(assetType)) {
        createBlock(assetName, assetType, parentAsset, drops, stonecutting);
        if (addToJava) {
            addBlock(assetName, javaBlockParent ? javaBlockParent : parentAsset.replace(/\w+:block\//, ""), assetType);
            addItemToGroup(assetName, hasBlockGroup ? "blocks" : "items");
        }
    } else {
        createItem(assetName, assetType, null, stonecutting);
        if (addToJava) {
            addItem(assetName);
            addItemToGroup(assetName, "items");
        }
    }
}

main();
