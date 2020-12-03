package sammlung.guellencore.nightskip;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedEnterEvent.BedEnterResult;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import sammlung.guellencore.GuellenCore;
import sammlung.guellencore.event.NightskipEvent;

/**
 * Listener listening to Events relevant to the Nightskip System
 */
public class NightskipListener implements Listener {

	@EventHandler()
	public void onBedLeave(PlayerBedLeaveEvent event) {
		if (event.getPlayer().getWorld().getUID().equals(GuellenCore.instance.getOverworld().getUID())) {
			if (GuellenCore.instance.getOverworld().getTime() > 0) {
				Nightskip.invalidate(event.getPlayer().getUniqueId());
				if (event.getPlayer().getWorld().getPlayers().size() > 1) {
					Bukkit.broadcastMessage(Nightskip.getBedLeaveMesssage(event.getPlayer().getName()));
				}
			}
		}
	}

	@EventHandler
	public void onBedEnter(PlayerBedEnterEvent event) {
		if (event.getBedEnterResult() == BedEnterResult.OK) {
			if (event.getPlayer().getWorld().getUID().equals(GuellenCore.instance.getOverworld().getUID())) {
				Nightskip.watch(event.getPlayer());
			}
		}
	}

	@EventHandler
	public void onPlayerDisconnected(PlayerQuitEvent event) {
		boolean relevant = Nightskip.hasVoted(event.getPlayer().getUniqueId());
		Nightskip.invalidate(event.getPlayer().getUniqueId());
		if (relevant)
			Bukkit.broadcastMessage(Nightskip.getBedLeaveMesssage(event.getPlayer().getName()));
	}

}
