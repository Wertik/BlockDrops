package space.devport.wertik.blockdrops.system.struct;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.extern.java.Log;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import space.devport.utils.configuration.Configuration;
import space.devport.utils.logging.DebugLevel;
import space.devport.utils.xseries.XMaterial;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Log
public class BlockDropPreset {

    @Getter
    private final String name;

    @Getter
    private final XMaterial material;

    @Getter
    private final Set<ChancedRewards> rewards = new HashSet<>();

    public BlockDropPreset(String name, XMaterial material) {
        this.name = name;
        this.material = material;
    }

    public static BlockDropPreset load(Configuration config, String path) {
        ConfigurationSection section = config.getFileConfiguration().getConfigurationSection(path);

        if (section == null)
            return null;

        String typeString = section.getString("target");

        if (Strings.isNullOrEmpty(typeString)) {
            log.warning("No target material for " + section.getName() + " at " + config.composePath(path + ".target"));
            return null;
        }

        Optional<XMaterial> xMaterial = XMaterial.matchXMaterial(typeString.toUpperCase());

        if (!xMaterial.isPresent()) {
            log.warning("Could not parse material from " + typeString + " at " + config.composePath(path + ".target"));
            return null;
        }

        BlockDropPreset preset = new BlockDropPreset(section.getName(), xMaterial.get());

        for (String key : section.getKeys(false)) {
            ChancedRewards rewards = ChancedRewards.load(config, path + "." + key);
            if (rewards == null)
                continue;
            preset.getRewards().add(rewards);
        }
        log.log(DebugLevel.DEBUG, "Loaded preset " + preset.getName() + " with " + preset.getRewards().size() + " rewards.");
        return preset;
    }

    public void give(Player player) {
        for (ChancedRewards rewards : this.rewards) {
            rewards.give(player);
        }
    }
}
