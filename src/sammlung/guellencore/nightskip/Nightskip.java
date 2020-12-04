package sammlung.guellencore.nightskip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import sammlung.guellencore.GuellenCore;
import sammlung.guellencore.event.NightskipEvent;
import sammlung.guellencore.tasks.SleepTimer;
import sammlung.guellencore.tasks.WorldTimeWatcher;

public class Nightskip {

	private static ArrayList<NightskipVote> votes = new ArrayList<NightskipVote>();
	public static WorldTimeWatcher worldTimeWatcher = new WorldTimeWatcher();
	public static Map<UUID, SleepTimer> sleepTimers = new HashMap<UUID, SleepTimer>();

	/**
	 * Start a timer and if a certain time has passed(config option), vote for the
	 * Player
	 * 
	 * @param player The Player
	 */
	public static void watch(Player player) {
		if (sleepTimers.containsKey(player.getUniqueId())) {
			GuellenCore.instance.getSLF4JLogger().error("Already watching: " + player.getName());
			return;
		}

		SleepTimer timer = new SleepTimer(player);
		sleepTimers.put(player.getUniqueId(), timer);
		timer.runTaskLater(GuellenCore.instance, GuellenCore.instance.getConfig().getInt("sleep_vote_delay"));
	}

	/**
	 * Performs the night skip if can be done according to {@link #shouldSkip()}
	 * 
	 * @param world The World to skip the night in
	 * @return Whether the Skip actually happened
	 */
	public static boolean skip(World world) {
		if (shouldSkip()) {

			// Ignore non sleeping Players
			for (Player p : world.getPlayers()) {
				if (!p.isSleeping()) {
					GuellenCore.instance.getSLF4JLogger().error("Now ignoring: " + p.getName());
					p.setSleepingIgnored(true);
				}
			}

			// Send Event
			Bukkit.getPluginManager().callEvent(new NightskipEvent());

			return true;
		}
		return false;
	}

	/**
	 * Invalidate All votes
	 */
	public static void invalidateAllVotes() {
		votes.clear();
	}

	/**
	 * @param playerID      The Players Unique ID
	 * @param isCounterVote False if its just a normal vote. True if this vote
	 *                      counts against all others
	 */
	public static void vote(UUID playerID, boolean isCounterVote) {
		if (hasVoted(playerID)) {
			return;
		}

		if (Bukkit.getPlayer(playerID) != null) {
			NightskipVote vote = new NightskipVote(playerID, isCounterVote);
			String name = Bukkit.getPlayer(playerID).getName();
			votes.add(vote);
			skip(GuellenCore.instance.getOverworld());
			return;
		} else {
			GuellenCore.instance.getLogger().info("No Player with this ID: " + playerID.toString());
		}
		return;
	}

	/**
	 * Remove a Players vote from the List for example if he disconnects or leaves
	 * the Bed
	 * 
	 * @param playerID
	 */
	public static void invalidate(UUID playerID) {
		for (int i = 0; i < votes.size(); i++) {
			if (votes.get(i).getPlayerID().equals(playerID)) {
				votes.remove(i);
				return;
			}
		}

		if (sleepTimers.containsKey(playerID)) {
			sleepTimers.get(playerID).cancel();
			sleepTimers.remove(playerID);
		}
	}

	/**
	 * Whether a certain Player has already voted
	 * 
	 * @param playerID The Players Unique ID
	 * @return If the player voted
	 */
	public static boolean hasVoted(UUID playerID) {
		return getVote(playerID) != null;
	}

	/**
	 * Get the Vote Instance of a certain Player Can be null if the Player has not
	 * contributed to the vote
	 * 
	 * @param playerID The Players Unique ID
	 * @return The Vote Instance
	 */
	@Nullable
	public static NightskipVote getVote(UUID playerID) {
		for (NightskipVote vote : votes) {
			if (vote.getPlayerID().equals(playerID)) {
				return vote;
			}
		}
		return null;
	}

	/**
	 * Whether the Night should be skipped.
	 * 
	 * @return
	 */
	public static boolean shouldSkip() {
		if (GuellenCore.instance.getOverworld().getPlayers().size() <= 1) {
			return false;
		}

		if (getEffectiveVotes() < 0)
			return false;
		return getEffectiveVotes() >= getRequiredPlayers();
	}

	/**
	 * How much Players are required to skip the night
	 * 
	 * @return
	 */
	public static double getRequiredPlayers() {
		double configVal = GuellenCore.instance.getConfig().getDouble("needed_players");
		if (configVal < 0) {
			GuellenCore.instance.getSLF4JLogger().error("Invalid value for needed_players. Must be greater than 0");
			return 1;
		} else if (configVal < 1) {
			return GuellenCore.instance.getOverworld().getPlayers().size() * configVal;
		} else {
			return configVal;
		}
	}

	/**
	 * @return The Number of Votes with the Counter-Votes subtracted
	 */
	public static int getEffectiveVotes() {
		return getSleepVotes() - getCounterVotes();
	}

	/**
	 * @return How many People voted to skip the night
	 */
	public static int getSleepVotes() {
		int count = 0;
		for (NightskipVote vote : votes) {
			if (!vote.isCounterVote()) {
				count++;
			}
		}
		return count;
	}

	/**
	 * @return How many People don't want the Night to be skipped
	 */
	public static int getCounterVotes() {
		int count = 0;
		for (NightskipVote vote : votes) {
			if (vote.isCounterVote()) {
				count++;
			}
		}
		return count;
	}

	/**
	 * @return How many People participated in the Vote
	 */
	public static int getTotalVotes() {
		return votes.size();
	}

	public static String getStatusMessage() {
		String config = "§6" + GuellenCore.instance.getConfig().getString("msg_status");
		return config.replaceAll("<people_sleep>", "§a" + Integer.toString(getSleepVotes()) + "§6").replaceAll("<counter_voters", "§c" + Integer.toString(getCounterVotes()) + "§6").replaceAll("<required>", "§c" + Integer.toString((getRequiredPlayers() % 1 == 0 ? (int) getRequiredPlayers() : (int) (getRequiredPlayers() + 1))) + "§6");
	}

	public static String getSleepMessage(String playerName) {
		String s = GuellenCore.instance.getConfig().getString("msg_now_sleeping");
		return String.format("%s%s %s\n%s", ChatColor.GOLD, playerName, s, getStatusMessage());
	}

	public static String getBedLeaveMesssage(String playerName) {
		String s = GuellenCore.instance.getConfig().getString("msg_leave_Bed");
		return String.format("%s%s %s\n%s", ChatColor.GOLD, playerName, s, getStatusMessage());
	}

	public static String getRevokeCounterMessage(String playerName) {
		String s = GuellenCore.instance.getConfig().getString("msg_revoke_counter");
		return String.format("%s%s %s\n%s", ChatColor.GOLD, playerName, s, getStatusMessage());
	}

	public static String getCountervoteMessage(String playerName) {
		String s = GuellenCore.instance.getConfig().getString("msg_countervote");
		return String.format("%s%s %s (%s%d Counter-Voters", ChatColor.GOLD, playerName, s, ChatColor.DARK_RED, getCounterVotes()) + ")";
	}

}
