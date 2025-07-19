const fs = require("fs");
const { modid } = require("../datapack_context.json");

function createDropsSelf(blockName) {
    const lootTablePath = `../data/${modid}/loot_tables/${blockName}.json`;
    fs.writeFileSync(lootTablePath, `{
  "type": "minecraft:block",
  "pools": [
    {
      "bonus_rolls": 0,
      "conditions": [
        {
          "condition": "minecraft:survives_explosion"
        }
      ],
      "entries": [
        {
          "type": "minecraft:item",
          "name": "${modid}:${blockName}"
        }
      ],
      "rolls": 1
    }
  ],
  "random_sequence": "${modid}:blocks/${blockName}"
}`);
}

module.exports = {
    createDropsSelf
};
