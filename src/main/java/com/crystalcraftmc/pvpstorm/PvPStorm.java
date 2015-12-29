/*
 * Copyright (c) 2015 CrystalCraftMC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.crystalcraftmc.pvpstorm;

import me.confuser.barapi.BarAPI;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PvPStorm extends JavaPlugin {
	
	/*Holds different powers one can use*/
	private enum Power { FLARE, VANISH, TIMEWARP }
	
	/**Holds list of players & corresponding time of command use
	 *  who are in cooldown for flare default(30s)*/
	private Map<String, Long> flareList = new HashMap<String, Long>();
	
	/**Holds list of players & corresponding time of command use
	 *  who are in cooldown for vanish default(3m)*/
	private Map<String, Long> vanishList = new HashMap<String, Long>();
	
	/**Holds list of players & corresponding time of command use
	 *  who are in cooldown for timewarp default(15s)*/
	private Map<String, Long> timeWarpList = new HashMap<String, Long>();
	
	/**Holds cooldown delay for Flare default(30s)(30000 ms)*/
	final private int FLARECOOLDOWN = 30000;
	
	/**Holds cooldown delay for Vanish default(3m)(180000 ms)*/
	final private int VANISHCOOLDOWN = 180000;
	
	/**Holds cooldown delay for TimeWarp default(15s)(15000 ms)*/
	final private int TIMEWARPCOOLDOWN = 15000;
	
    public void onEnable() {
        getLogger().info(ChatColor.AQUA + "PvPStorm has been initialized!");

        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (IOException e) {
            // Failed to submit the stats :-(
        }

        GameStartListener gameStart = new GameStartListener(this);
        GameEndListener gameEnd = new GameEndListener(this);
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
                    if (getServer().getPluginManager().getPlugin("BarAPI") != null) setBar(ChatColor.DARK_RED + getConfig().getString("start-message"));
                    world.setStorm(true);
                    this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                        //@Override
                        public void run() {
                            Bukkit.broadcastMessage(ChatColor.DARK_RED + "The PvP Storm is now hitting the Arena!");
                            if (getServer().getPluginManager().getPlugin("BarAPI") != null) setBar(ChatColor.DARK_RED + "The PvP Storm is now hitting the Arena!");
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
                        sender.sendMessage(ChatColor.YELLOW + "The available Storm powers are:\n" +
                                ChatColor.RED + "/storm power flare\n" +
                                ChatColor.GRAY + "/storm power vanish\n" +
                                ChatColor.AQUA + "/storm power timewarp");
                        return true;
                    } else if (args[1].equalsIgnoreCase("flare")) { //TODO test cooldown of 30 seconds
                    	
                    	int timeLeft = hasCooledDown(p, Power.FLARE);
                    	if(timeLeft != -1) {
                    		p.sendMessage(ChatColor.DARK_RED + "Flare " + ChatColor.AQUA + 
                    				"CoolDown for " + ChatColor.BLUE +
                    				String.valueOf(timeLeft) + ChatColor.AQUA + " more seconds!");
                    		return true;
                    	}
                        flareList.put(p.getName(), System.currentTimeMillis());
                        
                        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
                        Location senderLoc = p.getLocation();
                        Location nearbyLoc;
                        
                        Bukkit.broadcastMessage(p.getDisplayName() + ChatColor.YELLOW + " used " + ChatColor.GOLD + " FLARE " + ChatColor.YELLOW + " ability!");
                        for (Player nearbyPlayer : players) {
                            if (nearbyPlayer.getLocation().distanceSquared(senderLoc) <= 25) {
                                nearbyLoc = nearbyPlayer.getLocation();

                                if (nearbyPlayer.getHealth() <= 2.0 && nearbyPlayer != p) nearbyPlayer.setHealth(0.0D);
                                else if (nearbyPlayer != p) nearbyPlayer.setHealth(nearbyPlayer.getHealth() - 2.0);
                                nearbyPlayer.playSound(nearbyLoc, Sound.SUCCESSFUL_HIT, 1, 1);
                            }
                            p.playSound(senderLoc, Sound.EXPLODE, 1, 1);
                        }
                        if (p.getHealth() > 3.0) p.setHealth(p.getHealth() - 3.0);
                        else if (p.getHealth() <= 1.0) {
                            p.setHealth(0.0D);
                            Bukkit.broadcastMessage(p.getDisplayName() + ChatColor.RED + " commit suicide with their own powers!");
                        }
                        else p.setHealth(1.0);
                        return true;
                    } else if (args[1].equalsIgnoreCase("vanish")) { //TODO test cooldown of 3 minutes
                    	
                    	int timeLeft = hasCooledDown(p, Power.VANISH);
                    	if(timeLeft != -1) {
                    		p.sendMessage(ChatColor.LIGHT_PURPLE + "Vanish " + ChatColor.AQUA + 
                    				"CoolDown for " + ChatColor.BLUE +
                    				String.valueOf(timeLeft) + ChatColor.AQUA + " more seconds!");
                    		return true;
                    	}
                        vanishList.put(p.getName(), System.currentTimeMillis());
                    	
                        Bukkit.broadcastMessage(p.getDisplayName() + ChatColor.YELLOW + " used " + ChatColor.GRAY + " VANISH " + ChatColor.YELLOW + " ability!");
                        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 3600, 0, false, false));
                        if (p.getHealth() > 5.0) p.setHealth(p.getHealth() - 5.0);
                        else if (p.getHealth() <= 1.0) {
                            p.setHealth(0.0D);
                            Bukkit.broadcastMessage(p.getDisplayName() + ChatColor.RED + " commit suicide with their own powers!");
                        } else p.setHealth(1.0);
                        return true;
                    } else if (args[1].equalsIgnoreCase("timewarp")) { //TODO test cooldown of 15 seconds
                    	
                    	int timeLeft = hasCooledDown(p, Power.TIMEWARP);
                    	if(timeLeft != -1) {
                    		p.sendMessage(ChatColor.GREEN + "TimeWarp " + ChatColor.AQUA + 
                    				"CoolDown for " + ChatColor.BLUE +
                    				String.valueOf(timeLeft) + ChatColor.AQUA + " more seconds!");
                    		return true;
                    	}
                        timeWarpList.put(p.getName(), System.currentTimeMillis());
                    	
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

    private void setBar(String message) {
        for(Player p : Bukkit.getOnlinePlayers()){
            if(BarAPI.hasBar(p)){
                BarAPI.removeBar(p);
            }
            BarAPI.setMessage(p, message);
        }
    }
    
    /**Checks whether the player has cooled down for the specific power
     * @param p, the Player we're testing for
     * @param Power, the power we're looking at
     * @return int time since last use, -1 if has cooled down - time left if they have not
     */
    private int hasCooledDown(Player p, Power power) {
    	int timeLeft;
    	switch(power) {
    	case FLARE:
    		if(flareList.keySet() == null)
    			return -1;
    		for(String name : flareList.keySet()) {
    			if(p.getName().equals(name)) {
    				int timePassed = (int)(System.currentTimeMillis()-flareList.get(name));
    				if(timePassed > FLARECOOLDOWN) {
    					flareList.remove(name);
    					return -1;
    				}
    				else {
    					timeLeft = (int)(System.currentTimeMillis()-flareList.get(p.getName()));
                		timeLeft = FLARECOOLDOWN-timeLeft;
                		timeLeft /= 1000;
    					return timeLeft;
    				}
    			}
    		}
    		return -1;
    	case VANISH:
    		if(vanishList.keySet() == null)
    			return -1;
    		for(String name : vanishList.keySet()) {
    			if(p.getName().equals(name)) {
    				int timePassed = (int)(System.currentTimeMillis()-vanishList.get(name));
    				if(timePassed > VANISHCOOLDOWN) {
    					vanishList.remove(name);
    					return -1;
    				}
    				else {
    					timeLeft = (int)(System.currentTimeMillis()-vanishList.get(p.getName()));
                		timeLeft = VANISHCOOLDOWN-timeLeft;
                		timeLeft /= 1000;
    					return timeLeft;
    				}
    			}
    		}
    		return -1;
    	case TIMEWARP:
    		if(timeWarpList.keySet() == null)
    			return -1;
    		for(String name : timeWarpList.keySet()) {
    			if(p.getName().equals(name)) {
    				int timePassed = (int)(System.currentTimeMillis()-timeWarpList.get(name));
    				if(timePassed > TIMEWARPCOOLDOWN) {
    					timeWarpList.remove(name);
    					return -1;
    				}
    				else {
    					timeLeft = (int)(System.currentTimeMillis()-timeWarpList.get(p.getName()));
                		timeLeft = TIMEWARPCOOLDOWN-timeLeft;
                		timeLeft /= 1000;
    					return timeLeft;
    				}
    			}
    		}
    		return -1;
    	}
    	p.sendMessage(ChatColor.RED + "Error at PvPStorm.hasCooledDown");
    	return -1;
    }
}
