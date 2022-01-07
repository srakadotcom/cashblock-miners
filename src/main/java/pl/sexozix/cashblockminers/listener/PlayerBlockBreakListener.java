package pl.sexozix.cashblockminers.listener;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import pl.sexozix.cashblockminers.CashBlockConfiguration;
import pl.sexozix.cashblockminers.system.blockreward.BlockRewardManager;
import pl.sexozix.cashblockminers.system.bossbar.BossBarManager;
import pl.sexozix.cashblockminers.system.bossbar.BossbarManagerImpl;
import pl.sexozix.cashblockminers.system.chance.*;
import pl.sexozix.cashblockminers.system.data.UserDataModel;
import pl.sexozix.cashblockminers.system.data.UserHandler;
import pl.sexozix.cashblockminers.utils.ChatUtil;

import java.util.Arrays;
import java.util.List;

public final class PlayerBlockBreakListener implements Listener {
    private final UserHandler handler;
    private final BossBarManager bossbarManagerImpl;

    private final List<ChanceTransformer> transformerList;

    public PlayerBlockBreakListener(UserHandler handler, BossBarManager bossbarManagerImpl, BlockRewardManager blockRewardManager) {
        this.handler = handler;
        this.bossbarManagerImpl = bossbarManagerImpl;
        this.transformerList = Arrays.asList(
                new FakeRewardTrasformer(),
                new BlockRewardTransformer(blockRewardManager),
                new ConfigurationTransformer(),
                new HelmetTransformer());
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

        double quantity = ChanceTransformer.apply(player, userDataModel, event.getBlock(), transformerList);
        if (quantity > 0.0d) {
            quantity = Math.round(quantity * 100.0) / 100.0;

            String moneyFormat = String.format("%.2f", userDataModel.money());

            userDataModel.addMoney(quantity);
            try {
                player.sendTitle(ChatUtil.fixColor(CashBlockConfiguration.getConfiguration().messages.titleMessage
                                .replace("{MINED-MONEY}", String.valueOf(quantity))
                                .replace("{ACTUAL-MONEY}", moneyFormat)),
                        ChatUtil.fixColor(CashBlockConfiguration.getConfiguration().messages.subtitleMessage
                                .replace("{MINED-MONEY}", String.valueOf(quantity))
                                .replace("{ACTUAL-MONEY}", moneyFormat)), 10, 60, 10);
            } catch (NoSuchMethodError ex) {
                player.sendTitle(ChatUtil.fixColor(CashBlockConfiguration.getConfiguration().messages.titleMessage
                                .replace("{MINED-MONEY}", String.valueOf(quantity))
                                .replace("{ACTUAL-MONEY}", moneyFormat)),
                        ChatUtil.fixColor(CashBlockConfiguration.getConfiguration().messages.subtitleMessage
                                .replace("{MINED-MONEY}", String.valueOf(quantity))
                                .replace("{ACTUAL-MONEY}", moneyFormat)));
            }
            Bukkit.broadcastMessage(ChatUtil.fixColor(CashBlockConfiguration.getConfiguration().messages.playerMinedBroadcast
                    .replace("{PLAYER}", player.getName())
                    .replace("{MINED-MONEY}", String.valueOf(quantity))));
            bossbarManagerImpl.createNotification(player.getName(), quantity);
        }
    }
}
