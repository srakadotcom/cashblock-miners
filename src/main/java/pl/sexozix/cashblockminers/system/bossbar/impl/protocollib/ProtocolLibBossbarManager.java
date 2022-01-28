package pl.sexozix.cashblockminers.system.bossbar.impl.protocollib;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBar.Color;
import net.kyori.adventure.bossbar.BossBar.Overlay;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.sexozix.cashblockminers.CashBlockConfiguration;
import pl.sexozix.cashblockminers.CashBlockPlugin;
import pl.sexozix.cashblockminers.system.bossbar.BossBarManager;
import pl.sexozix.cashblockminers.system.data.UserDataModel;
import pl.sexozix.cashblockminers.system.data.UserHandler;
import pl.sexozix.cashblockminers.utils.ChatUtil;

public class ProtocolLibBossbarManager implements BossBarManager {
    private static final long BOSSBAR_SHOW_TIME = 5000L;
    private final UserHandler userHandler;
    private final CashBlockPlugin plugin;
    private long notificationExpiration;
    private String bossbarText;
    private final BossBar bossBar = BossBar.bossBar(Component.text("dupa"), 0, Color.PURPLE, Overlay.PROGRESS);

    public ProtocolLibBossbarManager(CashBlockPlugin plugin) {
        this.userHandler = plugin.getHandler();
        this.plugin = plugin;
    }

    public void createNotification(String playerName, double amountMined) {
        if (CashBlockConfiguration.getConfiguration().bossbar == null)
            return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if(!userHandler.getCachedDataModel(player.getUniqueId()).isBoostActive() || getBossbar() == null)
            bossbarText = ChatUtil.fixColor(CashBlockConfiguration.getConfiguration().bossbar.bossbarMoneyDisplayMessage
                .replace("{PLAYER}", playerName)
                .replace("{MONEY}", Double.toString(amountMined)));
            bossBar.name(Component.text(bossbarText));
        }

        notificationExpiration = System.currentTimeMillis() + BOSSBAR_SHOW_TIME;
    }

    @Override
    public void doTick() {
        long time = notificationExpiration - System.currentTimeMillis();
        long airdropTime = plugin.getNextAirdropTime() - System.currentTimeMillis();
        long airdropDisplayAt = (CashBlockConfiguration.getConfiguration().airdrop.time * 200);

        for (Player player : Bukkit.getOnlinePlayers()) {
            UserDataModel dataModel = userHandler.getCachedDataModel(player.getUniqueId());
            Audience audience = plugin.adventure().player(player);
            if(dataModel == null)
                continue;

            long boostExpireTime = dataModel.boostExpire() - System.currentTimeMillis();
            if (dataModel.isBoostActive() && getBossbar() != null) {
                bossBar.name(Component.text(ChatUtil.fixColor(getBossbar().bossbarMoneyDisplayMessage.replace("{TIME}", ChatUtil.getDurationBreakdown(boostExpireTime)))));
                bossBar.progress(1);
                audience.showBossBar(bossBar);
            } else if(airdropTime < airdropDisplayAt) {
                bossBar.name(Component.text(ChatUtil.fixColor(CashBlockConfiguration.getConfiguration().airdrop.airdropBossbar.bossbarMoneyDisplayMessage.replace("{TIME}", ChatUtil.getDurationBreakdown(airdropTime)))));
                bossBar.progress(1);
                audience.showBossBar(bossBar);
            } else {
                if (time <= 0)
                    audience.hideBossBar(bossBar);
                else {
                    bossBar.name(Component.text(bossbarText));
                    bossBar.progress(1);
                }
            }
        }
    }

    private CashBlockConfiguration.Bossbar getBossbar() {
        return CashBlockConfiguration.getConfiguration().turbocashBossbar;
    }
}
