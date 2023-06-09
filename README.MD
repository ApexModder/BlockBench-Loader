# BlockBench-Loader

BlockBench model loader for Forge & Fabric.

Allows loading BlockBench models (_.bbmodel_) files directly into Minecraft without needing to convert to Minecrafts builtin Block/Item Json format.

_BBLoader is fully clientside only, and should not be required server side._ 
<br>_Unless the debug data flag is enabled, then it is required server side as test blocks & items are registered._

### Features
- Quickly & Easily, Load BlockBench models directly in Minecraft without needing to convert to json.
- Load textures from BlockBench models, no PNG files required needed.

### Potential Future Features
- Load BlockBench animations.

---

**NOTE:** Highly experimental & work in progress, issues may arise, not recommended to be used.

### How it works

The backend system differs slightly depending on what platform is currently in use.

**Forge**, We make use of their Geometry loading system to load the BBModel model data and bake it into vanillas BakedModel system.

**Fabric**, We make use of their ModelResourceProvider system to read the model json files and look for the Forge geometry loader tags, if they exist and set to our values, we load a matching BBModel.

Both platforms register a new SpriteSourceType, which allows us to load the Base64 (_B64Texture_) encoded image data that BlockBench stores for all their textures.
<br>If we detect any B64Texture, we load them up and register them to the game using the following as their texture key
`<namespace>:<folder>/builtin/<path>`.<br>
Set this in your BlockBench texture properties to use the B64Texture rather than a PNG file in the textures folder. 

We have included a simple TestMod which makes use of this loader to load 2 BBModels `plush` & `mushroom`, these are provided as examples only.
<br>_See `TestMod/Common` resources for model json examples._

## Setup
To load a BBModel, you must add the following tags to your models json file. These tell the game to use our model loader & which BBModel to load.
```json5
{
    // ...
    // must be this exact value
    "loader": "bbloader:geometry",
    // similar to other model paths, but expands to the following
    // assets/<modid>/models/bbmodel/<model_path>.bbmodel
    "bbmodel": "<modid>:<model_path>"
    // ...
}
```

### Gradle setup
First add the following to your repositories block
```groovy
repositories {
    // ...
    maven { url 'https://maven.covers1624.net/' }
    // ...
}
```

Next add the following dependencies, depending on your project setup & desired platform,
[See here for all available versions & artifacts](https://nexus.covers1624.net/#browse/search=keyword%3Dxyz.apex.minecraft.bbloader)

_Optional dependencies are for if you want access to the API at compile time, which is not required_

```groovy
dependencies {
    // VanillaGradle
    /* Optional */ compileOnly "xyz.apex.minecraft.bbloader:bbloader-common:${BBLOADER_VERSION}+${MINECRAFT_VERSION}:api"
    
    // Fabric / Quilt
    /* Optional */ modCompileOnly "xyz.apex.minecraft.bbloader:bbloader-fabric:${BBLOADER_VERSION}+${MINECRAFT_VERSION}:api"
    modRuntimeOnly "xyz.apex.minecraft.bbloader:bbloader-fabric:${BBLOADER_VERSION}+${MINECRAFT_VERSION}"
    include "xyz.apex.minecraft.bbloader:bbloader-fabric:${BBLOADER_VERSION}+${MINECRAFT_VERSION}"
    
    // ForgeGradle
    /* Optional */ compileOnly fg.deobf("xyz.apex.minecraft.bbloader:bbloader-forge:${BBLOADER_VERSION}+${MINECRAFT_VERSION}:api")
    runtimeOnly fg.deobf("xyz.apex.minecraft.bbloader:bbloader-forge:${BBLOADER_VERSION}+${MINECRAFT_VERSION}")
    // ensure jarJar is setup correctly in your project | https://forge.gemwire.uk/wiki/Jar-in-jar
    jarJar("xyz.apex.minecraft.bbloader:bbloader-forge:[${BBLOADER_VERSION_MIN},${BBLOADER_VERSION_MAX})")
    
    // Architectury-Common
    /* Optional */ modCompileOnly "xyz.apex.minecraft.bbloader:bbloader-common:${BBLOADER_VERSION}+${MINECRAFT_VERSION}:api"
    modRuntimeOnly "xyz.apex.minecraft.bbloader:bbloader-common:${BBLOADER_VERSION}+${MINECRAFT_VERSION}"
    
    // Architectury-Fabric / Architectury-Quilt
    /* Optional */ modCompileOnly "xyz.apex.minecraft.bbloader:bbloader-fabric:${BBLOADER_VERSION}+${MINECRAFT_VERSION}:api"
    modRuntimeOnly "xyz.apex.minecraft.bbloader:bbloader-fabric:${BBLOADER_VERSION}+${MINECRAFT_VERSION}"
    include "xyz.apex.minecraft.bbloader:bbloader-fabric:${BBLOADER_VERSION}+${MINECRAFT_VERSION}"
    
    // Architectury-Forge
    /* Optional */ modCompileOnly "xyz.apex.minecraft.bbloader:bbloader-forge:${BBLOADER_VERSION}+${MINECRAFT_VERSION}:api"
    modRuntimeOnly "xyz.apex.minecraft.bbloader:bbloader-forge:${BBLOADER_VERSION}+${MINECRAFT_VERSION}"
    include "xyz.apex.minecraft.bbloader:bbloader-forge:${BBLOADER_VERSION}+${MINECRAFT_VERSION}"
}
```
