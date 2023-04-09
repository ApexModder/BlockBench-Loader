# BlockBench-Loader

BlockBench model loader for Forge & Fabric.

Allows loading BlockBench models (_.bbmodel_) files directly into Minecraft without needing to convert to Minecrafts builtin Block/Item Json format.

**NOTE:** Highly experimental & work in progress, issues may arise, not recommended to be used.

Loading a BBModel is done directly in the regular model json files
```json5
{
    // ...
    // must be this exact file
    "loader": "bbloader:geometry",
    // similar to other model paths, but expands to the following
    // assets/<modid>/models/bbmodel/<model_path>.bbmodel
    "bbmodel": "<modid>:<model_path>"
    // ...
}
```
