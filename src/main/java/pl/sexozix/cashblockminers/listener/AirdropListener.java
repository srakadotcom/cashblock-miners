package pl.sexozix.cashblockminers.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import pl.sexozix.cashblockminers.CashBlockConfiguration;
import pl.sexozix.cashblockminers.system.data.UserHandler;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
            handler.getUserDataModel(event.getPlayer()).addMoney(dupa);
            event.getPlayer().sendMessage(CashBlockConfiguration.getConfiguration().airdrop.getFormattedReceiveMessage(dupa));
        }
    }

    private double getDupa() {
        return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(3))
                .setScale(1, RoundingMode.HALF_DOWN)
                .doubleValue();
    }
}
