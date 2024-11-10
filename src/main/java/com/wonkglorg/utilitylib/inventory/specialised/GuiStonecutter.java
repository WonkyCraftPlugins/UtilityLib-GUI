package com.wonkglorg.utilitylib.inventory.specialised;

import com.wonkglorg.testplugin1211.inventory.GuiInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.java.JavaPlugin;


@SuppressWarnings("unused")
public abstract class GuiStonecutter extends GuiInventory {

    public GuiStonecutter(String name, JavaPlugin plugin, Player player) {
        super(Bukkit.createInventory(player, InventoryType.STONECUTTER, name), plugin, player);
    }

    public GuiStonecutter(JavaPlugin plugin, Player player) {
        super(Bukkit.createInventory(null, InventoryType.STONECUTTER), plugin, player);
    }


}
