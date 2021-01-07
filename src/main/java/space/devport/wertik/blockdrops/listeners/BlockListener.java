package space.devport.wertik.blockdrops.listeners;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import space.devport.utils.utility.reflection.ServerVersion;
import space.devport.utils.xseries.XBlock;
import space.devport.utils.xseries.XMaterial;
import space.devport.wertik.blockdrops.BlockDropsPlugin;
import space.devport.wertik.blockdrops.system.struct.BlockDropPreset;

public class BlockListener implements Listener {

    private final BlockDropsPlugin plugin;

    public BlockListener(BlockDropsPlugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        final XMaterial xMaterial = XBlock.getType(block);

        if (!plugin.getEnabledWorlds().contains(block.getWorld().getName()))
            return;

        final Player player = event.getPlayer();
        final ItemStack tool = getItemInMainHand(player);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (tool != null && tool.getEnchantments().containsKey(Enchantment.SILK_TOUCH)) {
                return;
            }

            BlockDropPreset preset = plugin.getPresetManager().getByType(xMaterial);

            if (preset == null)
                return;

            preset.give(player);
        });
    }

    @SuppressWarnings("deprecation")
    private ItemStack getItemInMainHand(Player player) {
        if (ServerVersion.isCurrentAbove(ServerVersion.v1_9)) {
            return player.getInventory().getItemInMainHand();
        } else return player.getInventory().getItemInHand();
    }
}
