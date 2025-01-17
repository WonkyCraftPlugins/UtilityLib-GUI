package com.wonkglorg.utilitylib.manager;

import com.wonkglorg.utilitylib.inventory.GuiInventory;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Class to manage and store all the menus created
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class GuiManager{
	private static final Map<UUID, GuiInventory> menus = new HashMap<>();
	
	/**
	 * Gets the menu of the uuid
	 *
	 * @param uuid The uuid to get the menu of
	 * @return The menu of the uuid (null if the uuid doesn't have the menu)
	 */
	public static <T> Optional<T> getMenu(UUID uuid) {
		var inventory = menus.get(uuid);
		if(inventory == null){
			return Optional.empty();
		}
		return Optional.of((T) inventory);
	}
	
	/**
	 * Gets the menu of the uuid
	 *
	 * @param uuid The uuid to get the menu of
	 * @param clazz The class of the menu (if a menu exists but is of the wrong type it will return an empty optional as well)
	 * @return The menu of the uuid (null if the uuid doesn't have the menu)
	 */
	public static <T> Optional<T> getMenu(UUID uuid, Class<T> clazz) {
		var inventory = menus.get(uuid);
		if(inventory == null){
			return Optional.empty();
		}
		
		if(clazz.isAssignableFrom(inventory.getClass())){
			return Optional.of((T) inventory);
		}
		
		return Optional.empty();
	}
	
	/**
	 * Cleans up all menus and destroys all menus for the uuid
	 *
	 * @param uuid The uuid to cleanup the menus for
	 */
	public static void cleanup(UUID uuid) {
		var inventory = menus.remove(uuid);
		if(inventory == null){
			return;
		}
		inventory.getPlayer().closeInventory();
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
	 * Adds a menu to the uuid
	 *
	 * @param uuid The uuid to add the menu to
	 * @param menu The menu to add
	 */
	public static void addMenu(UUID uuid, GuiInventory menu) {
		GuiInventory inventory = menus.put(uuid, menu);
		if(inventory != null){
			inventory.destroy();
		}
	}
}
