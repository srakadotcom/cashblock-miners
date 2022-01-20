package pl.sexozix.cashblockminers.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import pl.sexozix.cashblockminers.CashBlockConfiguration;
import pl.sexozix.cashblockminers.system.data.UserHandler;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class AirdropListener implements Listener {
    private final Set<Location> airdropLocations;
    private final UserHandler handler;

    public AirdropListener(Set<Location> airdropLocations, UserHandler handler) {
        this.airdropLocations = airdropLocations;
        this.handler = handler;
    }

    @EventHandler
    private void onBlockMine(BlockBreakEvent event) {
        if(airdropLocations.remove(event.getBlock().getLocation())) {
            event.setCancelled(true);
            event.getBlock().setType(Material.AIR);
            double dupa = getDupa();
            handler.getCachedDataModel(event.getPlayer().getUniqueId()).addMoney(dupa);
            event.getPlayer().sendMessage(CashBlockConfiguration.getConfiguration().airdrop.getFormattedReceiveMessage(dupa));
        }
    }

    private double getDupa() {
        double random = ThreadLocalRandom.current().nextDouble(5);
        String dupa = String.format("%.1f", random);
        return Double.parseDouble(dupa);
    }
}
