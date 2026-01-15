package com.wonkglorg.utilitylib.inventory;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@SuppressWarnings("unused")
public class InventoryItems{
	
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
	
	public static final ItemStack NEXT_PAGE = createItem(Material.ARROW,Component.text("Next Page"));
	public static final ItemStack PREVIOUS_PAGE = createItem(Material.ARROW,Component.text("Previous Page"));
	
	private InventoryItems() {
		//Utility class
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
	 * Utility Method to create an item with a name
	 *
	 * @param material The material to create the ItemStack with
	 * @param name then name of the item
	 * @return The ItemStack
	 */
	public static ItemStack createItem(Material material, Component name) {
		ItemStack itemStack = new ItemStack(material);
		ItemMeta meta = itemStack.getItemMeta();
		meta.displayName(name);
		itemStack.setItemMeta(meta);
		return itemStack;
	}
}
