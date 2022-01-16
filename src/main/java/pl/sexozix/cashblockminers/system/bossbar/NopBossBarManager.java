package pl.sexozix.cashblockminers.system.bossbar;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBar.Color;
import net.kyori.adventure.bossbar.BossBar.Overlay;
import net.kyori.adventure.text.Component;
import pl.sexozix.cashblockminers.CashBlockPlugin;
import pl.sexozix.cashblockminers.utils.ChatUtil;

public class NopBossBarManager implements BossBarManager{
    private final Set<NopBossbarNotification> bossbarNotificationSet = new HashSet<>();
    private Audience audience;
    
    @Override
    public void createNotification(String playerName, double amountMined) {
        BossBar bossBar = BossBar.bossBar(Component.text(ChatUtil.fixColor("&8* &fWykopałeś &8(&6&n" + amountMined + "zł&8) &8*")), 1F, Color.PURPLE, Overlay.PROGRESS);
        Player player = Bukkit.getPlayerExact(playerName);
        audience = CashBlockPlugin.getInstance().adventure().player(player);
        audience.showBossBar(bossBar);
        bossbarNotificationSet.add(new NopBossbarNotification(
            System.currentTimeMillis() + 5000L,
            bossBar));
    }

    @Override
    public void doTick() {
        Iterator<NopBossbarNotification> each = bossbarNotificationSet.iterator();

        while (each.hasNext()) {
            NopBossbarNotification notification = each.next();
            if (System.currentTimeMillis() > notification.expire()) {
                each.remove();
                audience.hideBossBar(notification.bossbar());
            }
        }
    }
}
