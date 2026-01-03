package net.oldschoolminecraft.bs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;

public class ExplosionHandler implements Listener
{
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        if (event.getPlayer().getItemInHand().getType() == Material.FLINT_AND_STEEL &&
            event.getBlock().getType() == Material.TNT)
        {
            int ptHoursRequired = (int) BombSquad.getInstance().getConfig().getConfigOption("playTimeHoursRequired");
            int ptHours = (int) PTUtil.getPlaytimeHours(event.getPlayer().getName());

            if (event.getPlayer() != null && ptHours < ptHoursRequired)
            {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "You do not have permission to ignite TNT!");
                System.out.println("[BombSquad] TNT ignition blocked @ " + event.getBlock().getLocation());
                System.out.println("[BombSquad] Player: " + event.getPlayer().getName());
                System.out.println("[BombSquad] Playtime Hours: " + ptHours + " (required: " + ptHoursRequired + ")");
            }
        }
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event)
    {
        boolean isTNT = event.getBlock().getType() == Material.TNT;

        if (isTNT)
        {
            System.out.println("[BombSquad] TNT ignition blocked @ " + event.getBlock().getLocation());
            System.out.println("[BombSquad] Ignition cause: " + event.getCause());
            if (event.getPlayer() != null)
                System.out.println("[BombSquad] Player: " + event.getPlayer().getName());
            else System.out.println("[BombSquad] EVENT PLAYER HANDLE IS NULL");

            event.setCancelled(true);

            for (Player player : Bukkit.getOnlinePlayers())
            {
                if (player.getLocation().distanceSquared(event.getBlock().getLocation()) < 100)
                {
                    // send block update in a 3x3 range with real block types.
                    // the goal is to make sure ghost blocks disappear for clients near the TNT.

                    int baseX = event.getBlock().getX();
                    int baseY = event.getBlock().getY();
                    int baseZ = event.getBlock().getZ();

                    // 3x3 area centered on the TNT block
                    for (int dx = -1; dx <= 1; dx++)
                    {
                        for (int dz = -1; dz <= 1; dz++)
                        {
                            int x = baseX + dx;
                            int y = baseY;
                            int z = baseZ + dz;

                            Block realBlock = event.getBlock().getWorld().getBlockAt(x, y, z);
                            player.sendBlockChange(realBlock.getLocation(), realBlock.getType(), realBlock.getData());
                        }
                    }
                }
            }
        }
    }
}
