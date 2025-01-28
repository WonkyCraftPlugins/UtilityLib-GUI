package com.wonkglorg.utilitylib.manager;

import com.wonkglorg.utilitylib.inventory.GuiInventory;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Class to manage and store all the menus created
 */
@SuppressWarnings({"rawtypes", "unchecked","unused"})
public class GuiManager{
	private static final Map<UUID, GuiInventory> menus = new HashMap<>();
	
	private GuiManager() {
	}
	
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
	 * Gets all menus of a given type
	 *
	 * @param clazz The class of the menu
	 * @param <T> The type of the menu
	 * @return All menus of the given type
	 */
	public static <T extends GuiInventory> List<T> getMenus(Class<T> clazz) {
		return (List<T>) menus.values().stream().filter(clazz::isInstance).toList();
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
	 * Cleans up all menus and destroys all menus for all players, sends a message to the player when the menu is closed
	 */
	public static void cleanup(Consumer<Player> onInventoryClose) {
		for(var player : menus.keySet()){
			var inventory = menus.remove(player);
			if(inventory == null){
				return;
			}
			inventory.getPlayer().closeInventory();
			if(onInventoryClose != null){
				onInventoryClose.accept(inventory.getPlayer());
			}
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
