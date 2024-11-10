package com.wonkglorg.utilitylib.inventory.specialised;

import com.wonkglorg.testplugin1211.inventory.Button;
import com.wonkglorg.testplugin1211.inventory.GuiInventory;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class GuiCraftingtable extends GuiInventory {
    private boolean allowCrafting = false;

    public GuiCraftingtable(String name, JavaPlugin plugin, Player player) {
        super(Bukkit.createInventory(player, InventoryType.WORKBENCH, name), plugin, player);
    }

    public GuiCraftingtable(JavaPlugin plugin, Player player) {
        super(Bukkit.createInventory(null, InventoryType.WORKBENCH), plugin, player);
    }

    public GuiCraftingtable(Inventory inventory, JavaPlugin plugin, Player player) {
        super(inventory, plugin, player);
    }


    /**
     * Gets the output slot of the inventory
     *
     * @return the output slot
     */
    public int getOutputSlot() {
        return 0;
    }

    /**
     * Gets the crafting table matrix slots equivalent to
     *
     * @return the matrix slots
     */
    public ItemStack[] getMatrix() {
        ItemStack[] matrix = new ItemStack[9];
        for (int i = 1; i < 10; i++) {
            matrix[i - 1] = getInventory().getItem(i);
        }
        return matrix;
    }

    /**
     * Gets the recipe of the current matrix
     *
     * @param world the world the crafting happens in
     * @return the recipe
     */
    public Recipe getRecipe(World world) {
        return Bukkit.getCraftingRecipe(getMatrix(), world);
    }

    /**
     * Gets the recipe of the current matrix
     *
     * @return the recipe
     */
    public Recipe getRecipe() {
        return Bukkit.getCraftingRecipe(getMatrix(), profile.getOwner().getWorld());
    }

    /**
     * Gets the resulting item of the current matrix
     *
     * @return the result
     */
    public ItemStack getResult() {
        if (getMatrix() == null) return null;


        Recipe recipe = getRecipe();

        if (recipe == null) return null;
        return recipe.getResult();
    }

    /**
     * Gets the matrix slots of the inventory
     *
     * @param item the item to add
     */
    public void addOutputItem(ItemStack item) {
        getInventory().setItem(getOutputSlot(), item);
    }

    /**
     * Adds a button to the output slot of the inventory
     *
     * @param button the button to add
     */
    public void addOutputButton(Button button) {
        addButton(button, getOutputSlot());
    }

    /**
     * Allow or disallow crafting in the inventory. (by default false)
     *
     * @param allow true to allow crafting, false to disallow
     */
/*
    public void allowCrafting(boolean allow) {
        allowCrafting = allow;
    }

 */

    //todo this does not work normal gui does not allow for crafting

    /**
     * ONLY FOR INTERNAL USE!
     */
    /*
    @EventHandler
    public void onInventoryClickCraftEvent(InventoryClickEvent e) {
        if (!e.getInventory().equals(getInventory())) return;
        if (!allowCrafting) return;

        ItemStack[] matrix = new ItemStack[9];
        for (int i = 1; i < 10; i++) {
            matrix[i - 1] = getInventory().getItem(i);
        }

        Recipe recipe = Bukkit.getCraftingRecipe(matrix, e.getWhoClicked().getWorld());

        new PrepareItemCraftEvent()

        CraftingInventory craftingInventory = Bukkit.createInventory(null, InventoryType.WORKBENCH);


        var recipe = getInventory().getRecipe();
        if (recipe == null) {
            return;
        }

        getInventory().setResult(recipe.getResult());
    }
 */
    /**
     * DON'T USE THIS UNLESS YOU KNOW WHAT YOU'RE DOING
     * <br>
     * This method is called when the {@link CraftItemEvent} is called for any inventory use {@link #onCraft(CraftItemEvent)} instead.
     */
    /*
    @EventHandler
    public void onCraftEvent(CraftItemEvent event) {
        if (event.getInventory().equals(getInventory())) {
            onCraft(event);
        }
    } */

    /**
     * DON'T USE THIS UNLESS YOU KNOW WHAT YOU'RE DOING
     * <br>
     * This method is called when the {@link PrepareItemCraftEvent} is called for any inventory use {@link #onPrepareCraft(PrepareItemCraftEvent)} instead.
     */
    /*
    @EventHandler
    public void onCraftEvent(PrepareItemCraftEvent event) {
        if (event.getInventory().equals(getInventory())) {
            onPrepareCraft(event);
        }
    }
 */
    /**
     * Called when the {@link CraftItemEvent} is called for this inventory.
     *
     * @param event the event
     */
    /*
    public abstract void onCraft(CraftItemEvent event);
 */
    /**
     * Called when the {@link PrepareItemCraftEvent} is called for this inventory.
     *
     * @param event the event
     */
    /*
    public abstract void onPrepareCraft(PrepareItemCraftEvent event);
 */
}
