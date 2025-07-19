const fs = require("fs");
const { modid } = require("../datapack_context.json");
const { createItem } = require("./item");
const { createDropsSelf } = require("./loot_table");

function createBlock(name, type, parent, drops, stonecutting) {
    createItem(name, "block_item", { type, parent }, stonecutting);
    
    if (drops) {
        createDropsSelf(name);
    }

    let blockstates = '';
    const models = {};

    if (type === "block") {
        blockstates = 
`{
  "variants": {
    "": {
      "model": "${modid}:block/${name}"
    }
  }
}`;
    }
    if (type === "cube_all") {
        blockstates = 
`{
  "variants": {
    "": {
      "model": "${modid}:block/${name}"
    }
  }
}`;
        models[""] = 
`{
  "parent": "minecraft:block/cube_all",
  "textures": {
    "all": "${modid}:block/${name}"
  }
}`;
    }
    if (type === "cross") {
        blockstates = 
`{
  "variants": {
    "": {
      "model": "${modid}:block/${name}"
    }
  }
}`;
        models[""] = 
`{
  "parent": "minecraft:block/cross",
  "textures": {
    "all": "${modid}:block/${name}"
  }
}`;
    }
    if (type === "slab") {
        blockstates = 
`{
  "variants": {
    "type=bottom": {
      "model": "${modid}:block/${name}"
    },
    "type=double": {
      "model": "${parent}"
    },
    "type=top": {
      "model": "${modid}:block/${name}_top"
    }
  }
}`;
        models[""] = 
`{
  "parent": "minecraft:block/slab",
  "textures": {
    "bottom": "${parent}",
    "side": "${parent}",
    "top": "${parent}"
  }
}`;
        models["top"] = 
`{
  "parent": "minecraft:block/slab_top",
  "textures": {
    "bottom": "${parent}",
    "side": "${parent}",
    "top": "${parent}"
  }
}`;
    }
    if (type === "stairs") {
        blockstates = 
`{
  "variants": {
    "facing=east,half=bottom,shape=inner_left": {
      "model": "${modid}:block/${name}_inner",
      "uvlock": true,
      "y": 270
    },
    "facing=east,half=bottom,shape=inner_right": {
      "model": "${modid}:block/${name}_inner"
    },
    "facing=east,half=bottom,shape=outer_left": {
      "model": "${modid}:block/${name}_outer",
      "uvlock": true,
      "y": 270
    },
    "facing=east,half=bottom,shape=outer_right": {
      "model": "${modid}:block/${name}_outer"
    },
    "facing=east,half=bottom,shape=straight": {
      "model": "${modid}:block/${name}"
    },
    "facing=east,half=top,shape=inner_left": {
      "model": "${modid}:block/${name}_inner",
      "uvlock": true,
      "x": 180
    },
    "facing=east,half=top,shape=inner_right": {
      "model": "${modid}:block/${name}_inner",
      "uvlock": true,
      "x": 180,
      "y": 90
    },
    "facing=east,half=top,shape=outer_left": {
      "model": "${modid}:block/${name}_outer",
      "uvlock": true,
      "x": 180
    },
    "facing=east,half=top,shape=outer_right": {
      "model": "${modid}:block/${name}_outer",
      "uvlock": true,
      "x": 180,
      "y": 90
    },
    "facing=east,half=top,shape=straight": {
      "model": "${modid}:block/${name}",
      "uvlock": true,
      "x": 180
    },
    "facing=north,half=bottom,shape=inner_left": {
      "model": "${modid}:block/${name}_inner",
      "uvlock": true,
      "y": 180
    },
    "facing=north,half=bottom,shape=inner_right": {
      "model": "${modid}:block/${name}_inner",
      "uvlock": true,
      "y": 270
    },
    "facing=north,half=bottom,shape=outer_left": {
      "model": "${modid}:block/${name}_outer",
      "uvlock": true,
      "y": 180
    },
    "facing=north,half=bottom,shape=outer_right": {
      "model": "${modid}:block/${name}_outer",
      "uvlock": true,
      "y": 270
    },
    "facing=north,half=bottom,shape=straight": {
      "model": "${modid}:block/${name}",
      "uvlock": true,
      "y": 270
    },
    "facing=north,half=top,shape=inner_left": {
      "model": "${modid}:block/${name}_inner",
      "uvlock": true,
      "x": 180,
      "y": 270
    },
    "facing=north,half=top,shape=inner_right": {
      "model": "${modid}:block/${name}_inner",
      "uvlock": true,
      "x": 180
    },
    "facing=north,half=top,shape=outer_left": {
      "model": "${modid}:block/${name}_outer",
      "uvlock": true,
      "x": 180,
      "y": 270
    },
    "facing=north,half=top,shape=outer_right": {
      "model": "${modid}:block/${name}_outer",
      "uvlock": true,
      "x": 180
    },
    "facing=north,half=top,shape=straight": {
      "model": "${modid}:block/${name}",
      "uvlock": true,
      "x": 180,
      "y": 270
    },
    "facing=south,half=bottom,shape=inner_left": {
      "model": "${modid}:block/${name}_inner"
    },
    "facing=south,half=bottom,shape=inner_right": {
      "model": "${modid}:block/${name}_inner",
      "uvlock": true,
      "y": 90
    },
    "facing=south,half=bottom,shape=outer_left": {
      "model": "${modid}:block/${name}_outer"
    },
    "facing=south,half=bottom,shape=outer_right": {
      "model": "${modid}:block/${name}_outer",
      "uvlock": true,
      "y": 90
    },
    "facing=south,half=bottom,shape=straight": {
      "model": "${modid}:block/${name}",
      "uvlock": true,
      "y": 90
    },
    "facing=south,half=top,shape=inner_left": {
      "model": "${modid}:block/${name}_inner",
      "uvlock": true,
      "x": 180,
      "y": 90
    },
    "facing=south,half=top,shape=inner_right": {
      "model": "${modid}:block/${name}_inner",
      "uvlock": true,
      "x": 180,
      "y": 180
    },
    "facing=south,half=top,shape=outer_left": {
      "model": "${modid}:block/${name}_outer",
      "uvlock": true,
      "x": 180,
      "y": 90
    },
    "facing=south,half=top,shape=outer_right": {
      "model": "${modid}:block/${name}_outer",
      "uvlock": true,
      "x": 180,
      "y": 180
    },
    "facing=south,half=top,shape=straight": {
      "model": "${modid}:block/${name}",
      "uvlock": true,
      "x": 180,
      "y": 90
    },
    "facing=west,half=bottom,shape=inner_left": {
      "model": "${modid}:block/${name}_inner",
      "uvlock": true,
      "y": 90
    },
    "facing=west,half=bottom,shape=inner_right": {
      "model": "${modid}:block/${name}_inner",
      "uvlock": true,
      "y": 180
    },
    "facing=west,half=bottom,shape=outer_left": {
      "model": "${modid}:block/${name}_outer",
      "uvlock": true,
      "y": 90
    },
    "facing=west,half=bottom,shape=outer_right": {
      "model": "${modid}:block/${name}_outer",
      "uvlock": true,
      "y": 180
    },
    "facing=west,half=bottom,shape=straight": {
      "model": "${modid}:block/${name}",
      "uvlock": true,
      "y": 180
    },
    "facing=west,half=top,shape=inner_left": {
      "model": "${modid}:block/${name}_inner",
      "uvlock": true,
      "x": 180,
      "y": 180
    },
    "facing=west,half=top,shape=inner_right": {
      "model": "${modid}:block/${name}_inner",
      "uvlock": true,
      "x": 180,
      "y": 270
    },
    "facing=west,half=top,shape=outer_left": {
      "model": "${modid}:block/${name}_outer",
      "uvlock": true,
      "x": 180,
      "y": 180
    },
    "facing=west,half=top,shape=outer_right": {
      "model": "${modid}:block/${name}_outer",
      "uvlock": true,
      "x": 180,
      "y": 270
    },
    "facing=west,half=top,shape=straight": {
      "model": "${modid}:block/${name}",
      "uvlock": true,
      "x": 180,
      "y": 180
    }
  }
}`;
        models[""] = 
`{
  "parent": "minecraft:block/stairs",
  "textures": {
    "bottom": "${parent}",
    "side": "${parent}",
    "top": "${parent}"
  }
}`;
        models["inner"] = 
`{
  "parent": "minecraft:block/inner_stairs",
  "textures": {
    "bottom": "${parent}",
    "side": "${parent}",
    "top": "${parent}"
  }
}`;
        models["outer"] = 
`{
  "parent": "minecraft:block/outer_stairs",
  "textures": {
    "bottom": "${parent}",
    "side": "${parent}",
    "top": "${parent}"
  }
}`;
    }
    if (type === "fence") {
        models["post"] = 
`{
  "parent": "minecraft:block/fence_post",
  "textures": {
    "texture": "${parent}"
  }
}`;
        models["side"] = 
`{
  "parent": "minecraft:block/fence_side",
  "textures": {
    "texture": "${parent}"
  }
}`;
        models["inventory"] = 
`{
  "parent": "minecraft:block/fence_inventory",
  "textures": {
    "texture": "${parent}"
  }
}`;
    }
    if (type === "fence_gate") {
        models[""] = 
`{
  "parent": "minecraft:block/template_fence_gate",
  "textures": {
    "texture": "${parent}"
  }
}`;
        models["open"] = 
`{
  "parent": "minecraft:block/template_fence_gate_open",
  "textures": {
    "texture": "${parent}"
  }
}`;
        models["wall"] = 
`{
  "parent": "minecraft:block/template_fence_gate_wall",
  "textures": {
    "texture": "${parent}"
  }
}`;
        models["wall_open"] = 
`{
  "parent": "minecraft:block/template_fence_gate_wall_open",
  "textures": {
    "texture": "${parent}"
  }
}`;
    }
    if (type === "wall") {
        models["post"] = 
`{
  "parent": "minecraft:block/template_wall_post",
  "textures": {
    "wall": "${parent}"
  }
}`;
        models["side"] = 
`{
  "parent": "minecraft:block/template_wall_side",
  "textures": {
    "wall": "${parent}"
  }
}`;
        models["side_tall"] = 
`{
  "parent": "minecraft:block/template_wall_side_tall",
  "textures": {
    "wall": "${parent}"
  }
}`;
        models["inventory"] = 
`{
  "parent": "minecraft:block/wall_inventory",
  "textures": {
    "wall": "minecraft:block/cobblestone"
  }
}`;
    }
    if (type === "door") {
        models["bottom_left"] = 
`{
  "parent": "minecraft:block/door_bottom_left",
  "textures": {
    "bottom": "${modid}:block/${name}_bottom",
    "top": "${modid}:block/${name}_top"
  }
}`;
        models["bottom_left_open"] = 
`{
  "parent": "minecraft:block/door_bottom_left_open",
  "textures": {
    "bottom": "${modid}:block/${name}_bottom",
    "top": "${modid}:block/${name}_top"
  }
}`;
        models["bottom_right"] = 
`{
  "parent": "minecraft:block/door_bottom_right",
  "textures": {
    "bottom": "${modid}:block/${name}_bottom",
    "top": "${modid}:block/${name}_top"
  }
}`;
        models["bottom_right_open"] = 
`{
  "parent": "minecraft:block/door_bottom_right_open",
  "textures": {
    "bottom": "${modid}:block/${name}_bottom",
    "top": "${modid}:block/${name}_top"
  }
}`;
        models["top_left"] = 
`{
  "parent": "minecraft:block/door_top_left",
  "textures": {
    "bottom": "${modid}:block/${name}_bottom",
    "top": "${modid}:block/${name}_top"
  }
}`;
        models["top_left_open"] = 
`{
  "parent": "minecraft:block/door_top_left_open",
  "textures": {
    "bottom": "${modid}:block/${name}_bottom",
    "top": "${modid}:block/${name}_top"
  }
}`;
        models["top_right"] = 
`{
  "parent": "minecraft:block/door_top_right",
  "textures": {
    "bottom": "${modid}:block/${name}_bottom",
    "top": "${modid}:block/${name}_top"
  }
}`;
        models["top_right_open"] = 
`{
  "parent": "minecraft:block/door_top_right_open",
  "textures": {
    "bottom": "${modid}:block/${name}_bottom",
    "top": "${modid}:block/${name}_top"
  }
}`;
    }
    if (type === "trapdoor") {
        models["bottom"] = 
`{
  "parent": "minecraft:block/template_trapdoor_bottom",
  "textures": {
    "texture": "${modid}:block/${name}"
  }
}`;
        models["top"] = 
`{
  "parent": "minecraft:block/template_trapdoor_top",
  "textures": {
    "texture": "${modid}:block/${name}"
  }
}`;
        models["open"] = 
`{
  "parent": "minecraft:block/template_trapdoor_open",
  "textures": {
    "texture": "${modid}:block/${name}"
  }
}`;
    }
    if (type === "pressure_plate") {
        models[""] = 
`{
  "parent": "minecraft:block/pressure_plate_up",
  "textures": {
    "texture": "${parent}"
  }
}`;
        models["down"] = 
`{
  "parent": "minecraft:block/pressure_plate_down",
  "textures": {
    "texture": "${parent}"
  }
}`;
    }
    if (type === "button") {
        models[""] = 
`{
  "parent": "minecraft:block/button",
  "textures": {
    "texture": "${parent}"
  }
}`;
        models["pressed"] = 
`{
  "parent": "minecraft:block/button_pressed",
  "textures": {
    "texture": "${parent}"
  }
}`;
        models["inventory"] = 
`{
  "parent": "minecraft:block/button_inventory",
  "textures": {
    "texture": "${parent}"
  }
}`;
    }

    const blockstatesPath = `../assets/${modid}/blockstates/${name}`;
    fs.writeFileSync(blockstatesPath, blockstates);
    for (const [state, model] of Object.entries(models)) {
        const modelPath = `../assets/${modid}/models/block/${name}${state ? "_" + state : ""}`;
        fs.writeFileSync(modelPath, model);
    }
}

module.exports = {
    createBlock
};
