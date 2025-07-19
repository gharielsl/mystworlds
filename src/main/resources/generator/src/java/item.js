const fs = require("fs");
const { modGroupId } = require("../datapack_context.json");

function addItem(name) {
    const java = `    public static RegistryObject<Item> ${name.toUpperCase()} = ITEMS.register("${name}", () -> new Item(new Item.Properties()));

    // END OF ITEMS`;

    const javaFilePath = `../../java/${modGroupId.replaceAll(".", "/")}/item/MystWorldsItems.java`;
    let javaFile = fs.readFileSync(javaFilePath, "utf-8");
    javaFile = javaFile.replace("\n    // END OF ITEMS", java);
    fs.writeFileSync(javaFilePath, javaFile);
}

module.exports = {
    addItem
};