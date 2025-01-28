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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.ref.Cleaner;
import java.util.ArrayList;
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
	
	private static class CleanupTask implements Runnable{
		private final GuiInventory<?> gui;
		
		public CleanupTask(GuiInventory<?> gui) {
			this.gui = gui;
		}
		
		@Override
		public void run() {
			gui.destroy();
		}
	}
	
	public static final ItemStack BLACK_FILLER = createFiller(Material.BLACK_STAINED_GLASS_PANE);
	public static final ItemStack GRAY_FILLER = createFiller(Material.GRAY_STAINED_GLASS_PANE);
	public static final ItemStack WHITE_FILLER = createFiller(Material.WHITE_STAINED_GLASS_PANE);
	public static final ItemStack RED_FILLER = createFiller(Material.RED_STAINED_GLASS_PANE);
	public static final ItemStack GREEN_FILLER = createFiller(Material.GREEN_STAINED_GLASS_PANE);
	public static final ItemStack BLUE_FILLER = createFiller(Material.BLUE_STAINED_GLASS_PANE);
	public static final ItemStack YELLOW_FILLER = createFiller(Material.YELLOW_STAINED_GLASS_PANE);
	public static final ItemStack ORANGE_FILLER = createFiller(Material.ORANGE_STAINED_GLASS_PANE);
	public static final ItemStack PURPLE_FILLER = createFiller(Material.PURPLE_STAINED_GLASS_PANE);
	public static final ItemStack CYAN_FILLER = createFiller(Material.CYAN_STAINED_GLASS_PANE);
	public static final ItemStack PINK_FILLER = createFiller(Material.PINK_STAINED_GLASS_PANE);
	public static final ItemStack LIME_FILLER = createFiller(Material.LIME_STAINED_GLASS_PANE);
	public static final ItemStack LIGHT_BLUE_FILLER = createFiller(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
	public static final ItemStack MAGENTA_FILLER = createFiller(Material.MAGENTA_STAINED_GLASS_PANE);
	public static final ItemStack BROWN_FILLER = createFiller(Material.BROWN_STAINED_GLASS_PANE);
	public static final ItemStack LIGHT_GRAY_FILLER = createFiller(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
	public static final ItemStack CYAN_STAINED_GLASS_PANE = createFiller(Material.CYAN_STAINED_GLASS_PANE);
	
	/**
	 * The player who owns this GUI
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
	 * Runs when a player clicks in the player inventory
	 * -- SETTER --
	 * Sets the handler for when a slot in the player inventory is clicked (default behaviour is to cancel the event)
	 *
	 * @param onPlayerInventoryClick The handler for when a slot in the player inventory is clicked
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
	 * -- GETTER --
	 *
	 * @return The Owning players inventory profile
	 */
	protected T profile;
	/**
	 * -- GETTER --
	 *
	 * @return The Owning player
	 */
	protected Player player;
	private static final int MAX_ROWS = 9;
	private static final int MAX_COLUMNS = 6;
	
	private boolean returnItems = true;
	/**
	 * -- SETTER --
	 * Sets whether this GUI is destroyed when it has been closed by all viewers
	 *
	 * @param destroyOnClose Whether this GUI is destroyed when it has been closed by all viewers
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
		//Add profile to constructor, avoids nullpointer exception if profile is used in constructor
		this.plugin = plugin;
		this.profile = profile;
		this.inventory = inventory;
		this.player = profile.getOwner();
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
	
	public int validateX(int x) {
		return Math.min(Math.max(x, 0), (inventory.getSize() / MAX_ROWS));
	}
	
	public int validateY(int y) {
		return Math.min(Math.max(y, 0), MAX_COLUMNS);
	}
	
	public int validateFitting(int slot) {
		return Math.min(Math.max(slot, 0), inventory.getSize());
	}
	
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
	 * Opens all slots so that items can be placed in them (by default all open slots will be returned to the player when the inventory is closed, can be toggled using {@link #setReturnsItems(boolean)})
	 */
	public void openAllSlots() {
		for(int i = 0; i < inventory.getSize(); i++){
			openSlots.add(i);
		}
	}
	
	/**
	 * Opens a slot so that items can be placed in it (by default all open slots will be returned to the player when the inventory is closed, can be toggled using {@link #setReturnsItems(boolean)})
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
		GuiManager.addMenu(player.getUniqueId(), this);
		profile.getOwner().openInventory(inventory);
	}
	
	/**
	 * Returns whether or not items in open slots are returned to the player when this inventory is destroyed
	 *
	 * @return Whether or not items in open slots are returned to the player when this inventory is destroyed
	 */
	public boolean returnsItems() {
		return returnItems;
	}
	
	/**
	 * Sets whether items in open slots are returned to the player when this inventory is destroyed
	 *
	 * @param returnItems Whether items in open slots should be returned to the player when this inventory is destroyed
	 */
	public void setReturnsItems(boolean returnItems) {
		this.returnItems = returnItems;
	}
	
	/**
	 * Returns whether this GUI is destroyed when it has been closed by all viewers
	 *
	 * @return Whether this GUI is destroyed when it has been closed by all viewers
	 */
	public boolean destroysOnClose() {
		return destroyOnClose;
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
		
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			
			if(onDestroy != null){
				onDestroy.run();
			}
			HandlerList.unregisterAll(this);
			
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
			
			GuiManager.cleanup(player.getUniqueId());
		}, 1);
		
	}
	
	/**
	 * Remove this inventory as a listener and clean everything up to prevent memory leaks. Call this when the GUI is no longer being used.
	 */
	public void destroy() {
		destroy(null);
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
	public void onClick(InventoryClickEvent e) {
		if(!inventory.equals(e.getView().getTopInventory())){
			return;
		}
		
		if(onClick != null){
			onClick.accept(e);
		}
		
		if(disabledClickEvents.contains(e.getClick())){
			e.setCancelled(true);
			return;
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
			if(data.isValid.test(e, this)){
				if(data.action.test(e, this)){
					return;
				}
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
		if(e.getInventory().equals(inventory) && destroyOnClose){
			if(e.getViewers().size() <= 1){
				destroy((Player) e.getPlayer());
			}
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
			(e, gui) -> !gui.equals(e.getClickedInventory()) && e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY,
			(e, gui) -> {
				if(!openSlots.isEmpty()){
					Inventory inventory = gui.getInventory();
					Map<Integer, ItemStack> slots = new HashMap<>();
					int amount = Objects.requireNonNull(e.getCurrentItem()).getAmount();
					for(int slot : openSlots){
						if(amount <= 0){
							break;
						}
						ItemStack item = inventory.getItem(slot);
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
					slots.forEach(inventory::setItem);
					Bukkit.getScheduler().scheduleSyncDelayedTask(gui.getPlugin(), () -> {
						((Player) e.getWhoClicked()).updateInventory();
					});
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
	
	public void updateClickHandlers() {
		clickHandlers.sort(Comparator.comparingInt(ClickActionData::weight));
	}
	
	/**
	 * Utility Method to create a filler item for a gui (removes tooltip)
	 *
	 * @param material The material to create the filler ItemStack with
	 * @return The filler ItemStack
	 */
	public static ItemStack createFiller(Material material) {
		ItemStack filler = new ItemStack(material);
		ItemMeta meta = filler.getItemMeta();
		meta.setHideTooltip(true);
		filler.setItemMeta(meta);
		return filler;
	}
	
	/**
	 * Disables a click event in the GUI
	 *
	 * @param clickType the type of click to disable (for example double clicks to prevent a third click from happening as spigot fires 2 single clicks and a double click event for a double click)
	 */
	public void disableClickEvent(ClickType... clickType) {
		for(ClickType type : clickType){
			disabledClickEvents.add(type);
		}
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
	
	/**
	 * Clears all pagination GUIs assigned to this GUI
	 */
	public void clearPaginationGuis() {
		paginationGuis.clear();
	}
	
	/**
	 * @return {@link #isDestroyed}
	 */
	public boolean isDestroyed() {
		return isDestroyed;
	}
	
	public Inventory getInventory() {
		return inventory;
	}
	
	/**
	 * @param onDestroy {@link #onDestroy}
	 */
	public void setOnDestroy(Runnable onDestroy) {
		this.onDestroy = onDestroy;
	}
	
	/**
	 *
	 * @param onClick {@link #onClick}
	 */
	public void setOnClick(Consumer<InventoryClickEvent> onClick) {
		this.onClick = onClick;
	}
	
	/**
	 *
	 * @param onDragOpenSlot {@link #onDragOpenSlot}
	 */
	public void setOnDragOpenSlot(Consumer<InventoryDragEvent> onDragOpenSlot) {
		this.onDragOpenSlot = onDragOpenSlot;
	}
	
	/**
	 *
	 * @param onPlayerInventoryClick {@link #onPlayerInventoryClick}
	 */
	public void setOnPlayerInventoryClick(Consumer<InventoryClickEvent> onPlayerInventoryClick) {
		this.onPlayerInventoryClick = onPlayerInventoryClick;
	}
	
	public JavaPlugin getPlugin() {
		return plugin;
	}
	
	public Player getPlayer() {
		return player;
	}
}
	
