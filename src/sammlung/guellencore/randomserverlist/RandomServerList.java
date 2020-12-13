package sammlung.guellencore.randomserverlist;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.util.CachedServerIcon;

import sammlung.guellencore.GuellenCore;

public class RandomServerList {

	public static ArrayList<String> motd = new ArrayList<String>();
	public static ArrayList<CachedServerIcon> icons = new ArrayList<CachedServerIcon>();
	public static Random random = new Random();

	public static void load() {
		// Load Random MOTD
		if (showRandomMotd()) {
			motd.clear();
			motd.addAll(GuellenCore.instance.getConfig().getStringList("motd"));
		}

		// Load Random Server Icons
		if (showRandomServerIcon()) {
			icons.clear();
			List<String> iconNames = GuellenCore.instance.getConfig().getStringList("icon_names");
			if (Bukkit.getServerIcon() != null) {
				icons.add(Bukkit.getServerIcon());
			}

			for (String icon : iconNames) {
				File file = new File(icon);
				if (file.exists()) {
					try {
						icons.add(Bukkit.getServer().loadServerIcon(file));
					} catch (Exception e) {
						GuellenCore.instance.getSLF4JLogger().error("Invalid Server Icon: " + icon);
						e.printStackTrace();
					}
				} else {
					GuellenCore.instance.getSLF4JLogger().error("No such File: " + icon);
				}
			}
		}
	}

	public static String getRandomMOTD() {
		if (motd.size() > 1) {
			return motd.get(random.nextInt(motd.size()));
		} else if (motd.size() == 1) {
			return motd.get(0);
		} else {
			return Bukkit.getMotd();
		}
	}

	public static CachedServerIcon getRandomIcon() {
		if (icons.size() > 1) {
			return icons.get(random.nextInt(icons.size()));
		} else if (icons.size() == 1) {
			return icons.get(0);
		} else {
			return Bukkit.getServerIcon();
		}
	}

	public static boolean showRandomMotd() {
		return GuellenCore.instance.getConfig().getBoolean("random_motd");
	}

	public static boolean showRandomServerIcon() {
		return GuellenCore.instance.getConfig().getBoolean("random_server_icon");
	}
}
