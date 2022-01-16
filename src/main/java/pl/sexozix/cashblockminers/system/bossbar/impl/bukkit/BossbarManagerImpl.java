package pl.sexozix.cashblockminers.system.bossbar.impl.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import pl.sexozix.cashblockminers.CashBlockConfiguration;
import pl.sexozix.cashblockminers.system.bossbar.BossBarManager;
import pl.sexozix.cashblockminers.utils.ChatUtil;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BossbarManagerImpl implements BossBarManager {
    private static final long BOSSBAR_SHOW_TIME = 5000L;

    private final Set<BukkitBossbarNotification> bossbarNotificationSet = new HashSet<>();

    public void createNotification(String playerName, double amountMined) {
        if(CashBlockConfiguration.getConfiguration().bossbar == null)
            return;

        BossBar bossBar = Bukkit.createBossBar(
                ChatUtil.fixColor(CashBlockConfiguration.getConfiguration().bossbar.bossbarMoneyDisplayMessage
                        .replace("{PLAYER}", playerName)
                        .replace("{MONEY}", Double.toString(amountMined))),
                BarColor.valueOf(CashBlockConfiguration.getConfiguration().bossbar.bossbarColor),
                BarStyle.valueOf(CashBlockConfiguration.getConfiguration().bossbar.bossbarStyle)
        );

        for(Player player: Bukkit.getOnlinePlayers()) {
            bossBar.addPlayer(player);
        }

        bossbarNotificationSet.add(new BukkitBossbarNotification(
                System.currentTimeMillis() + BOSSBAR_SHOW_TIME,
                bossBar
        ));
    }

    @Override
    public void doTick() {
        Iterator<BukkitBossbarNotification> each = bossbarNotificationSet.iterator();

        while (each.hasNext()) {
            BukkitBossbarNotification notification = each.next();
            if (System.currentTimeMillis() > notification.expire()) {
                each.remove();
                notification.bossbar().setVisible(false);
                notification.bossbar().removeAll();
            }
        }
    }
}
