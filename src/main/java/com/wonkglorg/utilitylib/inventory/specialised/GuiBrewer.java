package com.wonkglorg.utilitylib.inventory.specialised;

import com.wonkglorg.testplugin1211.inventory.GuiInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.plugin.java.JavaPlugin;


@SuppressWarnings("unused")
public abstract class GuiBrewer extends GuiInventory implements Listener {

    public GuiBrewer(String name, JavaPlugin plugin, Player player) {
        super(Bukkit.createInventory(player, InventoryType.BREWING, name), plugin, player);
    }

    public GuiBrewer(JavaPlugin plugin, Player player) {
        super(Bukkit.createInventory(null, InventoryType.BREWING), plugin, player);
    }

    public GuiBrewer(BrewerInventory inventory, JavaPlugin plugin, Player player) {
        super(inventory, plugin, player);
    }

    @Override
    public BrewerInventory getInventory() {
        return (BrewerInventory) super.getInventory();
    }
}
