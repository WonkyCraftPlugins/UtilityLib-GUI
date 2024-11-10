package com.wonkglorg.utilitylib.inventory.profile;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * A utility class to manage player profiles of any type.
 * @param <T>
 */
@SuppressWarnings("unused")
public final class ProfileManager<T extends MenuProfile> {
    private T defaultMenu;
    private final Map<Player, T> utilityMap = new HashMap<>();

    /**
     * Creates a new ProfileManagerm add the default menu that should be asigned when a player has no menu and gets the menu called for them
     *
     * @param defaultMenu
     */
    public ProfileManager(T defaultMenu) {
        this.defaultMenu = defaultMenu;
    }

    /**
     * Sets the default MenuProfile to asign to a player when non could be found for the player.
     * Inherit all values passed with the class besides the owner being reasigned to the new player.
     *
     * @param defaultMenu
     */
    public void setDefaultMenu(T defaultMenu) {
        this.defaultMenu = defaultMenu;
    }

    /**
     * Gets the MenuProfile for the player, if none is found a new one is created and returned.
     * @param player the player to get the profile for
     * @return the MenuProfile for the player
     */
    @SuppressWarnings("unchecked")
    public T get(Player player) {
        T profile = (T) defaultMenu.clone();
        profile.setOwner(player);
        utilityMap.keySet().removeIf(Predicate.not(Player::isValid));
        return utilityMap.computeIfAbsent(player, k -> profile);
    }

}