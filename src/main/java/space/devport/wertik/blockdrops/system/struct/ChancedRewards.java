package space.devport.wertik.blockdrops.system.struct;

import lombok.Getter;
import lombok.extern.java.Log;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import space.devport.utils.configuration.Configuration;
import space.devport.utils.logging.DebugLevel;
import space.devport.utils.struct.Rewards;

import java.util.Random;

@Log
public class ChancedRewards {

    private final Random random = new Random();

    @Getter
    private final String name;
    @Getter
    private final Rewards rewards;
    @Getter
    private final double chance;

    public ChancedRewards(String name, Rewards rewards, double chance) {
        this.name = name;
        this.rewards = rewards;
        this.chance = chance;
    }

    public void give(Player player) {
        double attempt = random.nextDouble();
        if (attempt < chance) {
            rewards.give(player);
            log.log(DebugLevel.DEBUG, "Player received rewards with attempt/chance of " + attempt + "/" + chance);
        }
    }

    public static ChancedRewards load(Configuration config, String path) {
        ConfigurationSection section = config.getFileConfiguration().getConfigurationSection(path);

        if (section == null)
            return null;

        Rewards rewards = config.getRewards(path);
        double chance = section.getDouble("chance", 1D);

        return new ChancedRewards(section.getName(), rewards, chance);
    }
}
