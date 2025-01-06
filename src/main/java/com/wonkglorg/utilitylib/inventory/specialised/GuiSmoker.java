package com.wonkglorg.utilitylib.inventory.specialised;

import com.wonkglorg.utilitylib.inventory.GuiInventory;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.plugin.java.JavaPlugin;


@SuppressWarnings("unused")
public abstract class GuiSmoker extends GuiInventory {

    public GuiSmoker(Component name, JavaPlugin plugin, Player player) {
        super(Bukkit.createInventory(player, InventoryType.SMOKER, name), plugin, player);
    }

    public GuiSmoker(JavaPlugin plugin, Player player) {
        super(Bukkit.createInventory(null, InventoryType.SMOKER), plugin, player);
    }

    public GuiSmoker(FurnaceInventory inventory, JavaPlugin plugin, Player player) {
        super(inventory, plugin, player);
    }

    @Override
    public FurnaceInventory getInventory() {
        return (FurnaceInventory) super.getInventory();
    }
}
