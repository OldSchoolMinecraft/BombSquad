package net.oldschoolminecraft.bs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
        }
    }
}
