package com.wonkglorg.utilitylib.inventory.specialised;

import com.wonkglorg.utilitylib.inventory.GuiInventory;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class GuiHopper extends GuiInventory {

    public GuiHopper(Component name, JavaPlugin plugin, Player player) {
        super(Bukkit.createInventory(player, InventoryType.HOPPER, name), plugin, player);
    }

    public GuiHopper(JavaPlugin plugin, Player player) {
        super(Bukkit.createInventory(null, InventoryType.HOPPER), plugin, player);
    }

    public GuiHopper(Inventory inventory, JavaPlugin plugin, Player player) {
        super(inventory, plugin, player);
    }

}