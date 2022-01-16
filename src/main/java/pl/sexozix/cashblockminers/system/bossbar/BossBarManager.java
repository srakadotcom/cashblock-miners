package pl.sexozix.cashblockminers.system.bossbar;

import pl.sexozix.cashblockminers.CashBlockPlugin;
import pl.sexozix.cashblockminers.system.bossbar.impl.NopBossbarManager;
import pl.sexozix.cashblockminers.system.bossbar.impl.bukkit.BossbarManagerImpl;
import pl.sexozix.cashblockminers.system.bossbar.impl.protocollib.ProtocolLibBossbarManager;

public interface BossBarManager {

    static BossBarManager findBossbarManager(CashBlockPlugin plugin) {
        try {
            Class.forName("org/bukkit/boss/BarColor");
            return new BossbarManagerImpl();
        } catch (ClassNotFoundException ex) {
            try {
                Class.forName("com.comphenix.protocol.ProtocolLib");
                return new ProtocolLibBossbarManager(plugin);
            } catch (ClassNotFoundException sex) {
                return new NopBossbarManager();
            }
        }

    }

    void createNotification(String playerName, double amountMined);

    void doTick();
}
