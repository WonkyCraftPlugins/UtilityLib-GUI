package com.wonkglorg.utilitylib.inventory.specialised;

import com.wonkglorg.testplugin1211.inventory.Button;
import com.wonkglorg.testplugin1211.inventory.GuiInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;


@SuppressWarnings("unused")
public abstract class GuiSmithing extends GuiInventory {

    public GuiSmithing(String name, JavaPlugin plugin, Player player) {
        super(Bukkit.createInventory(player, InventoryType.SMITHING, name), plugin, player);
    }

    public GuiSmithing(JavaPlugin plugin, Player player) {
        super(Bukkit.createInventory(null, InventoryType.SMITHING), plugin, player);
    }


    /**
     * @return the template slot of the inventory
     */
    public int getTemplateSlot() {
        return 0;
    }

    /**
     * @return the base slot of the inventory
     */
    public int getBaseSlot() {
        return 1;
    }

    /**
     * @return the material slot of the inventory
     */
    public int getMaterialSlot() {
        return 2;
    }

    /**
     * @return the result slot of the inventory
     */
    public int getResultSlot() {
        return 3;
    }

    /**
     * Adds a button to the template slot
     *
     * @param button the button to add
     */
    public void addTemplateButton(Button button) {
        addButton(button, getTemplateSlot());
    }

    /**
     * Adds an item to the template slot
     *
     * @param item the item to add
     */
    public void addTemplateItem(ItemStack item) {
        addItem(item, getTemplateSlot());
    }

    /**
     * Adds a button to the base slot
     *
     * @param button the button to add
     */
    public void addBaseButton(Button button) {
        addButton(button, getBaseSlot());
    }

    /**
     * Adds an item to the base slot
     *
     * @param item the item to add
     */
    public void addBaseItem(ItemStack item) {
        addItem(item, getBaseSlot());
    }

    /**
     * Adds a button to the material slot
     *
     * @param button the button to add
     */
    public void addMaterialButton(Button button) {
        addButton(button, getMaterialSlot());
    }

    /**
     * Adds an item to the material slot
     *
     * @param item the item to add
     */
    public void addMaterialItem(ItemStack item) {
        addItem(item, getMaterialSlot());
    }

    /**
     * Adds a button to the result slot
     *
     * @param button the button to add
     */
    public void addResultButton(Button button) {
        addButton(button, getResultSlot());
    }

    /**
     * Adds an item to the result slot
     *
     * @param item the item to add
     */
    public void addResultItem(ItemStack item) {
        addItem(item, getResultSlot());
    }

    /**
     * Adds an item to the result slot
     * @param item the item to add
     */
    public void addOutputItem(ItemStack item) {
        addItem(item, getResultSlot());
    }

    /**
     * Adds a button to the result slot
     * @param button the button to add
     */
    public void addOutputButton(Button button) {
        addButton(button, getResultSlot());
    }

}

