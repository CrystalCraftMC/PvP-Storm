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
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.Collection;

public class PvPStorm extends JavaPlugin {
    // TODO Implement mob boss health bar - perhaps in listener? NEEDS RESEARCH.
    
    public void onEnable() {
        getLogger().info(ChatColor.AQUA + "PvPStorm has been initialized!");

        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (IOException e) {
            // Failed to submit the stats :-(
        }

        getServer().getPluginManager().registerEvents(gameStart, this);
        getServer().getPluginManager().registerEvents(gameEnd, this);
    }

    public void onDisable() {
        getLogger().info(ChatColor.RED + "PvP Storm has been stopped by the server.");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;
        World world = p.getWorld();
        String stormer = p.toString();

        if (cmd.getName().equalsIgnoreCase("storm")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("This command can only be run by a player.");
                return false;
            } else if (args.length < 1) {
                return false;
            } else if (args.length >= 1) {
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
                        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
                        Location senderLoc = p.getLocation();
                        Location nearbyLoc;

                        Bukkit.broadcastMessage(stormer + ChatColor.YELLOW + " used " + ChatColor.GOLD + " FLARE " + ChatColor.YELLOW + " ability!");
                        for (Player nearbyPlayer : players) {
                            if (nearbyPlayer.getLocation().distanceSquared(senderLoc) <= 25) {
                                nearbyLoc = nearbyPlayer.getLocation();

                                nearbyPlayer.setHealth(nearbyPlayer.getHealth() - 2.0);
                                nearbyPlayer.playSound(nearbyLoc, Sound.SUCCESSFUL_HIT, 1, 1);
                            }
                            p.playSound(senderLoc, Sound.EXPLODE, 1, 1);
                        }
                        p.setHealth(p.getHealth() - 3.0);
                        return true;
                    } else if (args[1].equalsIgnoreCase("vanish")) {
                        Bukkit.broadcastMessage(stormer + ChatColor.YELLOW + " used " + ChatColor.GRAY + " VANISH " + ChatColor.YELLOW + " ability!");
                        // TODO Make user invisible at expense of health
                        return true;
                    } else if (args[1].equalsIgnoreCase("timewarp")) {
                        Vector direction = p.getLocation().getDirection();

                        Bukkit.broadcastMessage(stormer + ChatColor.YELLOW + " used " + ChatColor.RED + " TIMEWARP " + ChatColor.YELLOW + " ability!");
                        p.setVelocity(new Vector(direction.getX() * -2.5, 1.2, direction.getZ() * -2.5));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 0));
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