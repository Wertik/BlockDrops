package space.devport.wertik.blockdrops.listeners;

import lombok.extern.java.Log;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import space.devport.utils.DevportListener;
import space.devport.utils.logging.DebugLevel;
import space.devport.utils.utility.reflection.ServerVersion;
import space.devport.utils.xseries.XBlock;
import space.devport.utils.xseries.XMaterial;
import space.devport.wertik.blockdrops.BlockDropsPlugin;
import space.devport.wertik.blockdrops.system.struct.BlockDropPreset;

@Log
public class BlockListener extends DevportListener {

    private final BlockDropsPlugin plugin;

    public BlockListener(BlockDropsPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    private XMaterial parseMaterial(Block block) {
        try {
            return XBlock.getType(block);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent event) {

        if (event.isCancelled()) {
            log.log(DebugLevel.DEBUG, "Event cancelled on lower priority.");
            return;
        }

        Block block = event.getBlock();
        final XMaterial blockMaterial = parseMaterial(block);

        if (blockMaterial == null) {
            log.log(DebugLevel.DEBUG, "Invalid block material: " + block.getType().toString() + " data: " + block.getData());
            return;
        }

        if (!plugin.getEnabledWorlds().contains(block.getWorld().getName())) {
            log.log(DebugLevel.DEBUG, "World " + block.getWorld().getName() + " is not enabled.");
            return;
        }

        final Player player = event.getPlayer();

        final ItemStack tool = getItemInMainHand(player);

        if (tool != null &&
                plugin.getConfig().getBoolean("limitations.silk-touch", true) &&
                tool.getEnchantments().containsKey(Enchantment.SILK_TOUCH)) {
            log.log(DebugLevel.DEBUG, "Silk touch limitation.");
            return;
        }

        BlockDropPreset preset = plugin.getPresetManager().getByType(blockMaterial);

        if (preset == null) {
            log.log(DebugLevel.DEBUG, "No preset assigned to material.");
            return;
        }

        if (tool != null &&
                plugin.getConfig().getBoolean("limitations.shears", true) &&
                blockMaterial.toString().toLowerCase().contains("leaves") &&
                XMaterial.matchXMaterial(tool) == XMaterial.SHEARS) {
            if (ServerVersion.isCurrentAbove(ServerVersion.v1_8))
                event.setDropItems(false);
            else
                block.setType(Material.AIR);
        }

        log.log(DebugLevel.DEBUG, "Checks passed... running rewards.");
        preset.give(player);
    }

    @SuppressWarnings("deprecation")
    private ItemStack getItemInMainHand(Player player) {
        if (ServerVersion.isCurrentAbove(ServerVersion.v1_9)) {
            return player.getInventory().getItemInMainHand();
        } else return player.getInventory().getItemInHand();
    }
}
