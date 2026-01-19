package com.wonkglorg.utilitylib.inventory;

import com.wonkglorg.utilitylib.inventory.profile.MenuProfile;
import com.wonkglorg.utilitylib.manager.GuiManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.ref.Cleaner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

/**
 * @author Redempt, Wonkglorg
 */
@SuppressWarnings({"unused", "unchecked"})
public abstract class GuiInventory<T extends MenuProfile> implements Listener{
	
	private static final Cleaner cleaner = Cleaner.create();
	
	private record CleanupTask(GuiInventory<?> gui) implements Runnable{
		
		@Override
		public void run() {
			gui.destroy();
		}
	}
	
	/**
	 * The plugin owning this GUI
	 */
	protected final JavaPlugin plugin;
	
	/**
	 * The backing minecraft inventory
	 */
	private final Inventory inventory;
	/**
	 * The slots that are open for items to be placed in and moved out of (0-indexed) this gets ignored by the pagination gui if used in the same inventory region, (this does not include {@link #returnItems} this will always return items in open slots even if they include pagination slots)
	 */
	protected final Set<Integer> openSlots = new LinkedHashSet<>();
	/**
	 * Runs when the GUI is destroyed (should be used to clean up any resources)
	 */
	private Runnable onDestroy;
	
	/**
	 * Runs when an inventory click is made (before any other click action or button action) can be used to debug or do something specific no matter what is being clicked this does not adhere to {@link #disabledClickEvents} and simply gets called for every event inside the menu
	 */
	private Consumer<InventoryClickEvent> onClick = e -> {
	};
	/**
	 * Runs when an inventory click is made in an open slot (0-indexed), list of all slots affected by the click
	 */
	private BiConsumer<InventoryClickEvent, List<Integer>> onClickOpenSlot = (e, i) -> {
	};
	/**
	 * Runs when an inventory click is made in an open slot (0-indexed)
	 */
	private Consumer<InventoryDragEvent> onDragOpenSlot = e -> {
	};
	
	/**
	 * Runs when a player clicks in the player inventory while the GUI is open
	 */
	private Consumer<InventoryClickEvent> onPlayerInventoryClick = e -> {
	};
	
	/**
	 * The buttons in the GUI (0-indexed)
	 */
	private final Map<Integer, Button> buttons = new HashMap<>();
	/**
	 * Handles all possible clicks in the GUI.
	 */
	private final List<ClickActionData> clickHandlers = new ArrayList<>();
	
	private final Set<ClickType> disabledClickEvents = new HashSet<>();
	
	/**
	 * The pagination GUIs that are part of this GUI (if any)
	 */
	private final Set<PaginationGui> paginationGuis = new HashSet<>();
	
	/**
	 * The Owning players inventory profile
	 */
	protected T profile;
	
	public static final int MAX_ROWS = 9;
	public static final int MAX_COLUMNS = 6;
	
	/**
	 * Whether or not items in open slots are returned to the player when this inventory is destroyed, should be set to true for this to properly return the items otherwise it will only return items when the inventory is manually closed using {@link #destroy()}
	 */
	private boolean returnItems = true;
	
	/**
	 * Sets whether this GUI is destroyed when it has been closed by all viewers
	 */
	private boolean destroyOnClose = true;
	
	/**
	 * Whether or not the GUI has been destroyed (this menu should not be used anymore if it was marked as destroyed)
	 */
	private boolean isDestroyed = false;
	
	/**
	 * Creates a new GUI from an inventory
	 *
	 * @param inventory The inventory to create a GUI from
	 */
	protected GuiInventory(Inventory inventory, JavaPlugin plugin, T profile) {
		this.plugin = plugin;
		this.profile = profile;
		this.inventory = inventory;
		cleaner.register(this, new CleanupTask(this));
		Bukkit.getPluginManager().registerEvents(this, plugin);
		registerDefaultClicks();
	}
	
	/**
	 * Creates a new GUI, instantiating a new inventory with the given size and name
	 *
	 * @param size The size of the inventory
	 * @param name The name of the inventory
	 */
	protected GuiInventory(int size, Component name, JavaPlugin plugin, T profile) {
		this(Bukkit.createInventory(null, size, name), plugin, profile);
	}
	
	/**
	 * Creates a new GUI, instantiating a new inventory with the given size and name
	 *
	 * @param inventorySize The size of the inventory
	 * @param name The name of the inventory
	 */
	protected GuiInventory(InventorySize inventorySize, Component name, JavaPlugin plugin, T profile) {
		this(Bukkit.createInventory(null, inventorySize.getSize(), name), plugin, profile);
	}
	
	/**
	 * Creates a new GUI from an inventory
	 *
	 * @param inventory The inventory to create a GUI from
	 */
	protected GuiInventory(Inventory inventory, JavaPlugin plugin, Player player) {
		this(inventory, plugin, (T) new MenuProfile(player));
		
	}
	
	/**
	 * Creates a new GUI, instantiating a new inventory with the given size and name
	 *
	 * @param size The size of the inventory
	 * @param name The name of the inventory
	 */
	protected GuiInventory(int size, Component name, JavaPlugin plugin, Player player) {
		this(Bukkit.createInventory(null, size, name), plugin, player);
	}
	
	/**
	 * Creates a new GUI, instantiating a new inventory with the given size and name
	 *
	 * @param inventorySize The size of the inventory
	 * @param name The name of the inventory
	 */
	protected GuiInventory(InventorySize inventorySize, Component name, JavaPlugin plugin, Player player) {
		this(Bukkit.createInventory(null, inventorySize.getSize(), name), plugin, player);
	}
	
	/**
	 * This is called when the GUI is opened, add all the components to the GUI here
	 */
	public abstract void addComponents();
	
	/**
	 * Add a button to the GUI in the given slot
	 *
	 * @param button The button to be added
	 * @param slot The slot to add the button to
	 */
	public void addButton(Button button, int slot) {
		button.setSlot(slot);
		inventory.setItem(slot, button.getItem());
		buttons.put(slot, button);
	}
	
	/**
	 * Sets the item in the given slot
	 *
	 * @param item The item to set
	 * @param slot The slot to set the item in
	 */
	public void addItem(ItemStack item, int slot) {
		inventory.setItem(slot, item);
		buttons.put(slot, null);
	}
	
	/**
	 * Sets the item in the given slot
	 *
	 * @param slot The slot to set the item in
	 * @param item The item to sets
	 */
	public void addItem(int slot, ItemStack item) {
		addItem(item, slot);
	}
	
	/**
	 * Add a button to the GUI in the given slot
	 *
	 * @param button The button to be added
	 * @param slot The slot to add the button to
	 */
	public void addButton(int slot, Button button) {
		addButton(button, slot);
	}
	
	/**
	 * Add a button at the given position in the inventory
	 *
	 * @param button The button to be added
	 * @param x The X position to add the button at
	 * @param y The Y position to add the button at
	 */
	public void addButton(Button button, int x, int y) {
		int slot = x + (y * MAX_ROWS);
		addButton(button, slot);
	}
	
	/**
	 * Fills the inventory with the given item
	 *
	 * @param item The item to set
	 */
	public void fill(ItemStack item) {
		fill(0, inventory.getSize() - 1, item);
	}
	
	/**
	 * Fill a section of the inventory with the given button
	 *
	 * @param button The button to set in these slots
	 */
	public void fill(Button button) {
		fill(0, inventory.getSize() - 1, button);
	}
	
	/**
	 * Fill a section of the inventory with the given item
	 *
	 * @param start The starting index to fill from, inclusive
	 * @param end The ending index to fill to, inclusive
	 * @param item The item to set in these slots
	 */
	public void fill(int start, int end, ItemStack item) {
		for(int i = start; i <= end; i++){
			inventory.setItem(i, item == null ? null : item.clone());
		}
	}
	
	/**
	 * Fill a section of the inventory with the given button
	 *
	 * @param start The starting index to fill from, inclusive
	 * @param end The ending index to fill to, inclusive
	 * @param button The button to set in these slots
	 */
	public void fill(int start, int end, Button button) {
		for(int i = start; i <= end; i++){
			addButton(button, i);
		}
	}
	
	/**
	 * Fill a section of the inventory with the given item
	 *
	 * @param x1 The X position to fill from, inclusive
	 * @param y1 The Y position to fill from, inclusive
	 * @param x2 The X position to fill to, inclusive
	 * @param y2 The Y position to fill to, inclusive
	 * @param item The item to set in these slots
	 */
	public void fill(int x1, int y1, int x2, int y2, ItemStack item) {
		for(int x = x1; x <= x2; x++){
			for(int y = y1; y <= y2; y++){
				inventory.setItem(x + (y * 9), item == null ? null : item.clone());
			}
		}
	}
	
	/**
	 * Fill a section of the inventory with the given button
	 *
	 * @param x1 The X position to fill from, inclusive
	 * @param y1 The Y position to fill from, inclusive
	 * @param x2 The X position to fill to, inclusive
	 * @param y2 The Y position to fill to, inclusive
	 * @param button The button to set in these slots
	 */
	public void fill(int x1, int y1, int x2, int y2, Button button) {
		for(int x = x1; x <= x2; x++){
			for(int y = y1; y <= y2; y++){
				addButton(button, x, y);
			}
		}
	}
	
	/**
	 * Remove a button from the inventory
	 *
	 * @param button The button to be removed
	 */
	public void removeButton(Button button) {
		inventory.setItem(button.getSlot(), new ItemStack(Material.AIR));
		buttons.remove(button.getSlot());
	}
	
	/**
	 * Remove a button from the inventory
	 *
	 * @param slot Slot to be removed
	 */
	public void removeButton(int slot) {
		inventory.setItem(slot, new ItemStack(Material.AIR));
		buttons.remove(slot);
	}
	
	/**
	 * @return All the ItemButtons in this GUI
	 */
	public List<Button> getButtons() {
		return new ArrayList<>(buttons.values());
	}
	
	/**
	 * Gets the ItemButton in a given slot
	 *
	 * @param slot The slot the button is in
	 * @return The ItemButton, or null if there is no button in that slot
	 */
	public Button getButton(int slot) {
		return buttons.get(slot);
	}
	
	/**
	 * Clears a single slot, removing a button if it is present
	 *
	 * @param slot The slot to clear
	 */
	public void clearSlot(int slot) {
		Button button = buttons.get(slot);
		if(button != null){
			removeButton(button);
			return;
		}
		inventory.setItem(slot, new ItemStack(Material.AIR));
	}
	
	/**
	 * Refresh the inventory.
	 */
	public void update() {
		for(Button button : buttons.values()){
			inventory.setItem(button.getSlot(), button.getItem());
		}
	}
	
	/**
	 * Opens all slots so that items can be placed in them (by default all open slots will be returned to the player when the inventory is closed, can be toggled using {@link #setReturnItems(boolean)})
	 */
	public void openAllSlots() {
		for(int i = 0; i < inventory.getSize(); i++){
			openSlots.add(i);
		}
	}
	
	/**
	 * Opens a slot so that items can be placed in it (by default all open slots will be returned to the player when the inventory is closed, can be toggled using {@link #setReturnItems(boolean)})
	 *
	 * @param slot The slot to open
	 */
	public void openSlot(int slot) {
		openSlots.add(slot);
	}
	
	/**
	 * Opens slots so that items can be placed in them
	 *
	 * @param start The start of the open slot section, inclusive  (0-indexed)
	 * @param end The end of the open slot section, inclusive  (0-indexed)
	 */
	public void openSlots(int start, int end) {
		for(int i = start; i <= end; i++){
			openSlots.add(i);
		}
	}
	
	/**
	 * Opens slots so that items can be placed in them
	 *
	 * @param x1 The x position to open from, inclusive  (0-indexed)
	 * @param y1 The y position to open from, inclusive  (0-indexed)
	 * @param x2 The x position to open to, inclusive  (0-indexed)
	 * @param y2 The y position to open to, inclusive  (0-indexed)
	 */
	public void openSlots(int x1, int y1, int x2, int y2) {
		for(int y = y1; y <= y2; y++){
			for(int x = x1; x <= x2; x++){
				openSlots.add(y * MAX_ROWS + x);
			}
		}
	}
	
	/**
	 * Closes a slot so that items can't be placed in it
	 *
	 * @param slot The slot to open
	 */
	public void closeSlot(int slot) {
		openSlots.remove(slot);
	}
	
	/**
	 * Closes slots so that items can't be placed in them
	 *
	 * @param start The start of the closed slot section, inclusive  (0-indexed)
	 * @param end The end of the open closed section, inclusive  (0-indexed)
	 */
	public void closeSlots(int start, int end) {
		for(int i = start; i <= end; i++){
			openSlots.remove(i);
		}
	}
	
	/**
	 * Closes slots so that items can't be placed in them
	 *
	 * @param x1 The x position to close from, inclusive  (0-indexed)
	 * @param y1 The y position to close from, inclusive  (0-indexed)
	 * @param x2 The x position to close to, inclusive  (0-indexed)
	 * @param y2 The y position to close to, inclusive  (0-indexed)
	 */
	public void closeSlots(int x1, int y1, int x2, int y2) {
		for(int y = y1; y <= y2; y++){
			for(int x = x1; x <= x2; x++){
				openSlots.remove(y * MAX_ROWS + x);
			}
		}
	}
	
	/**
	 * Opens this GUI for a player
	 */
	public void open() {
		addComponents();
		GuiManager.addMenu(getPlayer().getUniqueId(), this);
		profile.getOwner().openInventory(inventory);
	}
	
	/**
	 * Sets the handler for when an open slot is clicked
	 *
	 * @param handler The handler for when an open slot is clicked
	 */
	public void setOnClickOpenSlot(Consumer<InventoryClickEvent> handler) {
		this.onClickOpenSlot = (e, i) -> handler.accept(e);
	}
	
	/**
	 * Sets the handler for when an open slot is clicked
	 *
	 * @param handler The handler for when an open slot is clicked, taking the event and list of affected slots
	 */
	public void setOnClickOpenSlot(BiConsumer<InventoryClickEvent, List<Integer>> handler) {
		this.onClickOpenSlot = handler;
	}
	
	/**
	 * Remove this inventory as a listener and clean everything up to prevent memory leaks. Call this when the GUI is no longer being used.
	 *
	 * @param lastViewer The last Player who was viewing this GUI, to have the items returned to them.
	 */
	public void destroy(Player lastViewer) {
		if(isDestroyed){
			return;
		}
		
		isDestroyed = true;
		
		if(onDestroy != null){
			onDestroy.run();
		}
		
		if(returnItems && lastViewer != null){
			for(int slot : openSlots){
				ItemStack item = inventory.getItem(slot);
				if(item != null){
					lastViewer.getInventory().addItem(item).values().forEach(remainingItem -> lastViewer.getWorld()
																										.dropItem(lastViewer.getLocation(),
																												remainingItem));
				}
			}
		}
		
		inventory.clear();
		buttons.clear();
		
		GuiManager.cleanup(getPlayer().getUniqueId());
		HandlerList.unregisterAll(this);
	}
	
	/**
	 * Remove this inventory as a listener and clean everything up to prevent memory leaks. Call this when the GUI is no longer being used.
	 */
	public void destroy() {
		destroy(getPlayer());
	}
	
	/**
	 * Clears the inventory and its buttons
	 */
	public void clear() {
		inventory.clear();
		buttons.clear();
	}
	
	@EventHandler
	public void onDrag(InventoryDragEvent e) {
		List<Integer> slots = e.getRawSlots().stream().filter(s -> getInventory(e.getView(), s).equals(inventory)).toList();
		if(slots.isEmpty()){
			return;
		}
		if(!openSlots.containsAll(slots)){
			e.setCancelled(true);
			return;
		}
		onDragOpenSlot.accept(e);
	}
	
	public Inventory getInventory(InventoryView view, int rawSlot) {
		return rawSlot < view.getTopInventory().getSize() ? view.getTopInventory() : view.getBottomInventory();
	}
	
	/**
	 * Checks if the slot is a slot handled by a pagination GUI
	 *
	 * @return Whether the slot is a pagination slot
	 */
	private boolean isPaginationSlot(InventoryClickEvent e) {
		for(PaginationGui paginationGui : paginationGuis){
			if(paginationGui.getSlots().contains(e.getSlot())){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Gets the pagination GUI that is handling the slot
	 *
	 * @return The pagination GUI handling the slot or null if no pagination GUI is handling the slot
	 */
	private PaginationGui getHandlingPaginationGui(InventoryClickEvent e) {
		for(PaginationGui paginationGui : paginationGuis){
			if(paginationGui.getSlots().contains(e.getSlot())){
				return paginationGui;
			}
		}
		return null;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) { //NOSONAR
		if(!inventory.equals(e.getView().getTopInventory())){
			return;
		}
		if(disabledClickEvents.contains(e.getClick())){
			e.setCancelled(true);
			return;
		}
		
		if(onClick != null){
			onClick.accept(e);
		}
		
		//if its a pagination button let the pagination gui handle it
		PaginationGui paginationGui = getHandlingPaginationGui(e);
		if(paginationGui != null){
			
			//if the raw slot is bigger than the top inventory size, it means the click was in the bottom inventory so invalid
			if(e.getRawSlot() >= e.getView().getTopInventory().getSize()){
				return;
			}
			
			//the button that was clicked (this works as the pagination gui registers the buttons in this menu so it can get the button from the slot directly)
			Button potentialButton = buttons.get(e.getRawSlot());
			Object object = potentialButton != null ? potentialButton : getInventory().getItem(e.getRawSlot());
			
			int position = -1;
			
			if(object instanceof Button button){
				position = paginationGui.getPosition(button);
			} else if(object instanceof ItemStack itemStack){
				position = paginationGui.getPosition(itemStack);
			}
			
			if(position == -1){
				position = paginationGui.getEntrySize();
			}
			
			paginationGui.onInventoryEvent(e, object, position);
			return;
		}
		
		if(onPlayerInventoryClick != null){
			onPlayerInventoryClick.accept(e);
		}
		
		for(ClickActionData data : clickHandlers){
			if(data.isValid.test(e, this) && data.action.test(e, this)){
				return;
			}
		}
	}
	
	/**
	 * DOES NOT NEED TO BE CALLED MANUALLY, automatically called on {@link PaginationGui} initialization
	 */
	public void addPaginationGui(PaginationGui paginationGui) {
		paginationGuis.add(paginationGui);
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if(e.getInventory().equals(inventory) && destroyOnClose && e.getViewers().size() <= 1){
			destroy((Player) e.getPlayer());
		}
	}
	
	//---------------Default Click Handlers----------------
	
	public void registerDefaultClicks() {
		clickHandlers.add(onDefaultInventoryClick);
		clickHandlers.add(onDefaultShiftClick);
		updateClickHandlers();
	}
	
	/**
	 * Handles all clicks in the GUI's inventory lowest prio as its just the average
	 */
	private final ClickActionData onDefaultInventoryClick = new ClickActionData(0,
			(e, gui) -> gui.getInventory().equals(e.getClickedInventory()),
			(e, gui) -> {
				if(openSlots.contains(e.getSlot())){
					List<Integer> list = new ArrayList<>();
					list.add(e.getSlot());
					onClickOpenSlot.accept(e, list);
					return true;
				}
				e.setCancelled(true);
				Button button = buttons.get(e.getSlot());
				if(button != null){
					button.onClick(e);
				}
				return true;
			});
	
	/**
	 * Handles all shift clicks in the GUI's inventory
	 */
	private final ClickActionData onDefaultShiftClick = new ClickActionData(1,
			(e, gui) -> !gui.getInventory().equals(e.getClickedInventory()) && e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY,
			(e, gui) -> {
				if(!openSlots.isEmpty()){
					Map<Integer, ItemStack> slots = new HashMap<>();
					int amount = Objects.requireNonNull(e.getCurrentItem()).getAmount();
					for(int slot : openSlots){ //NOSONAR
						if(amount <= 0){
							break;
						}
						ItemStack item = getInventory().getItem(slot);
						if(item == null){
							int diff = Math.min(amount, e.getCurrentItem().getType().getMaxStackSize());
							amount -= diff;
							ItemStack clone = e.getCurrentItem().clone();
							clone.setAmount(diff);
							slots.put(slot, clone);
							continue;
						}
						if(e.getCurrentItem().isSimilar(item)){
							int max = item.getType().getMaxStackSize() - item.getAmount();
							int diff = Math.min(max, e.getCurrentItem().getAmount());
							amount -= diff;
							ItemStack clone = item.clone();
							clone.setAmount(clone.getAmount() + diff);
							slots.put(slot, clone);
						}
					}
					if(slots.isEmpty()){
						return true;
					}
					onClickOpenSlot.accept(e, new ArrayList<>(slots.keySet()));
					if(e.isCancelled()){
						return true;
					}
					e.setCancelled(true);
					ItemStack item = e.getCurrentItem();
					item.setAmount(amount);
					e.setCurrentItem(item);
					slots.forEach(getInventory()::setItem);
					Bukkit.getScheduler().scheduleSyncDelayedTask(gui.getPlugin(), this::update);
					return true;
				}
				e.setCancelled(true);
				return true;
			});
	
	/**
	 * Registers a click handler for a specific action
	 *
	 * @param weight The weight of the handler (Higher weights are called first)
	 * @param isValid The predicate that determines if the action is valid
	 * @param action The action to run if the action is valid (Return true to consume the event and prevent any further click actions from trying to run after, false to let them run)
	 */
	public record ClickActionData(int weight, BiPredicate<InventoryClickEvent, GuiInventory<?>> isValid,
								  BiPredicate<InventoryClickEvent, GuiInventory<?>> action){}
	
	public void addAction(ClickActionData data) {
		clickHandlers.add(data);
		updateClickHandlers();
	}
	
	private void updateClickHandlers() {
		clickHandlers.sort(Comparator.comparingInt(ClickActionData::weight));
	}
	
	/**
	 * Disables a click event in the GUI
	 *
	 * @param clickType the type of click to disable (for example double clicks to prevent a third click from happening as spigot fires 2 single clicks and a double click event for a double click)
	 */
	public void disableClickEvent(ClickType... clickType) {
		disabledClickEvents.addAll(Arrays.asList(clickType));
	}
	
	/**
	 * Enables a click event in the GUI (by default all click events are enabled)
	 *
	 * @param clickType the type of click to enable
	 */
	public void enableClickEvent(ClickType... clickType) {
		for(ClickType type : clickType){
			disabledClickEvents.remove(type);
		}
	}
	
	public void clearPaginationGui(PaginationGui paginationGui) {
		paginationGuis.remove(paginationGui);
		paginationGui.clear();
	}
	
	/**
	 * Clears all pagination GUIs assigned to this GUI
	 */
	public void clearPaginationGuis() {
		for(PaginationGui paginationGui : paginationGuis){
			paginationGui.clear();
		}
		paginationGuis.clear();
	}
	
	public boolean isDestroyed() {
		return isDestroyed;
	}
	
	public Inventory getInventory() {return inventory;}
	
	public void setOnDestroy(Runnable onDestroy) {
		this.onDestroy = onDestroy;
	}
	
	public void setOnClick(Consumer<InventoryClickEvent> onClick) {
		this.onClick = onClick;
	}
	
	public void setOnDragOpenSlot(Consumer<InventoryDragEvent> onDragOpenSlot) {this.onDragOpenSlot = onDragOpenSlot;}
	
	public void setOnPlayerInventoryClick(Consumer<InventoryClickEvent> onPlayerInventoryClick) {
		this.onPlayerInventoryClick = onPlayerInventoryClick;
	}
	
	public JavaPlugin getPlugin() {return plugin;}
	
	public Player getPlayer() {return profile.getOwner();}
	
	public T getProfile() {return profile;}
	
	public void setReturnItems(boolean returnItems) {this.returnItems = returnItems;}
	
	public void setDestroyOnClose(boolean destroyOnClose) {this.destroyOnClose = destroyOnClose;}
	
	public boolean isReturnItems() {return returnItems;}
	
	public boolean isDestroyOnClose() {return destroyOnClose;}
}
	
