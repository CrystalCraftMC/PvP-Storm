/*
 * Copyright
 */
package com.crystalcraftmc.pvpstorm;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class PvPStorm extends JavaPlugin {
    public void onEnable() {
        getLogger().info(ChatColor.AQUA + "PvP Storm has been initialized!");
    }

    public void onDisable() {
        getLogger().info(ChatColor.RED + "PvP Storm has been stopped by the server.");
    }

    // TODO Write methods as described from Trello. Update on Slack as necessary!
}