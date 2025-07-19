const fs = require("fs");
const { modGroupId } = require("../datapack_context.json");

function addBlock(name, parent, type) {
    let javaBlockType = "Block";

    const javaTypes = {
        "slab": "SlabBlock", 
        "stairs": "StairBlock", 
        "fence": "FenceBlock", 
        "fence_gate": "FenceGateBlock", 
        "wall": "WallBlock", 
        "door": "DoorBlock", 
        "trapdoor": "TrapDoorBlock" 
    }

    const javaType = javaTypes[type];

    let java = `    public static Supplier<Block> ${name.toUpperCase()} = registerBlockWithItem(${name},
        () -> new ${javaType}(BlockBehaviour.Properties.copy(Blocks.${parent.toUpperCase()})), new Item.Properties());

    // END OF BLOCKS`;

    if (javaBlockType === "StairBlock") {
        java = java.replace(
            `new ${javaType}(BlockBehaviour.Properties.copy(Blocks.${parent.toUpperCase()}))`,
            `new ${javaType}(Blocks.${parent.toUpperCase()}.getDefaultState(), BlockBehaviour.Properties.copy(Blocks.${parent.toUpperCase()}))`
        );
    }

    const javaFilePath = `../../java/${modGroupId.replaceAll(".", "/")}/block/MystWorldsBlocks.java`;
    let javaFile = fs.readFileSync(javaFilePath, "utf-8");
    javaFile = javaFile.replace("\n    // END OF BLOCKS", java);
    fs.writeFileSync(javaFilePath, javaFile);
}

module.exports = {
    addBlock
};