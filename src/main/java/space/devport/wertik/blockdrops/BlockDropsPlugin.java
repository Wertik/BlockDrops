package space.devport.wertik.blockdrops;

import lombok.Getter;
import space.devport.utils.DevportPlugin;
import space.devport.utils.UsageFlag;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.wertik.blockdrops.listeners.BlockListener;
import space.devport.wertik.blockdrops.system.PresetManager;

import java.util.HashSet;
import java.util.Set;

public class BlockDropsPlugin extends DevportPlugin {

    @Getter
    private PresetManager presetManager;

    @Getter
    private final Set<String> enabledWorlds = new HashSet<>();

    @Getter
    private final SkyblockBridge skyblockBridge = new SkyblockBridge(this);

    @Override
    public void onPluginEnable() {
        loadOptions();

        this.presetManager = new PresetManager(this);
        presetManager.load();

        addListener(new BlockListener(this));

        buildMainCommand("blockdrops")
                .withSubCommand(buildSubCommand("reload")
                        .withDefaultDescription("Reloads the plugin.")
                        .withExecutor((sender, label, args) -> {
                            reload(sender);
                            return CommandResult.SUCCESS;
                        }));
    }

    private void loadOptions() {
        enabledWorlds.clear();
        enabledWorlds.addAll(getConfig().getStringList("enabled-worlds"));
    }

    @Override
    public void onPluginDisable() {
    }

    @Override
    public void onReload() {
        loadOptions();
        presetManager.load();
    }

    @Override
    public UsageFlag[] usageFlags() {
        return new UsageFlag[]{UsageFlag.CONFIGURATION, UsageFlag.LANGUAGE, UsageFlag.COMMANDS};
    }
}
