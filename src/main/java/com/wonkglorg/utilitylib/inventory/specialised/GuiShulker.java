package com.wonkglorg.utilitylib.inventory.specialised;

import com.wonkglorg.utilitylib.inventory.GuiInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;


@SuppressWarnings("unused")
public abstract class GuiShulker extends GuiInventory implements Listener {


    public GuiShulker(String name, JavaPlugin plugin, Player player) {
        super(Bukkit.createInventory(player, InventoryType.SHULKER_BOX, name), plugin, player);
    }

    public GuiShulker(JavaPlugin plugin, Player player) {
        super(Bukkit.createInventory(null, InventoryType.SHULKER_BOX), plugin, player);
    }

    public GuiShulker(Inventory inventory, JavaPlugin plugin, Player player) {
        super(inventory, plugin, player);
    }
}

