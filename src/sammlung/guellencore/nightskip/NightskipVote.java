package sammlung.guellencore.nightskip;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NightskipVote {

	/**
	 * False if its just a normal Sleep Vote. True if it counts against all other votes
	 */
	private boolean isCounterVote;
	
	/**
	 * UUID of the Player this vote belongs to
	 */
	private UUID playerID;
	
	public NightskipVote(UUID playerID, boolean isCounterVote) {
		this.playerID = playerID;
		this.isCounterVote = isCounterVote;
	}
	
	public NightskipVote(UUID playerID) {
		this(playerID, false);
	}
	
	@Nonnull
	public boolean isCounterVote() {
		return isCounterVote;
	}
	
	@Nonnull
	public UUID getPlayerID() {
		return playerID;
	}
	
	/**
	 * Might be null if the Player this vote belongs to isn't online any more
	 * @return The Player this vote belongs to.
	 */
	@Nullable
	public Player getPlayer() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getUniqueId().equals(playerID)) {
				return p;
			}
		}
		return null;
	}
}
