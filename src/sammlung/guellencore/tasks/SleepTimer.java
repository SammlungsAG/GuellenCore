package sammlung.guellencore.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import sammlung.guellencore.nightskip.Nightskip;

public class SleepTimer extends BukkitRunnable {

	/**
	 * The Player to watch
	 */
	Player player;

	public SleepTimer(Player player) {
		this.player = player;
	}

	@Override
	public void run() {
		boolean isRevoke = false;
		if (Nightskip.hasVoted(player.getUniqueId())) {
			Nightskip.invalidate(player.getUniqueId());
			isRevoke = true;
		}
		
		Nightskip.vote(player.getUniqueId(), false);
		
		if (player.getWorld().getPlayers().size() > 1) {
			if (isRevoke) {
				Bukkit.broadcastMessage(Nightskip.getRevokeCounterMessage(player.getName()));
			} else {
				Bukkit.broadcastMessage(Nightskip.getSleepMessage(player.getName()));
			}
		}
		
		Nightskip.sleepTimers.remove(player.getUniqueId());
	}

}
