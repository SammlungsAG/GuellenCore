package sammlung.guellencore;

import org.bukkit.plugin.java.JavaPlugin;

public class GuellenCore extends JavaPlugin {

	@Override
	public void onEnable() {
		getLogger().info("Spicing up grades... Entering Sammlungsmode");
	}
	
	@Override
	public void onDisable() {
		getLogger().info("Alfred betrayed. Money earned!");
	}
	
}
