package com.wonkglorg.utilitylib.inventory.specialised;

import com.wonkglorg.testplugin1211.inventory.GuiInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;


@SuppressWarnings("unused")
public abstract class GuiDispenser extends GuiInventory {

    public GuiDispenser(String name, JavaPlugin plugin, Player player) {
        super(Bukkit.createInventory(player, InventoryType.DISPENSER, name), plugin, player);
    }

    public GuiDispenser(JavaPlugin plugin, Player player) {
        super(Bukkit.createInventory(null, InventoryType.DISPENSER), plugin, player);
    }

    public GuiDispenser(Inventory inventory, JavaPlugin plugin, Player player) {
        super(inventory, plugin, player);
    }

}
