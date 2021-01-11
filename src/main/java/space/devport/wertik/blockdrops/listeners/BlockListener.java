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
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent event) {

        if (event.isCancelled())
            return;

        Block block = event.getBlock();
        final XMaterial blockMaterial = XBlock.getType(block);

        if (blockMaterial == null || !plugin.getEnabledWorlds().contains(block.getWorld().getName()))
            return;

        final Player player = event.getPlayer();

        if (!plugin.getSkyblockBridge().canBreakOnIslandAt(player, block.getLocation()))
            return;

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
