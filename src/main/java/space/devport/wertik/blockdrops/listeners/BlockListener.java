package space.devport.wertik.blockdrops.listeners;

import lombok.extern.java.Log;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import space.devport.utils.utility.reflection.ServerVersion;
import space.devport.utils.xseries.XBlock;
import space.devport.utils.xseries.XMaterial;
import space.devport.wertik.blockdrops.BlockDropsPlugin;
import space.devport.wertik.blockdrops.system.struct.BlockDropPreset;

@Log
public class BlockListener implements Listener {

    private final BlockDropsPlugin plugin;

    public BlockListener(BlockDropsPlugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        final XMaterial blockMaterial = XBlock.getType(block);

        if (!plugin.getEnabledWorlds().contains(block.getWorld().getName()))
            return;

        final Player player = event.getPlayer();
        final ItemStack tool = getItemInMainHand(player);

        if (tool != null &&
                plugin.getConfig().getBoolean("limitations.silk-touch", true) &&
                tool.getEnchantments().containsKey(Enchantment.SILK_TOUCH)) {
            return;
        }

        BlockDropPreset preset = plugin.getPresetManager().getByType(blockMaterial);

        if (preset == null)
            return;

        if (tool != null &&
                plugin.getConfig().getBoolean("limitations.shears", true) &&
                blockMaterial.toString().toLowerCase().contains("leaves") &&
                XMaterial.matchXMaterial(tool) == XMaterial.SHEARS) {
            if (ServerVersion.isCurrentAbove(ServerVersion.v1_8))
                event.setDropItems(false);
            else
                block.setType(Material.AIR);
        }

        preset.give(player);
    }

    @SuppressWarnings("deprecation")
    private ItemStack getItemInMainHand(Player player) {
        if (ServerVersion.isCurrentAbove(ServerVersion.v1_9)) {
            return player.getInventory().getItemInMainHand();
        } else return player.getInventory().getItemInHand();
    }
}
