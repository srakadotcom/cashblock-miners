package pl.sexozix.cashblockminers.system.bossbar;

import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import pl.sexozix.cashblockminers.CashBlockConfiguration;
import pl.sexozix.cashblockminers.utils.ChatUtil;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BossbarManager {
    private static final long BOSSBAR_SHOW_TIME = 5000L;

    private final Set<BossbarNotification> bossbarNotificationSet = new HashSet<>();

    public void createNotification(String playerName, double amountMined) {
        BossBar bossBar = Bukkit.createBossBar(
                ChatUtil.fixColor(CashBlockConfiguration.getConfiguration().bossbar.bossbarMoneyDisplayMessage
                        .replace("{PLAYER}", playerName)
                        .replace("{MONEY}", Double.toString(amountMined))),
                CashBlockConfiguration.getConfiguration().bossbar.bossbarColor,
                CashBlockConfiguration.getConfiguration().bossbar.bossbarStyle
        );

        for(Player player: Bukkit.getOnlinePlayers()) {
            bossBar.addPlayer(player);
        }

        bossbarNotificationSet.add(new BossbarNotification(
                System.currentTimeMillis() + BOSSBAR_SHOW_TIME,
                bossBar
        ));
    }

    public void doBossbarTick() {
        Iterator<BossbarNotification> each = bossbarNotificationSet.iterator();

        while (each.hasNext()) {
            BossbarNotification notification = each.next();
            if (System.currentTimeMillis() > notification.expire()) {
                each.remove();
                notification.bossbar().setVisible(false);
                notification.bossbar().removeAll();
            }
        }
    }

}
