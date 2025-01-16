package com.wonkglorg.utilitylib.inventory;

import com.wonkglorg.utilitylib.inventory.profile.MenuProfile;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;

/**
 * A panel in an InventoryGUI which can be used to paginate items and buttons
 *
 * @author Redempt
 */
@SuppressWarnings("unused")
public final class PaginationGui{
	//todo:jmd open specific pagination slots?
	private GuiInventory gui;
	private int page = 1;
	
	/**
	 * A list that represents the entries in the panel the pagination is based on this lists ordering (if this ordering is modified so will the panel elements order) (to create an empty panel slot use null or leave the {@link PaginationEntry#object} null)
	 */
	private final ArrayList<PaginationEntry> entries = new ArrayList<>();
	/**
	 * All Slots this panel uses to display elements
	 */
	private final Set<Integer> slots = new TreeSet<>();
	/**
	 * Runs on every update of the page
	 */
	private Runnable onUpdate = () -> {
	};
	/**
	 * The item to use to fill the rest with the empty slots
	 */
	private ItemStack fillerItem;
	
	/**
	 * The maximum number of rows and columns in the panel
	 */
	private static final int maxRows = 9;
	/**
	 * The maximum number of columns in the panel
	 */
	private static final int maxColumns = 6;
	/**
	 * The previous button assigned to this panel (is not required only for convenience in #updatePageButtons)
	 */
	private Button previousButton;
	/**
	 * The Next button assigned to this panel (is not required only for convenience in #updatePageButtons)
	 */
	private Button nextButton;
	
	/**
	 * A consumer that is called when an item is inserted into the panel
	 */
	private BiConsumer<ItemStack, Integer> onItemInsert = (item, index) -> entries.add(index,
			new PaginationEntry(item, i -> gui.getInventory().setItem(i, item), false));
	private Map<InventoryAction, Consumer<ClickData>> clickActions = new HashMap<>();
	
	//todo implement open slots to take from? would be pretty hard if the gui inventory handles it and not the pagination
	
	private boolean allowInsertion = false;
	
	/**
	 * Constructs a PaginationPanel to work on a given InventoryGUI
	 *
	 * @param gui The InventoryGUI to paginate
	 */
	public PaginationGui(GuiInventory<MenuProfile> gui) {
		this(gui, null);
		gui.addPaginationGui(this);
		registerActions();
	}
	
	/**
	 * Constructs a PaginationPanel to work on a given InventoryGUI
	 *
	 * @param gui The InventoryGUI to paginate
	 * @param fillerItem The item to use for the background
	 */
	public PaginationGui(GuiInventory<MenuProfile> gui, ItemStack fillerItem) {
		this.gui = gui;
		this.fillerItem = fillerItem;
	}
	
	/**
	 * Sets a task to be run whenever the page updates, can be used to update a page indicator or similar
	 *
	 * @param onUpdate The task to be run on update
	 */
	public void setOnUpdate(Runnable onUpdate) {
		this.onUpdate = onUpdate;
	}
	
	private void addPagedButton0(Button button) {
		entries.add(new PaginationEntry(button, i -> gui.addButton(button, i), false));
	}
	
	/**
	 * Adds a paged button to the panel
	 *
	 * @param button The button to add
	 */
	public void addPagedButton(Button button) {
		addPagedButton0(button);
		updatePage();
	}
	
	private void addPagedItem0(ItemStack item) {
		entries.add(new PaginationEntry(item, i -> gui.getInventory().setItem(i, item), false));
	}
	
	private void addPagedItem0(ItemStack item, boolean isOpen) {
		entries.add(new PaginationEntry(item, i -> gui.getInventory().setItem(i, item), isOpen));
	}
	
	/**
	 * Adds a paged item to the panel
	 *
	 * @param item The item to add
	 */
	public void addPagedItem(ItemStack item) {
		addPagedItem0(item);
		updatePage();
	}
	
	/**
	 * Adds multiple buttons to the paged panel
	 *
	 * @param buttons The buttons to add
	 */
	public void addPagedButtons(Iterable<Button> buttons) {
		for(Button button : buttons){
			addPagedButton0(button);
		}
		updatePage();
	}
	
	/**
	 * Adds multiple items to the paged panel
	 *
	 * @param items The items to add
	 */
	public void addPagedItems(Iterable<ItemStack> items) {
		for(ItemStack item : items){
			addPagedItem0(item);
		}
		updatePage();
	}
	
	/**
	 * Removes an item from the paged panel.
	 *
	 * @param item The item to remove
	 */
	public void removePagedItem(ItemStack item) {
		entries.removeIf(e -> e.object.equals(item));
		updatePage();
	}
	
	/**
	 * Removes a button from the paged panel.
	 *
	 * @param button The button to remove
	 */
	public void removePagedButton(Button button) {
		entries.removeIf(e -> e.object.equals(button));
		updatePage();
	}
	
	/**
	 * Removes multiple items from the paged panel
	 *
	 * @param items The items to remove
	 */
	public void removePagedItems(Iterable<ItemStack> items) {
		for(ItemStack item : items){
			entries.removeIf(e -> e.object.equals(item));
		}
		updatePage();
	}
	
	/**
	 * Removes multiple buttons from the paged panel
	 *
	 * @param buttons The buttons to remove
	 */
	public void removePagedButtons(Iterable<Button> buttons) {
		for(Button button : buttons){
			entries.removeIf(e -> e.object.equals(button));
		}
		updatePage();
	}
	
	/**
	 * Removes an item from the panel
	 *
	 * @param index The index of the item to remove
	 */
	public void removeByIndex(int index) {
		entries.remove(index);
		updatePage();
	}
	
	/**
	 * Adds an item to the panel at a specific index
	 *
	 * @param index The index to add the item at
	 * @param item The item to add
	 */
	public void addAtPosition(int index, ItemStack item) {
		ensureCapacity(index + 1);
		PaginationEntry paginationEntry = entries.get(index);
		if(paginationEntry == null){
			entries.add(index, new PaginationEntry(item, i -> gui.getInventory().setItem(i, item), false));
			return;
		}
		entries.add(index, new PaginationEntry(item, i -> gui.getInventory().setItem(i, item), paginationEntry.isOpen()));
	}
	
	/**
	 * Adds a button to the panel at a specific index
	 *
	 * @param index The index to add the button at
	 * @param button The button to add
	 */
	public void addAtPosition(int index, Button button) {
		ensureCapacity(index + 1);
		PaginationEntry paginationEntry = entries.get(index);
		if(paginationEntry == null){
			entries.add(index, new PaginationEntry(button, i -> gui.addButton(button, i), false));
			return;
		}
		
		entries.add(index, new PaginationEntry(button, i -> gui.addButton(button, i), paginationEntry.isOpen()));
	}
	
	/**
	 * Ensures the entries list has a capacity of at least size
	 *
	 * @param size The size to ensure
	 */
	private void ensureCapacity(int size) {
		while(entries.size() < size){
			entries.add(null);
		}
	}
	
	/**
	 * Adds an item to the panel at a specific index
	 *
	 * @param index The index to add the item at
	 * @param item The item to add
	 */
	public void updateItem(int index, ItemStack item) {
		if(index >= entries.size()){
			addAtPosition(index, item);
		}
		
		PaginationEntry paginationEntry = entries.get(index);
		
		if(paginationEntry == null){
			entries.add(index, new PaginationEntry(item, i -> gui.getInventory().setItem(i, item), false));
			return;
		}
		
		entries.set(index, new PaginationEntry(item, i -> gui.getInventory().setItem(i, item), paginationEntry.isOpen()));
		updatePage();
	}
	
	/**
	 * Adds a button to the panel at a specific index
	 *
	 * @param index The index to add the button at
	 * @param button The button to add
	 */
	public void updateButton(int index, Button button) {
		if(index >= entries.size()){
			addAtPosition(index, button);
		}
		PaginationEntry paginationEntry = entries.get(index);
		
		if(paginationEntry == null){
			entries.add(index, new PaginationEntry(button, i -> gui.addButton(button, i), false));
			return;
		}
		entries.set(index, new PaginationEntry(button, i -> gui.addButton(button, i), paginationEntry.isOpen()));
		updatePage();
	}
	
	/**
	 * @return The page this panel is currently on
	 */
	public int getPage() {
		return page;
	}
	
	/**
	 * @return The max number of elements displayed on each page
	 */
	public int getPageSize() {
		return slots.size();
	}
	
	/**
	 * @return The maximum page number of this panel with the current number of elements
	 */
	public int getMaxPage() {
		return (Math.max(0, entries.size() - 1) / Math.max(1, slots.size())) + 1;
	}
	
	/**
	 * Adds a slot which will be used to display elements
	 *
	 * @param slot The slot to add
	 */
	public void addSlot(int slot) {
		slots.add(slot);
		updatePage();
	}
	
	/**
	 * Adds a range of slots which will be used to display elements
	 *
	 * @param start The start index of slots to add, inclusive (0-indexed)
	 * @param end The end index of slots to add, inclusive  (0-indexed)
	 */
	public void addSlots(int start, int end) {
		for(int i = start; i <= end; i++){
			slots.add(i);
		}
		updatePage();
	}
	
	/**
	 * Adds a rectangular area of slots which will be used to display elements
	 *
	 * @param x1 The starting X of slots to add, inclusive (0-indexed)
	 * @param y1 The starting Y of slots to add, inclusive (0-indexed)
	 * @param x2 The ending X of slots to add, inclusive (0-indexed)
	 * @param y2 The ending Y of slots to add, inclusive (0-indexed)
	 */
	public void addSlots(int x1, int y1, int x2, int y2) {
		for(int x = x1; x <= x2; x++){
			for(int y = y1; y <= y2; y++){
				slots.add((y * maxRows) + x);
			}
		}
		updatePage();
	}
	
	/**
	 * Removes a slot which will be used to display elements
	 *
	 * @param slot The slot to remove
	 */
	public void removeSlot(int slot) {
		slots.forEach(gui::clearSlot);
		slots.forEach(gui::clearSlot);
		slots.remove(slot);
		updatePage();
	}
	
	/**
	 * Removes a range of slots which will be used to display elements
	 *
	 * @param start The start index of slots to remove, inclusive (0-indexed)
	 * @param end The end index of slots to remove, inclusive (0-indexed)
	 */
	public void removeSlots(int start, int end) {
		slots.forEach(gui::clearSlot);
		for(int i = start; i <= end; i++){
			slots.remove(i);
		}
		updatePage();
	}
	
	/**
	 * Removes a rectangular area of slots which will be used to display elements
	 *
	 * @param x1 The starting X of slots to remove, inclusive (0-indexed)
	 * @param y1 The starting Y of slots to remove, inclusive (0-indexed)
	 * @param x2 The ending X of slots to remove, inclusive (0-indexed)
	 * @param y2 The ending Y of slots to remove, inclusive (0-indexed)
	 */
	public void removeSlots(int x1, int y1, int x2, int y2) {
		slots.forEach(gui::clearSlot);
		for(int x = x1; x <= x2; x++){
			for(int y = y1; y <= y2; y++){
				slots.remove((y * maxRows) + x);
			}
		}
		updatePage();
	}
	
	/**
	 * Updates the elements displayed on the current page
	 */
	public void updatePage() {
		slots.forEach(gui::clearSlot);
		slots.forEach(i -> gui.getInventory().setItem(i, fillerItem));
		if(getPageSize() == 0 || entries.isEmpty()){
			onUpdate.run();
			return;
		}
		int start = (page - 1) * getPageSize();
		int end = Math.min(entries.size(), page * getPageSize());
		Iterator<Integer> iter = slots.iterator();
		for(int i = start; i < end; i++){
			PaginationEntry paginationEntry = entries.get(i);
			if(paginationEntry == null){
				continue;
			}
			if(paginationEntry.object() == null){
				gui.addItem(null, i);
			}
			paginationEntry.adder().accept(iter.next());
		}
		onUpdate.run();
	}
	
	/**
	 * Sets the page of this panel
	 *
	 * @param page The page to set
	 */
	public void setPage(int page) {
		if(page < 1 || page > getMaxPage()){
			throw new IllegalArgumentException("Invalid page: " + page);
		}
		this.page = page;
		updatePage();
	}
	
	/**
	 * Removes all items and buttons from the panel
	 */
	public void clear() {
		entries.clear();
		updatePage();
	}
	
	/**
	 * @return All ItemStacks added to this panel
	 */
	public List<ItemStack> getItems() {
		//@formatter:off
        return entries.stream()
                .map(PaginationEntry::object)
                .filter(ItemStack.class::isInstance)
                .map(ItemStack.class::cast)
                .collect(Collectors.toList());
        //@formatter:on
	}
	
	/**
	 * @return All ItemButtons added to this panel
	 */
	public List<Button> getButtons() {
		//@formatter:off
        return entries.stream()
                .map(PaginationEntry::object)
                .filter(Button.class::isInstance)
                .map(Button.class::cast)
                .collect(Collectors.toList());
        //@formatter:on
	}
	
	/**
	 * @param item the item to check
	 * @return the position in the entries list of the item or -1 if not found
	 */
	public int getPosition(ItemStack item) {
		for(PaginationEntry entry : entries){
			if(entry == null){
				continue;
			}
			
			if(entry.object().equals(item)){
				return entries.indexOf(entry);
			}
		}
		return -1;
	}
	
	/**
	 * @param button the button to check
	 * @return the position in the entries list of the button or -1 if not found
	 */
	public int getPosition(Button button) {
		for(PaginationEntry entry : entries){
			if(entry == null){
				continue;
			}
			if(entry.object().equals(button)){
				return entries.indexOf(entry);
			}
		}
		return -1;
	}
	
	/**
	 * Navigates to the next page, if there is one
	 */
	public void nextPage() {
		page = Math.min(page + 1, getMaxPage());
		updatePage();
	}
	
	/**
	 * Navigates to the previous page, if there is one
	 */
	public void prevPage() {
		page = Math.max(1, page - 1);
		updatePage();
	}
	
	public void setPreviousButton(Button button, int slot) {
		this.previousButton = button;
		button.setSlot(slot);
	}
	
	public void setNextButton(Button button, int slot) {
		this.nextButton = button;
		button.setSlot(slot);
	}
	
	/**
	 * updates the page buttons based on the current page count with an itemstack
	 *
	 * @param fillerItem the item to use as a filler
	 */
	public void updatePageButtons(ItemStack fillerItem) {
		if(previousButton == null || nextButton == null){
			return;
		}
		
		if(getMaxPage() == 1){
			gui.addItem(fillerItem, previousButton.getSlot());
			gui.addItem(fillerItem, nextButton.getSlot());
			return;
		}
		
		if(getPage() > 1){
			gui.addButton(previousButton, previousButton.getSlot());
		} else {
			gui.addItem(fillerItem, previousButton.getSlot());
		}
		
		if(getPage() < getMaxPage()){
			gui.addButton(nextButton, nextButton.getSlot());
		} else {
			gui.addItem(fillerItem, nextButton.getSlot());
		}
		gui.update();
	}
	
	/**
	 * @param event the event
	 * @param object the object reference (Button or ItemStack)
	 * @param index the index of the object in the entries list
	 */
	public void onInventoryEvent(InventoryClickEvent event, Object object, int index) {
		
		if(index < entries.size()){
			PaginationEntry entry = entries.get(index);
			if(!entry.isOpen()){
				event.setCancelled(true);
			}
		}
		
		//todo handle each seperate
		//todo make more customizeable with what buttons do what and what action runs?
		
		if(!allowInsertion){
			return;
		}
		switch(event.getAction()) {
			//needs those cases in case where the items being added are not the same as the ones in the panel
			case SWAP_WITH_CURSOR -> {
				//when a new stack not yet in the clicked slot is added
				System.out.println("Attempt to add item");
				var item = event.getCursor();
				if(item == null){
					return;
				}
				
				System.out.println("Adding item");
				onItemInsert.accept(item.clone(), entries.size());
				event.getCursor().setAmount(0);
				updatePage();
			}
			case PLACE_ALL -> {
				//when a new stack not yet in the clicked slot is added on an empty slot
				System.out.println("Attempt to add item");
				var item = event.getCursor();
				if(item == null){
					return;
				}
				
				if(index >= entries.size()){
					onItemInsert.accept(item.clone(), entries.size());
					item.setAmount(0);
					updatePage();
					return;
				}
				
				ItemStack itemStack = entries.get(index).getItemStack();
				if(itemStack == null){
					return;
				}
				
				itemStack.setAmount(itemStack.getAmount() + item.getAmount());
				event.getCursor().setAmount(0);
				updatePage();
			}
			case PLACE_ONE -> {
				//when a new stack not yet in the clicked slot is added on an same item non empty slot either left click or only 1 more item till max stack
				var item = event.getCursor();
				if(item == null){
					return;
				}
				
				if(index >= entries.size()){
					onItemInsert.accept(item.clone(), entries.size());
					item.setAmount(item.getAmount() - 1);
					updatePage();
					return;
				}
				
				ItemStack itemStack = entries.get(index).getItemStack();
				if(itemStack == null){
					return;
				}
				itemStack.setAmount(itemStack.getAmount() + 1);
				event.getCursor().setAmount(event.getCursor().getAmount() - 1);
				updatePage();
			}
			
			case PLACE_SOME -> {
				//todo do the math on how many can be added to it
				//When items of the same type in the cursor and slot are being added (anything from 2 to 63 items) only 1 and all and non is treated different
				System.out.println("Attempt to add item");
				var item = event.getCursor();
				if(item == null){
					return;
				}
				
				int stackSize = item.getMaxStackSize();
				
				ItemStack itemStack = entries.get(index).getItemStack();
				if(itemStack == null){
					return;
				}
				
				int amount = Math.min(stackSize - itemStack.getAmount(), item.getAmount());
				
				itemStack.setAmount(itemStack.getAmount() + amount);
				item.setAmount(item.getAmount() - amount);
				updatePage();
			}
			
			case NOTHING -> {
				//item is full nothing happens
				var item = event.getCursor();
				if(item == null){
					return;
				}
				
				onItemInsert.accept(item.clone(), entries.size());
				event.getCursor().setAmount(0);
				updatePage();
			}
			
			case PICKUP_ALL -> {
				if(object instanceof Button button){
					button.onClick(event);
				}
			}
		}
		updatePage();
	}
	
	/**
	 * updates the page buttons based on the current page count with the default filler item
	 */
	public void updatePageButtons() {
		updatePageButtons(fillerItem);
	}
	
	/**
	 * updates the page buttons based on the current page count, set 2 buttons for previous and next to replace them if not needed
	 *
	 * @param previousButtonReplacer
	 * @param nextButtonReplacer
	 */
	public void updatePageButtons(Button previousButtonReplacer, Button nextButtonReplacer) {
		if(previousButton == null || nextButton == null){
			return;
		}
		
		if(getMaxPage() == 1){
			gui.addButton(previousButtonReplacer, previousButton.getSlot());
			gui.addButton(nextButtonReplacer, nextButton.getSlot());
			return;
		}
		
		if(getPage() > 1){
			gui.addButton(previousButton, previousButton.getSlot());
		} else {
			gui.addButton(previousButtonReplacer, previousButton.getSlot());
		}
		
		if(getPage() < getMaxPage()){
			gui.addButton(nextButton, nextButton.getSlot());
		} else {
			gui.addButton(nextButtonReplacer, nextButton.getSlot());
		}
		gui.update();
	}
	
	/**
	 * Sets the filler item
	 */
	public void setFillerItem(ItemStack item) {
		this.fillerItem = item;
	}
	
	/**
	 * Gets the filler item
	 */
	public ItemStack getFillerItem() {
		return fillerItem;
	}
	
	public int getButtonSize() {
		return getButtons().size();
	}
	
	public int getSlotSize() {
		return slots.size();
	}
	
	/**
	 * @return The slots used by this panel
	 */
	public Set<Integer> getSlots() {
		return slots;
	}
	
	public void setOnItemInsert(BiConsumer<ItemStack, Integer> onItemInsert) {
		this.onItemInsert = onItemInsert;
	}
	
	public BiConsumer<ItemStack, Integer> getOnItemInsert() {
		return onItemInsert;
	}
	
	public int getEntrySize() {
		return entries.size();
	}
	
	/**
	 * @param allowInsertion whether to allow items to be inserted into the panel
	 */
	public void setAllowInsertion(boolean allowInsertion) {
		this.allowInsertion = allowInsertion;
	}
	
	/**
	 * Represents an entry in the pagination panel
	 *
	 * @param object the object reference (Button or ItemStack)
	 * @param adder the adder that sets the object in the gui
	 */
	private record PaginationEntry(Object object, IntConsumer adder, boolean isOpen){
		/**
		 * Returns the objects itemstack (either directly or in the form of a buttons itemstack)
		 *
		 * @return
		 */
		public ItemStack getItemStack() {
			if(object instanceof ItemStack item){
				return item;
			}
			if(object instanceof Button button){
				return button.getItem();
			}
			return null;
		}
	}
	
	/**
	 * Represents the data of a click event
	 *
	 * @param event the click event
	 * @param gui the pagination gui this event is for
	 * @param object the clicked object reference (Button or ItemStack)
	 * @param index the index of the object in the entries list
	 */
	private record ClickData(InventoryClickEvent event, PaginationGui gui, Object object, int index){
		public boolean isButton() {
			return object instanceof Button;
		}
		
		public boolean isItem() {
			return object instanceof ItemStack;
		}
	}
	
	public void setClickAction(InventoryAction action, Consumer<ClickData> consumer) {
		clickActions.put(action, consumer);
	}
	
	private void registerActions() {
		clickActions.put(InventoryAction.SWAP_WITH_CURSOR, clickData -> {
			var event = clickData.event();
			System.out.println("Attempt to add item");
			var item = event.getCursor();
			if(item == null){
				return;
			}
			
			System.out.println("Adding item");
			onItemInsert.accept(item.clone(), entries.size());
			event.getCursor().setAmount(0);
		});
		
		clickActions.put(InventoryAction.PLACE_ALL, clickData -> {
			//when a new stack not yet in the clicked slot is added on an empty slot
			var event = clickData.event();
			var index = clickData.index();
			System.out.println("Attempt to add item");
			var item = event.getCursor();
			if(item == null){
				return;
			}
			
			if(index >= entries.size()){
				onItemInsert.accept(item.clone(), entries.size());
				item.setAmount(0);
				updatePage();
				return;
			}
			
			ItemStack itemStack = entries.get(index).getItemStack();
			if(itemStack == null){
				return;
			}
			
			itemStack.setAmount(itemStack.getAmount() + item.getAmount());
			event.getCursor().setAmount(0);
		});
		
		clickActions.put(InventoryAction.PLACE_ONE, clickData -> {
			//when a new stack not yet in the clicked slot is added on an same item non empty slot either left click or only 1 more item till max stack
			var event = clickData.event();
			var index = clickData.index();
			var item = event.getCursor();
			if(item == null){
				return;
			}
			
			if(index >= entries.size()){
				onItemInsert.accept(item.clone(), entries.size());
				item.setAmount(item.getAmount() - 1);
				updatePage();
				return;
			}
			
			ItemStack itemStack = entries.get(index).getItemStack();
			if(itemStack == null){
				return;
			}
			itemStack.setAmount(itemStack.getAmount() + 1);
			event.getCursor().setAmount(event.getCursor().getAmount() - 1);
		});
		
		clickActions.put(InventoryAction.PLACE_SOME, clickData -> {
			//todo do the math on how many can be added to it
			//When items of the same type in the cursor and slot are being added (anything from 2 to 63 items) only 1 and all and non is treated different
			var event = clickData.event();
			var index = clickData.index();
			System.out.println("Attempt to add item");
			var item = event.getCursor();
			if(item == null){
				return;
			}
			
			int stackSize = item.getMaxStackSize();
			
			ItemStack itemStack = entries.get(index).getItemStack();
			if(itemStack == null){
				return;
			}
			
			int amount = Math.min(stackSize - itemStack.getAmount(), item.getAmount());
			
			itemStack.setAmount(itemStack.getAmount() + amount);
			item.setAmount(item.getAmount() - amount);
			updatePage();
		});
		
		clickActions.put(InventoryAction.NOTHING, clickData -> {
			//item is full nothing happens
			var event = clickData.event();
			var index = clickData.index();
			//item is full nothing happens
			var item = event.getCursor();
			if(item == null){
				return;
			}
			
			onItemInsert.accept(item.clone(), entries.size());
			event.getCursor().setAmount(0);
			updatePage();
		});
		
		clickActions.put(InventoryAction.PICKUP_ALL, clickData -> {
			var object = clickData.object();
			if(object instanceof Button button){
				button.onClick(clickData.event());
			}
		});
		
	}
	
}
