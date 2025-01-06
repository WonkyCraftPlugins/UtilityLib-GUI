package com.wonkglorg.utilitylib.inventory.specialised;


import com.wonkglorg.utilitylib.inventory.Button;
import com.wonkglorg.utilitylib.inventory.GuiInventory;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;


@SuppressWarnings("unused")
public abstract class GuiCrafter extends GuiInventory {

    public GuiCrafter(Component name, JavaPlugin plugin, Player player) {
        super(Bukkit.createInventory(player, InventoryType.CRAFTER, name), plugin, player);
    }

    public GuiCrafter(JavaPlugin plugin, Player player) {
        super(Bukkit.createInventory(null, InventoryType.CRAFTER), plugin, player);
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
     * Adds an item to the primary slot of the inventory
     *
     * @param matrix the items to add (array of 9)
     */
    public void addMatrixItem(ItemStack[] matrix) {
        for (int i = 1; i < 10; i++) {
            getInventory().setItem(i, matrix[i - 1]);
        }
    }

    /**
     * Adds a button to the matrix slots of the inventory
     *
     * @param matrix the matrix to add (array of 9)
     */
    public void addMatrixButton(Button[] matrix) {
        for (int i = 1; i < 10; i++) {
            addButton(matrix[i - 1], i);
        }
    }


}
