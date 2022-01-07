package pl.sexozix.cashblockminers.system.bossbar;

public interface BossBarManager {
    void createNotification(String playerName, double amountMined);

    void doTick();
}
