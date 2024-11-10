package com.wonkglorg.utilitylib.inventory.specialised;

import com.wonkglorg.testplugin1211.inventory.GuiInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;


@SuppressWarnings("unused")
public abstract class GuiDropper extends GuiInventory {

    public GuiDropper(String name, JavaPlugin plugin, Player player) {
        super(Bukkit.createInventory(player, InventoryType.DROPPER, name), plugin, player);
    }

    public GuiDropper(JavaPlugin plugin, Player player) {
        super(Bukkit.createInventory(null, InventoryType.DROPPER), plugin, player);
    }

    public GuiDropper(Inventory inventory, JavaPlugin plugin, Player player) {
        super(inventory, plugin, player);
    }

}
