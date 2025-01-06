package com.wonkglorg.utilitylib.inventory.specialised;

import com.wonkglorg.utilitylib.inventory.GuiInventory;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;


@SuppressWarnings("unused")
public abstract class GuiDropper extends GuiInventory {

    public GuiDropper(Component name, JavaPlugin plugin, Player player) {
        super(Bukkit.createInventory(player, InventoryType.DROPPER, name), plugin, player);
    }

    public GuiDropper(JavaPlugin plugin, Player player) {
        super(Bukkit.createInventory(null, InventoryType.DROPPER), plugin, player);
    }

    public GuiDropper(Inventory inventory, JavaPlugin plugin, Player player) {
        super(inventory, plugin, player);
    }

}
