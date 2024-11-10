package com.wonkglorg.utilitylib.inventory.specialised;

import com.wonkglorg.testplugin1211.inventory.GuiInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.plugin.java.JavaPlugin;


@SuppressWarnings("unused")
public abstract class GuiBlastFurnace extends GuiInventory {

    public GuiBlastFurnace(String name, JavaPlugin plugin, Player player) {
        super(Bukkit.createInventory(player, InventoryType.BLAST_FURNACE, name), plugin, player);
    }

    public GuiBlastFurnace(JavaPlugin plugin, Player player) {
        super(Bukkit.createInventory(null, InventoryType.BLAST_FURNACE), plugin, player);
    }

    public GuiBlastFurnace(FurnaceInventory inventory, JavaPlugin plugin, Player player) {
        super(inventory, plugin, player);
    }

    @Override
    public FurnaceInventory getInventory() {
        return (FurnaceInventory) super.getInventory();
    }
}
