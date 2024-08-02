package net.oldschoolminecraft.bs;

import net.minecraft.server.EntityTNTPrimed;
import net.minecraft.server.Packet53BlockChange;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerBlockEventHandler implements Listener
{
    private static final boolean DEBUG = false;

    @EventHandler(priority = Event.Priority.Lowest)
    public void onBlockPlaced(PreBlockPlaceEvent event)
    {
        if (DEBUG) System.out.println("[BombSquad Debug] PreBlockPlaceEvent: " + net.minecraft.server.Block.byId[event.getBlockID()].material + " @ " + event.getLocation());
        if (event.getBlockID() == Material.TNT.getId())
        {
            if (!(event.getPlayer().hasPermission("bombsquad.place") || event.getPlayer().isOp()))
            {
                System.out.println("[BombSquad] Prevented TNT placement @ " + event.getLocation());
                event.getPlayer().sendMessage(ChatColor.RED + "You are not allowed to place TNT!");
                event.setCancelled(true);
//                event.setBuild(false);
                return;
            }

            Block north = getRelativeBlock(event.getLocation(), BlockFace.NORTH);
            Block east = getRelativeBlock(event.getLocation(), BlockFace.EAST);
            Block south = getRelativeBlock(event.getLocation(), BlockFace.SOUTH);
            Block west = getRelativeBlock(event.getLocation(), BlockFace.WEST);
            Block up = getRelativeBlock(event.getLocation(), BlockFace.UP);
            Block down = getRelativeBlock(event.getLocation(), BlockFace.DOWN);

            Block[] blocks = new Block[] { north, east, south, west, up, down };

            for (Block relativeBlock : blocks)
            {
//                Block relativeBlock = event.getBlock().getRelative(face);
                Material faceType = relativeBlock.getType();

                if (DEBUG)
                {
                    System.out.println("[BombSquad Debug] Relative block: " + relativeBlock);
                    System.out.println("[BombSquad Debug] Relative material: " + faceType);
                }

                if (relativeBlock.isBlockPowered() || relativeBlock.isBlockIndirectlyPowered())
                {
                    System.out.println("[BombSquad] Prevented indirect TNT redstone ignition @ " + event.getLocation());
                    event.getPlayer().sendMessage(ChatColor.RED + "You can't place TNT next to active redstone!");
                    event.setCancelled(true);
//                    event.setBuild(false);
                    return;
                }

                if (isRedstoneType(faceType))
                {
                    System.out.println("[BombSquad] Prevented TNT ignition on block placement @ " + event.getLocation());
                    event.getPlayer().sendMessage(ChatColor.RED + "You can't place TNT next to active redstone!");
                    event.setCancelled(true);
//                    event.setBuild(false);
                    return;
                }
            }
        }
    }

    private Block getRelativeBlock(Location loc, BlockFace face)
    {
        return loc.add(face.getModX(), face.getModY(), face.getModZ()).getBlock();
    }

    @EventHandler
    public void onBlockRedstone(BlockRedstoneEvent event)
    {
        if (event.getBlock().getType() == Material.TNT)
            event.setNewCurrent(0); // nope.avi
    }

    private boolean isRedstoneType(Material material)
    {
        return material == Material.REDSTONE_TORCH_ON ||
                material == Material.REDSTONE ||
                material == Material.REDSTONE_WIRE ||
                material == Material.LEVER ||
                material == Material.STONE_PLATE ||
                material == Material.WOOD_PLATE ||
                material == Material.STONE_BUTTON ||
                material == Material.DIODE ||
                material == Material.DIODE_BLOCK_ON;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_AIR)
        {
            if (event.getPlayer().getItemInHand() == null || event.getPlayer().getItemInHand().getType() != Material.FLINT_AND_STEEL)
                return;
            Block targetBlock = event.getPlayer().getTargetBlock(null, 150);
            if (targetBlock.getType() == Material.TNT)
            {
                if (!(event.getPlayer().hasPermission("bombsquad.ignite") || event.getPlayer().isOp()))
                {
                    event.getPlayer().sendMessage(ChatColor.RED + "You aren't allowed to remotely detonate TNT!");
                    event.setUseItemInHand(Event.Result.DENY);
                    event.setUseInteractedBlock(Event.Result.DENY);
                    event.setCancelled(true);
                    return;
                }

                Location tntLoc = targetBlock.getLocation();
                targetBlock.setType(Material.AIR);
                tntLoc.getWorld().spawn(tntLoc, TNTPrimed.class);
                System.out.println("[BombSquad] Player remotely ignited TNT @ " + tntLoc + " from " + event.getPlayer().getLocation());
            }
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.isBlockInHand() && event.getPlayer().getItemInHand().getType() == Material.TNT)
        {
            if (!(event.getPlayer().hasPermission("bombsquad.place") || event.getPlayer().isOp()))
            {
                event.getPlayer().sendMessage(ChatColor.RED + "You aren't allowed to place TNT!");
                event.setUseItemInHand(Event.Result.DENY);
                event.setUseInteractedBlock(Event.Result.DENY);
                event.setCancelled(true);
                return;
            }

//            Block toCheck = event.getClickedBlock().getRelative(event.getBlockFace());
            Block toCheck = event.getClickedBlock();
            for (BlockFace face : BlockFace.values())
            {
                if (isRedstoneType(toCheck.getRelative(face).getType()))
                {
                    if (!(event.getPlayer().hasPermission("bombsquad.ignite") || event.getPlayer().isOp()))
                    {
                        System.out.println("[BombSquad] Prevented TNT ignition on block placement @ " + event.getClickedBlock().getRelative(face).getLocation());
                        event.getPlayer().sendMessage(ChatColor.RED + "You can't place TNT next to active redstone!");
                        event.setUseItemInHand(Event.Result.DENY);
                        event.setUseInteractedBlock(Event.Result.DENY);
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        if (isRedstoneType(event.getBlock().getType()) && DEBUG) System.out.println("[BombSquad Debug] Redstone block break: " + event.getBlock() + " (" + event.getBlock().getType() + ")");
        if (event.getBlock().getType() != Material.TNT) return;
        if (event.getPlayer() != null && (event.getPlayer().hasPermission("bombsquad.ignite") || event.getPlayer().isOp()))
        {
            System.out.println("[BombSquad] Prevented TNT ignition @ " + event.getBlock().getLocation());
            return;
        }
        if (event.getPlayer() != null) fixBlock(event.getBlock().getLocation(), Material.AIR, event.getPlayer());
        event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.TNT, 1));
        event.setCancelled(true);
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

    @EventHandler(priority = Event.Priority.Lowest)
    public void onBlockPhysics(BlockPhysicsEvent event)
    {
//        List<Material> causes = Arrays.asList(Material.REDSTONE_WIRE, Material.DIODE_BLOCK_ON, Material.REDSTONE_TORCH_ON, Material.STONE_PLATE, Material.WOOD_PLATE, Material.LEVER, Material.STONE_BUTTON);

        if (isRedstoneType(event.getBlock().getType()) && event.getChangedType() == Material.TNT)
        {
            System.out.println("[BombSquad] Prevented TNT physics ignition @ " + event.getBlock().getLocation());
            event.setCancelled(true); // nope.avi

            if (DEBUG)
            {
                System.out.println("[BombSquad Debug] Physics block: " + event.getBlock() + " (" + event.getBlock().getType() + ")");
                System.out.println("[BombSquad Debug] Physics changed type: " + event.getChangedType());
            }
        }

        if (isRedstoneType(event.getChangedType()) && event.getBlock().getType() == Material.TNT)
        {
            System.out.println("[BombSquad] Prevented TNT physics ignition @ " + event.getBlock().getLocation());
            event.setCancelled(true); // nope.avi

            if (DEBUG)
            {
                System.out.println("[BombSquad Debug] Physics block: " + event.getBlock() + " (" + event.getBlock().getType() + ")");
                System.out.println("[BombSquad Debug] Physics changed type: " + event.getChangedType());
            }
        }
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
