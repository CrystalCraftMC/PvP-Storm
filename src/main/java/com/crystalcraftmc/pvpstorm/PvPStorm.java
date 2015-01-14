/*
 * Copyright 2015 CrystalCraftMC
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.crystalcraftmc.pvpstorm;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PvPStorm extends JavaPlugin {
    // TODO Implement mob boss health bar - perhaps in listener? NEEDS RESEARCH.
    
    public void onEnable() {
        getLogger().info(ChatColor.AQUA + "PvP Storm v0.0.1 has been initialized!");
        getServer().getPluginManager().registerEvents(gameStart, this);
        getServer().getPluginManager().registerEvents(gameEnd, this);
    }

    public void onDisable() {
        getLogger().info(ChatColor.RED + "PvP Storm has been stopped by the server.");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;
        World world = p.getWorld();

        if (cmd.getName().equalsIgnoreCase("storm")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("This command can only be run by a player.");
                return false;
            } else if (args.length == 1) {
                return false;
            } else if (args.length > 1) {
                if (args[0].equalsIgnoreCase("start")) {
                    Bukkit.broadcastMessage(ChatColor.DARK_RED + getConfig().getString("start-message"));
                    world.setStorm(true);
                    this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                        @Override
                        public void run() {
                            Bukkit.broadcastMessage(ChatColor.DARK_RED + "The PvP Storm is now hitting the Arena!")
                        }
                    }, 12000L); // 12000L == 10 minutes, 60L == 3 seconds, 20L == 1 second (it's the # of ticks)
                    // TODO Alert the listener to begin counting who hits the Stormer, in order to give prizes at end
                    return true;
                } else if (args[0].equalsIgnoreCase("stop")) {
                    Bukkit.broadcastMessage(ChatColor.AQUA + getConfig().getString("end-message"));
                    world.setStorm(false);
                    world.setThundering(true);
                    world.setThunderDuration(5000);
                    // TODO Alert listener to stop counting and give out awards
                    return true;
                } else if (args[0].equalsIgnoreCase("power")) {
                    // TODO Block console from running these commands!
                    if (args.length < 2) {
                        // TODO Output a list of all possible powers to the user
                        return true;
                    } else if (args[1].equalsIgnoreCase("flare")) {
                        Bukkit.broadcastMessage("[INSERT PLAYER NAME VARIABLE] " + ChatColor.YELLOW + " used " + ChatColor.GOLD + " FLARE " + ChatColor.YELLOW + " ability!");
                        // TODO Damage players in radius, temporarily lower health
                        return true;
                    } else if (args[1].equalsIgnoreCase("vanish")) {
                        Bukkit.broadcastMessage("[INSERT PLAYER NAME VARIABLE] " + ChatColor.YELLOW + " used " + ChatColor.GRAY + " VANISH " + ChatColor.YELLOW + " ability!");
                        // TODO Make user invisible at expense of health
                        return true;
                    } else if (args[1].equalsIgnoreCase("timewarp")) {
                        Bukkit.broadcastMessage("[INSERT PLAYER NAME VARIABLE] " + ChatColor.YELLOW + " used " + ChatColor.RED + " TIMEWARP " + ChatColor.YELLOW + " ability!");
                        // TODO Instantly move the user backwards about 10 blocks, but add slowness for a few seconds
                        return true;
                    }
                } else {
                    return false;
                }
            }
        }
        return false;
    }
    
    private final GameStartListener gameStart = new GameStartListener(this);
    private final GameEndListener gameEnd = new GameEndListener(this);
}