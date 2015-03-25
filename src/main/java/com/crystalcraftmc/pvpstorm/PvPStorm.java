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

    public void onDisable() { getLogger().info(ChatColor.RED + "PvPStorm has been stopped by the server."); }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("storm")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("This command can only be run by a player.");
                return false;
            } else if (args.length < 1) {
                return false;
            } else if (args.length >= 1) {
                Player p = (Player) sender;
                World world = p.getWorld();

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

                        Bukkit.broadcastMessage(p.getDisplayName() + ChatColor.YELLOW + " used " + ChatColor.GOLD + " FLARE " + ChatColor.YELLOW + " ability!");
                        for (Player nearbyPlayer : players) {
                            if (nearbyPlayer.getLocation().distanceSquared(senderLoc) <= 25) {
                                nearbyLoc = nearbyPlayer.getLocation();

                                nearbyPlayer.setHealth(nearbyPlayer.getHealth() - 2.0);
                                nearbyPlayer.playSound(nearbyLoc, Sound.SUCCESSFUL_HIT, 1, 1);
                            }
                            p.playSound(senderLoc, Sound.EXPLODE, 1, 1);
                        }
                        if (p.getHealth() > 3.0) p.setHealth(p.getHealth() - 3.0);
                        else if (p.getHealth() <= 1.0) {
                            p.setHealth(0.0D);
                            Bukkit.broadcastMessage(p.getDisplayName() + ChatColor.RED + "commit suicide with their own powers!");
                        }
                        else p.setHealth(1.0);
                        return true;
                    } else if (args[1].equalsIgnoreCase("vanish")) {
                        Bukkit.broadcastMessage(p.getDisplayName() + ChatColor.YELLOW + " used " + ChatColor.GRAY + " VANISH " + ChatColor.YELLOW + " ability!");
                        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 3600, 0, false, false));
                        if (p.getHealth() > 5.0) p.setHealth(p.getHealth() - 5.0);
                        else if (p.getHealth() <= 1.0) {
                            p.setHealth(0.0D);
                            Bukkit.broadcastMessage(p.getDisplayName() + ChatColor.RED + "commit suicide with their own powers!");
                        } else p.setHealth(1.0);
                        return true;
                    } else if (args[1].equalsIgnoreCase("timewarp")) {
                        Vector direction = p.getLocation().getDirection();

                        Bukkit.broadcastMessage(p.getDisplayName() + ChatColor.YELLOW + " used " + ChatColor.RED + " TIMEWARP " + ChatColor.YELLOW + " ability!");
                        p.setVelocity(new Vector(direction.getX() * -2.5, 1.2, direction.getZ() * -2.5));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 300, 0));
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