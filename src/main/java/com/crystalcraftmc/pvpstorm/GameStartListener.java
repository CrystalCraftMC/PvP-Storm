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

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;

public class GameStartListener implements Listener, CommandExecutor {
    PvPStorm plugin;
    private ArrayList<Player> playersWhoHitStormer; //holds array of Player objects recording who hit flory
    private boolean enablePlayerHitStormerCounting; //enables onEntityVsEntity listener method
  
    public GameStartListener(PvPStorm plugin) {
        this.plugin = plugin;
        playersWhoHitStormer = new ArrayList<Player>();
        enablePlayerHitStormerCounting = false;
    }
    
    
    // WARNING: The following listener code was me experimenting. Needs research! - jf
    // WARNING#2:  I added a few commands to comply with my untested algorithm - jwood
    // Not on my computer at the moment - though I'll be able to test code tomorrow - jwood
    @EventHandler
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
            playersWhoHitStormer = new ArrayList<Player>(); //empties the arraylist
        }
        else if(useCommand.equals("getArrayList")) {
            //this command is mostly for testing / debugging
            for(int i = 0; i < playersWhoHitStormer.size(); i++) {
                useCommand.getPlayer().sendMessage(playersWhoHitStormer.get(i).getName());
            }
        }
    }
    
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
    @EventHandler
    public void onEntityVsEntity(EntityDamageByEntityEvent event) {
        if(enablePlayerHitStormerCounting) {
            if(event.getDamager() instanceof Player) {
                Bukkit.broadcastMessage("debug: damager is an instance of player");
                if(event.getEntity() instanceof Player) {
                    Bukkit.broadcastMessage("debug: damagee is an instance of player");
                    Player damagee = (Player)event.getEntity();
                    Player damager = (Player)event.getDamager();
                    if(damagee.getName().equals("jflory7") || damagee.getName().equals("Jwood9198")) {
                        if(playersWhoHitStormer.size() == 0) {
                            playersWhoHitStormer.add(damager);
                            Bukkit.broadcastMessage("debug: player added to arraylist");
                        }
                        else {
                            boolean isDamagersFirstHitOnStormer = true;
                            for(int i = 0; i < playersWhoHitStormer.size(); i++) {
                                if(damager.getName().equals(playersWhoHitStormer.get(i).getName())) {
                                    isDamagersFirstHitOnStormer = false;
                                }
                            }
                            if(isDamagersFirstHitOnStormer) {
                                playersWhoHitStormer.add(damager);
                                Bukkit.broadcastMessage("debug: player added to arraylist");
                            }
                        } 
                    }  
            
                }  
            }
        }
    
    }
}
