package net.oldschoolminecraft.bs;

import net.minecraft.server.Packet53BlockChange;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerBlockEventHandler implements Listener
{
    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent event)
    {
        if (event.getBlockPlaced().getType() == Material.TNT)
        {
            if (!(event.getPlayer().hasPermission("bombsquad.place") || event.getPlayer().isOp()))
            {
                event.getPlayer().sendMessage(ChatColor.RED + "You are not allowed to place TNT!");
                event.setCancelled(true);
                event.setBuild(false);
            }
        }
    }

    @EventHandler
    public void onBlockRedstone(BlockRedstoneEvent event)
    {
        if (event.getBlock().getType() == Material.TNT && event.getNewCurrent() > 0)
            event.setNewCurrent(0); // nope.avi
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        if (event.getBlock().getType() != Material.TNT) return;
        if (!(event.getPlayer().hasPermission("bombsquad.ignite") || event.getPlayer().isOp()))
        {
            fixBlock(event.getBlock().getLocation(), Material.AIR, event.getPlayer());
            event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.TNT, 1));
            event.setCancelled(true);
        }
    }

    private void fixBlock(Location loc, Material material, Player player)
    {
        player.getWorld().getBlockAt(loc).setType(material);
        ((CraftPlayer) player).getHandle().netServerHandler.sendPacket(new Packet53BlockChange(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), ((CraftWorld)player.getWorld()).getHandle()));
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event)
    {
        if (event.getBlock().getType() == Material.TNT)
            event.setCancelled(true); // nope.avi
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event)
    {
        List<Material> causes = Arrays.asList(Material.REDSTONE_WIRE, Material.DIODE_BLOCK_ON, Material.REDSTONE_TORCH_ON, Material.STONE_PLATE, Material.WOOD_PLATE);
        if (causes.contains(event.getChangedType()) && event.getBlock().getType() == Material.TNT)
            event.setCancelled(true); // nope.avi
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event)
    {
        if (event.getBlock().getType() == Material.TNT && event.getSource().getType() == Material.FIRE)
        {
            event.getSource().setType(Material.AIR); // nope.avi
            event.setCancelled(true);
        }
    }
}
