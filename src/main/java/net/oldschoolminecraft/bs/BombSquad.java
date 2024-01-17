package net.oldschoolminecraft.bs;

import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

public class BombSquad extends JavaPlugin
{
    public void onEnable()
    {
        getServer().getPluginManager().registerSuperEvents(new PlayerBlockEventHandler(), this);

        System.out.println("BombSquad enabled");
    }

    public void onDisable()
    {
        System.out.println("BombSquad disabled");
    }
}
