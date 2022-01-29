package pl.sexozix.cashblockminers.system.bossbar.impl.protocollib;

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
    private final Bossy bossy;
    private final UserHandler userHandler;
    private final CashBlockPlugin plugin;

    private long notificationExpiration;
    private String bossbarText;

    public ProtocolLibBossbarManager(CashBlockPlugin plugin) {
        this.bossy = new Bossy(plugin);
        this.userHandler = plugin.getHandler();
        this.plugin = plugin;
    }

    public void createNotification(String playerName, double amountMined) {
        if (CashBlockConfiguration.getConfiguration().bossbar == null)
            return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if(!userHandler.getCachedDataModel(player.getUniqueId()).isBoostActive() || getBossbar() == null)
            bossy.setText(player, bossbarText = ChatUtil.fixColor(CashBlockConfiguration.getConfiguration().bossbar.bossbarMoneyDisplayMessage
                    .replace("{PLAYER}", playerName)
                    .replace("{MONEY}", Double.toString(amountMined))));
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
            if(dataModel == null)
                continue;

            long boostExpireTime = dataModel.boostExpire() - System.currentTimeMillis();
            if (dataModel.isBoostActive() && getBossbar() != null) {
                bossy.set(player, ChatUtil.fixColor(getBossbar().bossbarMoneyDisplayMessage.replace("{TIME}", ChatUtil.getDurationBreakdown(boostExpireTime))), dataModel.boostPercentage());
            } else if(airdropTime < airdropDisplayAt) {
                bossy.set(player, ChatUtil.fixColor(CashBlockConfiguration.getConfiguration().airdrop.airdropBossbar.bossbarMoneyDisplayMessage.replace("{TIME}", ChatUtil.getDurationBreakdown(airdropTime))), (float) airdropTime / airdropDisplayAt);
            } else {
                if (time <= 0)
                    bossy.hide(player);
                else
                    bossy.set(player, bossbarText, (time / (float) BOSSBAR_SHOW_TIME));
            }
        }
    }

    private CashBlockConfiguration.Bossbar getBossbar() {
        return CashBlockConfiguration.getConfiguration().turbocashBossbar;
    }
}
