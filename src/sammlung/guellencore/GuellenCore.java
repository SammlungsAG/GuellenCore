package sammlung.guellencore;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import sammlung.guellencore.nightskip.CommandCounterVote;
import sammlung.guellencore.nightskip.Nightskip;
import sammlung.guellencore.nightskip.NightskipListener;

public class GuellenCore extends JavaPlugin {

	public static GuellenCore instance;

	@Override
	public void onEnable() {
		// Load default Config File
		saveDefaultConfig();
		instance = this;

		// Nightskip
		if (getConfig().getBoolean("enable_nightskip")) {
			getServer().getPluginManager().registerEvents(new NightskipListener(), this);
			Nightskip.worldTimeWatcher.runTaskTimer(this, 0L, 0L);
		}
		
		//Command Registry
		getCommand("countervote").setExecutor(new CommandCounterVote());
	}

	@Override
	public void onDisable() {
		getLogger().info("Alfred betrayed. Money earned!");

		//Unschedule Tasks
		if (getConfig().getBoolean("enable_nightskip")) {
			Nightskip.worldTimeWatcher.cancel();
			for (BukkitRunnable runnable : Nightskip.sleepTimers.values()) {
				runnable.cancel();
			}
			Nightskip.sleepTimers.clear();
		}
	}

	public World getOverworld() {
		return Bukkit.getWorlds().get(0);
	}

}
