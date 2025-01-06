package com.wonkglorg.utilitylib.inventory.specialised;

import com.wonkglorg.utilitylib.inventory.GuiInventory;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.plugin.java.JavaPlugin;


@SuppressWarnings("unused")
public abstract class GuiFurnace extends GuiInventory {

    public GuiFurnace(Component name, JavaPlugin plugin, Player player) {
        super(Bukkit.createInventory(player, InventoryType.FURNACE, name), plugin, player);
    }

    public GuiFurnace(JavaPlugin plugin, Player player) {
        super(Bukkit.createInventory(null, InventoryType.FURNACE), plugin, player);
    }

    public GuiFurnace(FurnaceInventory inventory, JavaPlugin plugin, Player player) {
        super(inventory, plugin, player);
    }

    public FurnaceInventory getInventory() {
        return (FurnaceInventory) super.getInventory();
    }

}