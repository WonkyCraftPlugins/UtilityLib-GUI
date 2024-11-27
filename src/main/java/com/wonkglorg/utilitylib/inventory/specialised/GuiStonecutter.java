package com.wonkglorg.utilitylib.inventory.specialised;

import com.wonkglorg.utilitylib.inventory.Button;
import com.wonkglorg.utilitylib.inventory.GuiInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;


@SuppressWarnings("unused")
public abstract class GuiStonecutter extends GuiInventory {

    public GuiStonecutter(String name, JavaPlugin plugin, Player player) {
        super(Bukkit.createInventory(player, InventoryType.STONECUTTER, name), plugin, player);
    }

    public GuiStonecutter(JavaPlugin plugin, Player player) {
        super(Bukkit.createInventory(null, InventoryType.STONECUTTER), plugin, player);
    }


    public int getOutputSlot() {
        return 1;
    }

    public int getInputSlot() {
        return 0;
    }


    /**
     * Adds a button to the output slot
     *
     * @param button the button to add
     */
    public void addOutputButton(Button button) {
        addButton(getOutputSlot(), button);
    }

    /**
     * Adds an item to the output slot
     *
     * @param itemStack the item to add
     */
    public void addOutputItem(ItemStack itemStack) {
        addItem(getOutputSlot(), itemStack);
    }

    /**
     * Adds a button to the input slot
     *
     * @param button the button to add
     */
    public void addInputButton(Button button) {
        addButton(getInputSlot(), button);
    }

    /**
     * Adds an item to the input slot
     *
     * @param itemStack the item to add
     */
    public void addInputItem(ItemStack itemStack) {
        addItem(getInputSlot(), itemStack);
    }

}
