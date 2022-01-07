package pl.sexozix.cashblockminers.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import pl.sexozix.cashblockminers.system.inventory.ClickableInventory;

public class InventoryListener implements Listener {
    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && event.getClickedInventory().getHolder() instanceof ClickableInventory) {
            ((ClickableInventory) event.getClickedInventory().getHolder()).handleClick(event);
            event.setCancelled(true);
        }
    }
}
