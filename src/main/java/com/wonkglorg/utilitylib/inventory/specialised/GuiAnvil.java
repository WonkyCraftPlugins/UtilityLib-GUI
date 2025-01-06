package com.wonkglorg.utilitylib.inventory.specialised;

import com.wonkglorg.utilitylib.inventory.Button;
import com.wonkglorg.utilitylib.inventory.GuiInventory;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;


@SuppressWarnings("unused")
public abstract class GuiAnvil extends GuiInventory implements Listener {


    public GuiAnvil(Component name, JavaPlugin plugin, Player player) {
        super(Bukkit.createInventory(player, InventoryType.ANVIL, name), plugin, player);
    }

    public GuiAnvil(JavaPlugin plugin, Player player) {
        super(Bukkit.createInventory(null, InventoryType.ANVIL), plugin, player);
    }

    /**
     * Adds an item to the output slot of the anvil
     * @param item the item to add
     */
    public void addOutput(ItemStack item) {
        getInventory().setItem(getOutputSlot(), item);
    }

    /**
     * Adds a button to the output slot of the anvil
     * @param button the button to add
     */
    public void addOutput(Button button) {
        addButton(button, getOutputSlot());
    }

    /**
     * Adds an item to the primary slot of the anvil
     * @param item the item to add
     */
    public void addPrimary(ItemStack item) {
        addItem(item, getPrimarySlot());
    }

    /**
     * Adds a button to the primary slot of the anvil
     * @param button the button to add
     */
    public void addPrimary(Button button) {
        addButton(button, getPrimarySlot());
    }

    /**
     * Adds an item to the secondary slot of the anvil
     * @param item the item to add
     */
    public void addSecondary(ItemStack item) {
        addItem(item, getSecondarySlot());
    }

    /**
     * Adds a button to the secondary slot of the anvil
     * @param button the button to add
     */
    public void addSecondary(Button button) {
        addButton(button, getSecondarySlot());
    }


    /**
     * @return the primary slot of the anvil (first slot from the left)
     */
    public int getPrimarySlot() {
        return 0;
    }

    /**
     * @return the secondary slot of the anvil (second slot from the left)
     */
    public int getSecondarySlot() {
        return 1;
    }

    /**
     * @return the output slot of the anvil
     */
    public int getOutputSlot() {
        return 2;
    }

    //todo figure out how to implement this

    /*
    @EventHandler
    public void onAnvil(PrepareAnvilEvent e) {
        if (e.getInventory().equals(getInventory())) {
            onAnvilEvent(e);
        }
    }


    //todo check which raw slot and only take final output slot?
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getInventory().equals(getInventory())) {
            onOutputClick(e);
        }
    }

    public abstract void onOutputClick(InventoryClickEvent e);

    public abstract void onAnvilEvent(PrepareAnvilEvent e);

     */
}

