package pl.sexozix.cashblockminers.system.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public interface ClickableInventory extends InventoryHolder {
    default void handleClick(InventoryClickEvent event) {

    }
}
