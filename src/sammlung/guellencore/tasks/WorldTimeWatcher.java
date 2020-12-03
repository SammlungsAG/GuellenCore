package sammlung.guellencore.tasks;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;
import sammlung.guellencore.GuellenCore;
import sammlung.guellencore.nightskip.Nightskip;

public class WorldTimeWatcher extends BukkitRunnable {

	public Map<UUID, Boolean> worldTimeStates = new HashMap<UUID, Boolean>();
	
	@Override
	public void run() {
		for (World world : Bukkit.getWorlds()) {
			boolean isDayTime = world.isDayTime();
			UUID worldID = world.getUID();
			if (!worldTimeStates.containsKey(worldID)) {
				worldTimeStates.put(worldID, isDayTime);
				continue;
			}
			
			if (worldTimeStates.get(worldID) != isDayTime) {
				worldTimeStates.put(worldID, isDayTime);
				if (isDayTime && world.getUID().equals(GuellenCore.instance.getOverworld().getUID())) {
					Nightskip.invalidateAllVotes();
					for (Player p : GuellenCore.instance.getOverworld().getPlayers()) {
						if (p.isSleepingIgnored()) {	
							p.setSleepingIgnored(false);
						}
					}
					Bukkit.broadcastMessage(ChatColor.GOLD + GuellenCore.instance.getConfig().getString("msg_good_morning"));
				}
			}
		}
	}
	
	@Override
	public synchronized void cancel() throws IllegalStateException {
		worldTimeStates.clear();
		super.cancel();
	}

}
