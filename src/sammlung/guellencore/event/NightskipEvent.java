package sammlung.guellencore.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired when a Nightskip is performed
 */
public class NightskipEvent extends Event{
	
	private static final HandlerList handlers = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

}
