/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 CrystalCraftMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.crystalcraftmc.pvpstorm;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;

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
        Location loc = p.getLocation();

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
                            Bukkit.broadcastMessage(ChatColor.DARK_RED + "The PvP Storm is now hitting the Arena!");
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
                    if (args.length < 2) {
                        sender.sendMessage(ChatColor.YELLOW + "The available Storm powers are:" +
                                ChatColor.RED + "/storm power flare" +
                                ChatColor.GRAY + "/storm power vanish" +
                                ChatColor.AQUA + "/storm power timewarp");
                        return true;
                    } else if (args[1].equalsIgnoreCase("flare")) {
                        Bukkit.broadcastMessage("[INSERT PLAYER NAME VARIABLE] " + ChatColor.YELLOW + " used " + ChatColor.GOLD + " FLARE " + ChatColor.YELLOW + " ability!");
                        loc = p.getLocation();
                        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
                        for (Player nearbyPlayer : players) {
                            if (nearbyPlayer.getLocation().distanceSquared(loc) <= 25) nearbyPlayer.setHealth(nearbyPlayer.getHealth() - 2.0);
                        }
                        p.setHealth(p.getHealth() - 3.0);
                        return true;
                    } else if (args[1].equalsIgnoreCase("vanish")) {
                        Bukkit.broadcastMessage("[INSERT PLAYER NAME VARIABLE] " + ChatColor.YELLOW + " used " + ChatColor.GRAY + " VANISH " + ChatColor.YELLOW + " ability!");
                        // TODO Make user invisible at expense of health
                        return true;
                    } else if (args[1].equalsIgnoreCase("timewarp")) {
                        Bukkit.broadcastMessage("[INSERT PLAYER NAME VARIABLE] " + ChatColor.YELLOW + " used " + ChatColor.RED + " TIMEWARP " + ChatColor.YELLOW + " ability!");
                        // TODO Instantly move the user backwards about 10 blocks, but add slowness for a few seconds
                        loc = p.getLocation();
                        loc.setX(loc.getX() - 10);
                        loc.setY(loc.getY() + 5);
                        loc.setZ(loc.getZ() - 10);
                        p.addPotionEffect();
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