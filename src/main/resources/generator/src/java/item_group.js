const fs = require("fs");
const { modGroupId } = require("../datapack_context.json");

function addItemToGroup(name, group) {
    const itemClassName = group === "blocks" ? "MystWorldsBlocks" : "MystWorldsItems";
    const java = `\n                        output.accept(${itemClassName}.${name.toUpperCase()}.get());
                        // END OF ${group.toUpperCase()}`;

    const javaFilePath = `../../java/${modGroupId.replaceAll(".", "/")}/item/MystWorldsItemGroups.java`;
    let javaFile = fs.readFileSync(javaFilePath, "utf-8");
    javaFile = javaFile.replace(`\n                        // END OF ${group.toUpperCase()}`, java);
    fs.writeFileSync(javaFilePath, javaFile);
}

module.exports = {
    addItemToGroup
};