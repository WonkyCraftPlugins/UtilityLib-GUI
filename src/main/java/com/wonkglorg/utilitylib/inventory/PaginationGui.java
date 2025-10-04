package com.wonkglorg.utilitylib.inventory;

import static com.wonkglorg.utilitylib.inventory.GuiInventory.MAX_ROWS;
import com.wonkglorg.utilitylib.inventory.profile.MenuProfile;
import org.bukkit.entity.Item;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;
import java.util.stream.Collectors;

/**
 * A panel in an InventoryGUI which can be used to paginate items and buttons
 *
 * @author Redempt
 */
@SuppressWarnings("unused")
public final class PaginationGui{
	
	/**
	 * The InventoryGUI this panel is in
	 */
	private final GuiInventory<?> gui;
	/**
	 * The current page of the panel
	 */
	private int page = 1;
	
	/**
	 * A list that represents the entries in the panel the pagination is based on this lists ordering (if this ordering is modified so will the panel elements order) (to create an empty panel slot use null or leave the {@link PaginationEntry#object} null)
	 */
	private final ArrayList<PaginationEntry> entries = new ArrayList<>();
	
	private final Set<Integer> slots = new TreeSet<>();
	
	private Runnable onUpdate = () -> {
	};
	/**
	 * The item to use to fill the rest with the empty slots
	 * -- SETTER --
	 * Sets the filler item
	 * -- GETTER --
	 * Gets the filler item
	 */
	private ItemStack fillerItem;
	
	/**
	 * The previous button assigned to this panel (is not required only for convenience in #updatePageButtons)
	 */
	private Button previousButton;
	/**
	 * The Next button assigned to this panel (is not required only for convenience in #updatePageButtons)
	 */
	private Button nextButton;
	
	/**
	 * IMPLEMENT LATER  IF SET TO TRUE THE PANEL WILL ALLOW INSERTIONS INTO THE PANEL SLOTS (DEFAULT FALSE)
	 */
	private final boolean open = false; //NOSONAR
	
	/**
	 * A consumer that is called when an item is inserted into the panel
	 */
	private final ObjIntConsumer<ItemStack> onItemInsert = (item, index) -> entries.add(index, new PaginationEntry(item));
	
	/**
	 * Called whenever a click event happens for this pagination menu, this happens before any buttons fire their click events
	 */
	private Consumer<ClickData> onClick = event -> {
	};
	
	/**
	 * Constructs a PaginationPanel to work on a given InventoryGUI
	 *
	 * @param gui The InventoryGUI to paginate
	 */
	public PaginationGui(GuiInventory<MenuProfile> gui) {
		this(gui, null);
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
		gui.addPaginationGui(this);
	}
	
	/**
	 * Adds a paged button to the panel
	 *
	 * @param button The button to add
	 */
	public void addPagedButton(Button button) {
		entries.add(new PaginationEntry(button));
	}
	
	/**
	 * Adds a paged item to the panel
	 *
	 * @param item The item to add
	 */
	public void addPagedItem(ItemStack item) {
		entries.add(new PaginationEntry(item));
	}
	
	/**
	 * Adds multiple buttons to the paged panel
	 *
	 * @param buttons The buttons to add
	 */
	public void addPagedButtons(Iterable<Button> buttons) {
		for(Button button : buttons){
			addPagedButton(button);
		}
	}
	
	/**
	 * Adds multiple items to the paged panel
	 *
	 * @param items The items to add
	 */
	public void addPagedItems(Iterable<ItemStack> items) {
		for(ItemStack item : items){
			addPagedItem(item);
		}
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
		ensureCapacity(index);
		entries.set(index, new PaginationEntry(item));
	}
	
	/**
	 * Adds a button to the panel at a specific index
	 *
	 * @param index The index to add the button at
	 * @param button The button to add
	 */
	public void addAtPosition(int index, Button button) {
		ensureCapacity(index);
		entries.set(index, new PaginationEntry(button));
	}
	
	/**
	 * Ensures the entries list has a capacity of at least index
	 *
	 * @param index The index to ensure
	 */
	private void ensureCapacity(int index) {
		int start = entries.size();
		while(start <= index){
			entries.add(start++, null);
		}
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
				slots.add((y * MAX_ROWS) + x);
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
				slots.remove((y * MAX_ROWS) + x);
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
		for(int i = start; i < end; i++){ //NOSONAR
			PaginationEntry paginationEntry = entries.get(i);
			int slot = iter.next();
			if(paginationEntry == null){
				gui.addItem(fillerItem, slot);
				continue;
			}
			if(paginationEntry.object() == null){
				gui.addItem(null, slot);
				continue;
			}
			
			if(paginationEntry.isButton()){
				gui.addButton((Button) paginationEntry.object(), slot);
			} else {
				gui.addItem(paginationEntry.getItemStack(), slot);
			}
		}
		onUpdate.run();
		updatePageChangeButtons();
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
					  .collect(Collectors.toList()); //NOSONAR
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
					  .collect(Collectors.toList()); //NOSONAR
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
	
	/**
	 * Sets the previous button for page navigation used by {@link #updatePageChangeButtons()}
	 * @param button the button
	 * @param slot the slot of the button
	 */
	public void setPreviousPageButton(Button button, int slot) {
		button.setSlot(slot);
		this.previousButton = button;
	}
	
	/**
	 * Sets the previous button for page navigation used by {@link #updatePageChangeButtons()}
	 * @param item the previous page icon
	 * @param slot the slot of the button
	 */
	public void setPreviousPageButton(ItemStack item, int slot) {
		setPreviousPageButton(Button.create(item,e -> prevPage()), slot);
	}
	
	
	/**
	 * Sets the next button for page navigation used by {@link #updatePageChangeButtons()}
	 * @param button the button
	 * @param slot the slot of the button
	 */
	public void setNextPageButton(Button button, int slot) {
		button.setSlot(slot);
		this.nextButton = button;
	}
	
	
	/**
	 * Sets the next button for page navigation used by {@link #updatePageChangeButtons()}
	 * @param item the next page icon
	 * @param slot the slot of the button
	 */
	public void setNextPageButton(ItemStack item, int slot) {
		setNextPageButton(Button.create(item,e -> nextPage()), slot);
	}
	
	
	
	/**
	 * Sets the previous and next buttons for page navigation used by {@link #updatePageChangeButtons()}
	 * @param previousButton the previous button
	 * @param previousSlot the slot of the previous button
	 * @param nextButton the next button
	 * @param nextSlot the slot of the next button
	 */
	public void setPageSwapButtons(Button previousButton, int previousSlot, Button nextButton, int nextSlot) {
		setPreviousPageButton(previousButton, previousSlot);
		setNextPageButton(nextButton, nextSlot);
	}
	
	
	/**
	 * Sets the previous and next buttons for page navigation used by {@link #updatePageChangeButtons()}
	 * @param previousButton the previous page icon
	 * @param previousSlot the slot of the previous button
	 * @param nextButton the next page icon
	 * @param nextSlot the slot of the next button
	 */
	public void setPageSwapButtons(ItemStack previousButton, int previousSlot, ItemStack nextButton, int nextSlot) {
		setPreviousPageButton(previousButton, previousSlot);
		setNextPageButton(nextButton, nextSlot);
	}
	
	
	/**
	 * updates the page buttons based on the current page count with an itemstack
	 *
	 * @param fillerItem the item to use as a filler
	 */
	public void updatePageChangeButtons(ItemStack fillerItem) {
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
	 * FOR INTERNAL USE ONLY
	 * @param event the event
	 * @param object the object reference (Button or ItemStack)
	 * @param index the index of the object in the entries list
	 */
	public void onInventoryEvent(InventoryClickEvent event, Object object, int index) { //NOSONAR
		if(onClick != null){
			onClick.accept(new ClickData(event, this, object, index));
		}
		
		if(object instanceof Button button){
			button.onClick(event);
			event.setCancelled(true);
			return;
		}
		
		if(!open){
			event.setCancelled(true);
		}
		
		//updatePage();
	}
	
	/**
	 * updates the page buttons based on the current page count with the default filler item
	 */
	public void updatePageChangeButtons() {
		updatePageChangeButtons(fillerItem);
	}
	
	/**
	 * updates the page buttons based on the current page count, set 2 buttons for previous and next to replace them if not needed
	 *
	 * @param previousButtonReplacer the button to replace the previous button with if not needed
	 * @param nextButtonReplacer the button to replace the next button with if not needed
	 */
	public void updatePageChangeButtons(Button previousButtonReplacer, Button nextButtonReplacer) {
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
	 * Represents an entry in the pagination panel
	 *
	 * @param object the object reference (Button or ItemStack)
	 */
	private record PaginationEntry(Object object){
		
		/**
		 * @return the objects itemstack (either directly or in the form of a buttons itemstack)
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
		
		public boolean isButton() {
			return object instanceof Button;
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
	public record ClickData(InventoryClickEvent event, PaginationGui gui, Object object, int index){
		public boolean isButton() {
			return object instanceof Button;
		}
		
		public boolean isItem() {
			return object instanceof ItemStack;
		}
	}
	
	public int getPage() {
		return page;
	}
	
	public int getButtonSize() {
		return getButtons().size();
	}
	
	public int getSlotSize() {
		return slots.size();
	}
	
	public int getEntrySize() {
		return entries.size();
	}
	
	public void setOnUpdate(Runnable onUpdate) {this.onUpdate = onUpdate;}
	
	public Set<Integer> getSlots() {return slots;}
	
	public void setFillerItem(ItemStack fillerItem) {this.fillerItem = fillerItem;}
	
	/**
	 * @return The max number of elements displayed on each page
	 */
	public int getPageSize() {return slots.size();}
	
	
	public void setOnClick(Consumer<ClickData> onClick) {
		this.onClick = onClick;
	}
}
