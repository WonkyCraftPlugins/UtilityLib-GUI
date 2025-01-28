package com.wonkglorg.utilitylib.inventory.specialised;

import com.wonkglorg.utilitylib.inventory.GuiInventory;
import com.wonkglorg.utilitylib.inventory.profile.MenuProfile;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;


@SuppressWarnings("unused")
public abstract class GuiDispenser<T extends MenuProfile> extends GuiInventory<T> {

    protected GuiDispenser(Component name, JavaPlugin plugin, Player player) {
        super(Bukkit.createInventory(player, InventoryType.DISPENSER, name), plugin, player);
    }

    protected GuiDispenser(JavaPlugin plugin, Player player) {
        super(Bukkit.createInventory(null, InventoryType.DISPENSER), plugin, player);
    }

    protected GuiDispenser(Inventory inventory, JavaPlugin plugin, Player player) {
        super(inventory, plugin, player);
    }

}
