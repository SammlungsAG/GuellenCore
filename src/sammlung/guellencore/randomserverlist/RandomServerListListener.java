package sammlung.guellencore.randomserverlist;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

import sammlung.guellencore.GuellenCore;

public class RandomServerListListener implements Listener {
	
	private int herobrine_counter = 1;
	
	@EventHandler
	public void onServerListPing(PaperServerListPingEvent event) {
		event.setMotd(RandomServerList.getRandomMOTD());
		event.setServerIcon(RandomServerList.getRandomIcon());
		
		if (GuellenCore.instance.getConfig().getBoolean("enable_herobrine_invasion") && event.getPlayerSample().size() > 0) {
			int chance = GuellenCore.instance.getConfig().getInt("herobrine_chance");
			if (chance < 1) {
				GuellenCore.instance.getSLF4JLogger().error("Herobrine Chance must be at least 1 (Its 1 in ... You cant have 1 in 0)");
				GuellenCore.instance.getSLF4JLogger().error("Using default: 5");
				chance = 5;
			}
			
			if (chance == herobrine_counter) {
				herobrine_counter = 1;
				
				if (event.getPlayerSample().size() == 1) {
					event.getPlayerSample().get(0).setName("Herobrine");
				} else if (event.getPlayerSample().size() > 1) {
					event.getPlayerSample().get(RandomServerList.random.nextInt(event.getPlayerSample().size())).setName("Herobrine");
				}
			} else {
				herobrine_counter++;
			}
		}
	}
	
}
