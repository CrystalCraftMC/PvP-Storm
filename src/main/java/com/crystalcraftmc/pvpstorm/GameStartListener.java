/*
 * Copyright (c) 2015 CrystalCraftMC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.crystalcraftmc.pvpstorm;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.Collection;

public class GameStartListener implements Listener {
    private Collection<Player> hittingPlayers; //changed to set and string - Players objects are memory hogs -jacc
    private boolean countHittingPlayers; //enables onEntityVsEntity listener method
    private PvPStorm plugin;

    /**
     * Constructor for <code>GameStartListener</code>.
     * @param plugin
     */
    public GameStartListener(PvPStorm plugin) {
        this.plugin = plugin;
        hittingPlayers = new ArrayList<Player>();
        countHittingPlayers = false;
    }

    /**
     * Untested listener method. Remove this line once tested. <br />
     * <p/>
     * This listener is called when one entity hits another. If the damager and damgee are both instanceof Player and
     * the damagee.getName() == jflory7, then this will record the Player object of the damager. The algorithm is set up
     * so no duplicate Player objects are added to the ArrayList. <br />
     * <p/>
     * Note that an ArrayList is an array of objects with the unique trait of being able to add elements with the
     * ArrayList's <code>add</code> method, a dynamic count of all the Players stored with the <code>size()</code>
     * method, and retrieve a specific object with the <code>get(index)</code> method. <br />
     * <p/>
     * The result is a method that will start listening upon the "/storm start" command and will add each player that
     * hits the Stormer to the ArrayList without any duplicates, and it stops listening on the "/storm stop" command.
     * There is also a "reset" command which turns the boolean <code>playerHitStormerCounting</code> to false and
     * empties the current ArrayList.
     * <p/>
     * Warning: Using sets resolves the issue as sets will not add an item that it already contains. - Ivan
     *
     * @param event
     * @author Alex Woodward
     * @author Justin W. Flory
     * @author Ivan Frasure
     */
    @EventHandler
    public void onEntityVsEntity(EntityDamageByEntityEvent event) {
        if (countHittingPlayers && event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Bukkit.broadcastMessage(ChatColor.GOLD + "DEBUG: Both damager AND damagee are an instance of a Player");
            Player damagee = (Player) event.getEntity();
            Player damager = (Player) event.getDamager();
            if (damagee.getDisplayName().equals(plugin.getConfig().getString("stormer")) && !damagee.getName().equals(null)) {
                hittingPlayers.add(damager);
                Bukkit.broadcastMessage(ChatColor.GOLD + "DEBUG: Player added to ArrayList");
            }
        }
    }
}
