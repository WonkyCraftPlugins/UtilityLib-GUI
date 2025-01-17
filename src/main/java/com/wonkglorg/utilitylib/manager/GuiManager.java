package com.wonkglorg.utilitylib.manager;

import com.wonkglorg.utilitylib.inventory.GuiInventory;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Class to manage and store all the menus created
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class GuiManager{
	private static final Map<Player, GuiInventory> menus = new HashMap<>();
	
	/**
	 * Gets the menu of the player
	 *
	 * @param player The player to get the menu of
	 * @return The menu of the player (null if the player doesn't have the menu)
	 */
	public static <T> Optional<T> getMenu(Player player) {
		var inventory = menus.get(player);
		if(inventory == null){
			return Optional.empty();
		}
		return Optional.of((T) inventory);
	}
	
	/**
	 * Gets the menu of the player
	 *
	 * @param player The player to get the menu of
	 * @param clazz The class of the menu (if a menu exists but is of the wrong type it will return an empty optional as well)
	 * @return The menu of the player (null if the player doesn't have the menu)
	 */
	public static <T> Optional<T> getMenu(Player player, Class<T> clazz) {
		var inventory = menus.get(player);
		if(inventory == null){
			return Optional.empty();
		}
		
		if(clazz.isAssignableFrom(inventory.getClass())){
			return Optional.of((T) inventory);
		}
		
		return Optional.empty();
	}
	
	/**
	 * Cleans up all menus and destroys all menus for the player
	 *
	 * @param player The player to cleanup the menus for
	 */
	public static void cleanup(Player player) {
		var inventory = menus.remove(player);
		if(inventory == null){
			return;
		}
		player.closeInventory();
	}
	
	/**
	 * Cleans up all menus and destroys all menus for all players
	 */
	public static void cleanup() {
		for(var player : menus.keySet()){
			cleanup(player);
		}
	}
	
	/**
	 * Adds a menu to the player
	 *
	 * @param player The player to add the menu to
	 * @param menu The menu to add
	 */
	public static void addMenu(Player player, GuiInventory menu) {
		GuiInventory inventory = menus.put(player, menu);
		if(inventory != null){
			inventory.destroy();
		}
	}
}
