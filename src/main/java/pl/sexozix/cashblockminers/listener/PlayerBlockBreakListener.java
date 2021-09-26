package pl.sexozix.cashblockminers.listener;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import pl.sexozix.cashblockminers.CashBlockConfiguration;
import pl.sexozix.cashblockminers.system.bossbar.BossbarManager;
import pl.sexozix.cashblockminers.system.data.UserDataModel;
import pl.sexozix.cashblockminers.system.data.UserHandler;
import pl.sexozix.cashblockminers.system.reward.Reward;
import pl.sexozix.cashblockminers.utils.ChatUtil;
import pl.sexozix.cashblockminers.utils.RandomUtil;

import java.util.concurrent.ThreadLocalRandom;

public final class PlayerBlockBreakListener implements Listener {
    private static final double FIRST_CHANCE = 10.0d;

    private final UserHandler handler;
    private final BossbarManager bossbarManager;

    public PlayerBlockBreakListener(UserHandler handler, BossbarManager bossbarManager) {
        this.handler = handler;
        this.bossbarManager = bossbarManager;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onPlayerBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (!CashBlockConfiguration.getConfiguration().blocks.contains(event.getBlock().getType()))
            return;

        UserDataModel userDataModel = handler.getUserDataModel(player);

        double quantity = 0.0d;

        double fakeReward = userDataModel.fakeReward();
        if (fakeReward > 0.0d) {
            quantity = fakeReward;
        } else if (userDataModel.money() < 0.0d) {
            if (RandomUtil.chance(FIRST_CHANCE))
                quantity = ThreadLocalRandom.current().nextDouble(1);
        } else {
            for (Reward reward : CashBlockConfiguration.getConfiguration().rewardList)
                if (RandomUtil.chance(reward.chance())) {
                    quantity = reward.quantity();
                    break;
                }
        }

        if (quantity > 0.0d) {
            quantity = Math.round(quantity * 100.0) / 100.0;

            String moneyFormat = String.format("%.2f", userDataModel.money());

            userDataModel.addMoney(quantity);
            player.sendTitle(ChatUtil.fixColor(CashBlockConfiguration.getConfiguration().messages.titleMessage
                            .replace("{MINED-MONEY}", String.valueOf(quantity))
                            .replace("{ACTUAL-MONEY}", moneyFormat)),
                    ChatUtil.fixColor(CashBlockConfiguration.getConfiguration().messages.subtitleMessage
                            .replace("{MINED-MONEY}", String.valueOf(quantity))
                            .replace("{ACTUAL-MONEY}", moneyFormat)), 10, 60, 10);
            Bukkit.broadcastMessage(ChatUtil.fixColor(CashBlockConfiguration.getConfiguration().messages.playerMinedBroadcast
                    .replace("{PLAYER}", player.getName())
                    .replace("{MINED-MONEY}", String.valueOf(quantity))));
            bossbarManager.createNotification(player.getName(), quantity);
        }
    }
}
