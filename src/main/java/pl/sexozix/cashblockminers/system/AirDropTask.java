package pl.sexozix.cashblockminers.system;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.sexozix.cashblockminers.CashBlockConfiguration;
import pl.sexozix.cashblockminers.CashBlockPlugin;
import pl.sexozix.cashblockminers.utils.ChatUtil;

public class AirDropTask extends BukkitRunnable
{
  private final CashBlockPlugin plugin;

  public AirDropTask(CashBlockPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public void run() {
    long airdropTime = plugin.getNextAirdropTime() - System.currentTimeMillis();
    long airdropDisplayAt = (CashBlockConfiguration.getConfiguration().airdrop.time * 200);
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (airdropTime <= 0) continue;
      if (airdropTime < airdropDisplayAt) {
        ChatUtil.sendActionBar(player, CashBlockConfiguration.getConfiguration().airdrop.actionbarMessage.replace("{TIME}", ChatUtil.getDurationBreakdown(airdropTime)));
      }
    }
  }
}
