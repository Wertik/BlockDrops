package space.devport.wertik.blockdrops;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.events.PluginInitializeEvent;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandPrivilege;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import lombok.Getter;
import lombok.extern.java.Log;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import space.devport.utils.DevportManager;
import space.devport.utils.utility.DependencyUtil;

@Log
public class SkyblockBridge extends DevportManager implements Listener {

    @Getter
    private boolean hooked = false;

    public SkyblockBridge(BlockDropsPlugin plugin) {
        super(plugin);

        if (DependencyUtil.isInstalled("SuperiorSkyblock2"))
            plugin.registerListener(this);
    }

    @Override
    public void afterDependencyLoad() {
        hook();
    }

    @Override
    public void afterEnable() {
        hook();
    }

    @EventHandler
    public void onInit(PluginInitializeEvent event) {
        hook();
    }

    public void hook() {
        if (hooked || !DependencyUtil.isEnabled("SuperiorSkyblock2"))
            return;

        this.hooked = true;
        HandlerList.unregisterAll(this);
        log.info("Hooked into SuperiorSkyblock2.");
    }

    public boolean canBreakOnIslandAt(Player player, Location location) {
        if (!hooked || plugin.getConfig().getBoolean("superior-skyblock-hook", true))
            return false;

        Island island = SuperiorSkyblockAPI.getIslandAt(location);
        SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(player);
        return island.hasPermission(superiorPlayer, IslandPrivilege.getByName("BREAK"));
    }
}
