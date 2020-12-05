package sammlung.guellencore.nightskip;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import sammlung.guellencore.GuellenCore;

/**
 * Command Executor for the Counter-Vote Command
 */
public class CommandCounterVote implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = Player.class.cast(sender);
			if (GuellenCore.instance.getConfig().getBoolean("enable_nightskip") && GuellenCore.instance.getConfig().getBoolean("allow_countervotes")) {
				if (player.getWorld().getUID().equals(GuellenCore.instance.getOverworld().getUID())) {
					if (!Nightskip.hasVoted(player.getUniqueId())) {
						Nightskip.vote(player.getUniqueId(), true);
						Bukkit.broadcastMessage(Nightskip.getCountervoteMessage(player.getName()));
					} else {
						player.sendMessage(ChatColor.GOLD + "You already voted");
					}
				} else {
					player.sendMessage(ChatColor.GOLD + GuellenCore.instance.getConfig().getString("msg_wrong_dimension"));
				}
				return true;
			} else {
				player.sendMessage(ChatColor.DARK_RED + "This feature is disabled in the Server-Config");
			}
		}
		return false;
	}

}
