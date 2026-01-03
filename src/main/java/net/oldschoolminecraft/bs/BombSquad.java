package net.oldschoolminecraft.bs;

import org.bukkit.*;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class BombSquad extends JavaPlugin
{
    private BSConfig config;

    public void onEnable()
    {
        instance = this;
        config = new BSConfig(new File(getDataFolder(), "config.yml"));
        getServer().getPluginManager().registerEvents(new ExplosionHandler(), this);

        System.out.println("BombSquad enabled");
    }

    public BSConfig getConfig()
    {
        return config;
    }

    public void onDisable()
    {
        System.out.println("BombSquad disabled");
    }

    private static BombSquad instance;

    public static BombSquad getInstance()
    {
        return instance;
    }
}
