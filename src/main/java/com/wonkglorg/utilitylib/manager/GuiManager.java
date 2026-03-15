package com.wonkglorg.utilitylib.manager;

import com.wonkglorg.utilitylib.inventory.GuiInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Class to manage and store all the menus created
 */
@SuppressWarnings({"rawtypes", "unchecked", "unused"})
public class GuiManager implements Listener{
	private static GuiManager instance;
	private static final Map<UUID, GuiInventory> menus = new HashMap<>();
	
	private GuiManager(JavaPlugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	/**
	 * Creates a new instance of the GuiManager
	 *
	 * @param plugin the plugin to create the instance for
	 * @return the created instance
	 */
	public static GuiManager createInstance(JavaPlugin plugin) {
		if(instance == null){
			instance = new GuiManager(plugin);
		}
		return instance;
	}
	
	/**
	 * Gets the instance of the GuiManager use {@link GuiManager#createInstance(JavaPlugin)} before to initialize the instance
	 *
	 * @return the instance of the GuiManager or null if not initialized correctly
	 */
	public static GuiManager instance() {
		if(instance == null){
			throw new IllegalStateException("GuiManager instance has not been initialized!");
		}
		return instance;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) { //NOSONAR
		if(menus.isEmpty()) return;
		for(var menu : menus.values()){
			if(menu.getInventory().equals(e.getView().getTopInventory())){
				menu.onClick(e);
				return;
			}
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if(menus.isEmpty()) return;
		Iterator<GuiInventory> iterator = menus.values().iterator();
		while(iterator.hasNext()){
			GuiInventory next = iterator.next();
			if(next.getInventory().equals(e.getView().getTopInventory()) && e.getViewers().size() <= 1){
				next.destroy((Player) e.getPlayer(), false);
				iterator.remove();
			}
		}
	}
	
	@EventHandler
	public void onDrag(InventoryDragEvent e) {
		if(menus.isEmpty()) return;
		for(var menu : menus.values()){
			List<Integer> slots = e.getRawSlots().stream().filter(s -> menu.getInventory(e.getView(), s).equals(menu.getInventory())).toList();
			menu.onDrag(e, slots);
		}
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
		inventory.destroy();
		inventory.getPlayer().closeInventory();
	}
	
	/**
	 * Cleans up all menus and destroys all menus for all players
	 */
	public static void cleanup() {
		Iterator<GuiInventory> iterator = menus.values().iterator();
		while(iterator.hasNext()){
			GuiInventory next = iterator.next();
			next.destroy();
			next.getPlayer().closeInventory();
			iterator.remove();
		}
	}
	
	/**
	 * Adds a menu to the uuid
	 *
	 * @param uuid The uuid to add the menu to
	 * @param menu The menu to add
	 */
	public static <T extends GuiInventory> void addMenu(UUID uuid, T menu) {
		GuiInventory inventory = menus.put(uuid, menu);
		if(inventory != null){
			inventory.destroy();
		}
	}
}
