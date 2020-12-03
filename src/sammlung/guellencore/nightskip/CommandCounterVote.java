package sammlung.guellencore.nightskip;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import sammlung.guellencore.GuellenCore;

public class CommandCounterVote implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = Player.class.cast(sender);

			if (player.getWorld().getUID().equals(GuellenCore.instance.getOverworld().getUID())) {
				if (!Nightskip.hasVoted(player.getUniqueId())) {
					Nightskip.vote(player.getUniqueId(), true);
					Bukkit.broadcastMessage(Nightskip.getCountervoteMessage(player.getName()));
				} else {
					player.sendMessage(ChatColor.GOLD + "You already voted");
				}
			} else {
				player.sendMessage(GuellenCore.instance.getConfig().getString("msg_wrong_dimension"));
			}
			return true;
		}
		return false;
	}

}
