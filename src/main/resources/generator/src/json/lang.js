const fs = require("fs");
const { modid } = require("../datapack_context.json");

function createTranslation(key, value) {
    const langPath = `../assets/${modid}/lang/en_us.json`;
    const lang = JSON.parse(fs.readFileSync(langPath));
    lang[key] = value;
    fs.writeFileSync(langPath, JSON.stringify(lang));
}

module.exports = {
    createTranslation
};