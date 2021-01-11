package space.devport.wertik.blockdrops.system;

import lombok.extern.java.Log;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.configuration.Configuration;
import space.devport.utils.xseries.XMaterial;
import space.devport.wertik.blockdrops.BlockDropsPlugin;
import space.devport.wertik.blockdrops.system.struct.BlockDropPreset;

import java.util.HashMap;
import java.util.Map;

@Log
public class PresetManager {

    private final Map<String, BlockDropPreset> loadedPresets = new HashMap<>();

    private final Configuration config;

    public PresetManager(BlockDropsPlugin plugin) {
        this.config = new Configuration(plugin, "presets.yml");
    }

    public BlockDropPreset get(String name) {
        return loadedPresets.get(name);
    }

    @Nullable
    public BlockDropPreset getByType(XMaterial material) {
        for (BlockDropPreset preset : loadedPresets.values()) {
            if (preset.getMaterial() == material)
                return preset;
        }
        return null;
    }

    public void load() {
        if (!config.load()) {
            log.warning("Could not load presets.yml, cannot load presets.");
            return;
        }

        loadedPresets.clear();

        for (String key : config.getFileConfiguration().getKeys(false)) {
            BlockDropPreset preset = BlockDropPreset.load(config, key);
            if (preset == null)
                continue;
            loadedPresets.put(key, preset);
        }
        log.info(String.format("Loaded %d block drop preset(s)...", loadedPresets.size()));
    }
}
