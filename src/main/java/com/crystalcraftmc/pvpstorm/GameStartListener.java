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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Set;
import java.util.TreeSet;

public class GameStartListener implements Listener  {
	//PvPStorm plugin;
	private String stormerName;//Added this so that the stormer could be edited in config file for others. - jacc
	private Set<String> playersWhoHitStormer; //changed to set and string - Players objects are memory hogs -jacc
	private boolean enablePlayerHitStormerCounting; //enables onEntityVsEntity listener method
	
	//IGNORE this method - Only for use when an instances in called in the main plugin class. And only when Stormer name is needed to add to check permissions.
	public String getStormer() {
        return stormerName;
    }
    //IGNORE This method is NOT for human use. (It will be called once in the onEnable() method of the main plugin class file.)
	public void setStormer(String name) {
       this.stormerName = name;
    }
	
	public GameStartListener(PvPStorm plugin) {
		//this.plugin = plugin; # This line needs to be reworked, throwing compiler error
		playersWhoHitStormer = new TreeSet<String>();
		enablePlayerHitStormerCounting = false;
	}
    
    
    // WARNING: The following listener code was me experimenting. Needs research! - jf

    // WARNING#2:  I added a few commands to comply with my untested algorithm - jwood
    // Not on my computer at the moment - though I'll be able to test code tomorrow - jwood
    //WARNING#3: I made a few changes... so I am sorry for the confusion or intrusion into current edits-jacc
	
	//TODO MOVE THESE... commands to plugin side? and rework so that they connect idk just an idea? commented it out for now - jacc
	
	/*@EventHandler
	public void onStart(PlayerCommandPreprocessEvent useCommand) {
		if (useCommand.equals("start")) {
			// TODO Implement counts of which players hit the Stormer - might use another event type?
			enablePlayerHitStormerCounting = true;
		}
		else if(useCommand.equals("stop")) {
			enablePlayerHitStormerCounting = false;
		}
		else if(useCommand.equals("reset")) {
			enablePlayerHitStormerCounting = false;
			playersWhoHitStormer.clear();//clears the set
		}
		else if(useCommand.equals("getSet")) {//btw If this doesnt work I can try something else.
			//this command is mostly for testing / debugging
			if ((String[]) playersWhoHitStormer.toArray() == null) return;
			useCommand.getPlayer().sendMessage((String[]) playersWhoHitStormer.toArray());
		}
		return;
	}
	*/
    
    // Warning below listener is untested! Though I will explain the idea - jwood
    //listener is called when one entity hits another
    //if the damager & damagee are both instanceof Player and the damagee.getName() is jflory7
    //then this will record the Player object of the damager - the algorithm is set up so
    //no duplicate Player objects are added to the arraylist
    //note: an arraylist is an array of objects -- with the unique trait of being able to add
    //elements with the ArrayLists's add method, to see how many objects you've saved you can
    //call the ArrayList's size() method, and to get a specific object, you use the
    //ArrayList's get(index) method, where index is an int (0 represents the first element)
    //The result is a method that will start listening upon the "start" command (after it's tested)
    //and will add each player that hits flory to the ArrayList (no duplicates), and stops listening
    //upon the "stop" command. (added a "reset" command which turns the boolean flag variable
    //playerHitStormerCounting to false (disables listener below), and empties the current arraylist)
    //again, I note that this method is untested - jwood
    //TODO RE: Warning: Using sets resolves the issue as sets will not add an item that it already contains -jacc
	@EventHandler
	public void onEntityVsEntity(EntityDamageByEntityEvent event) {
		if(enablePlayerHitStormerCounting) {
			if(event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
				Bukkit.broadcastMessage(ChatColor.GOLD + "debug: Both damager AND damagee are an instances of a Player");
				Player damagee = (Player)event.getEntity();
				Player damager = (Player)event.getDamager();
				if(damagee.getName().equals(stormerName) && !damagee.getName().equals(null)) {
					playersWhoHitStormer.add(damager.getName());
					Bukkit.broadcastMessage(ChatColor.GOLD + "debug: player added to set");
				}
			}
		}
	}
}
