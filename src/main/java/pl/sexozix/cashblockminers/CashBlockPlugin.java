package pl.sexozix.cashblockminers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import pl.memexurer.srakadb.sql.DatabaseManager;
import pl.memexurer.srakadb.sql.DatabaseTransactionError;
import pl.sexozix.cashblockminers.commands.*;
import pl.sexozix.cashblockminers.listener.InventoryListener;
import pl.sexozix.cashblockminers.listener.PlayerBlockBreakListener;
import pl.sexozix.cashblockminers.system.blockreward.BlockRewardManager;
import pl.sexozix.cashblockminers.system.bossbar.BossBarManager;
import pl.sexozix.cashblockminers.system.bossbar.BossbarManagerImpl;
import pl.sexozix.cashblockminers.system.bossbar.NopBossBarManager;
import pl.sexozix.cashblockminers.system.data.UserHandler;
import pl.sexozix.cashblockminers.system.data.UserRepository;
import pl.sexozix.cashblockminers.system.reward.RewardSerializer;

import java.io.File;
import java.sql.SQLException;

public final class CashBlockPlugin extends JavaPlugin {
    private static final long SAVE_INTERVAL = 20L * 60L * 4L;

    private HikariDataSource dataSource;
    private boolean isDataSourceStolen = false;
    private UserHandler handler;
    private UserRepository repository;

    @Override
    public void onEnable() {
       CashBlockConfiguration.setConfiguration(ConfigManager.create(CashBlockConfiguration.class, (it) -> {
            it.withConfigurer(new YamlBukkitConfigurer());
            it.withSerdesPack(pack -> pack.register(new RewardSerializer()));
            it.withBindFile(new File(getDataFolder(), "config.yml"));
            it.saveDefaults();
            it.load(true); // XDDDDDDDDDd
        }));

       this.repository = new UserRepository();

        RegisteredServiceProvider<HikariDataSource> dataSourceProvider = getServer().getServicesManager().getRegistration(HikariDataSource.class);
        if (dataSourceProvider != null) {
            dataSource = dataSourceProvider.getProvider();
            isDataSourceStolen = true;
            getLogger().info("Uzyto gotowego polaczenia z baza danych od pluginu " + dataSourceProvider.getPlugin().getName());
        } else try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(CashBlockConfiguration.getConfiguration().database.databaseUrl);
            config.setUsername(CashBlockConfiguration.getConfiguration().database.databaseUser);
            config.setPassword(CashBlockConfiguration.getConfiguration().database.databasePassword);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("useLocalSessionState", "true");
            config.addDataSourceProperty("rewriteBatchedStatements", "true");
            config.addDataSourceProperty("cacheResultSetMetadata", "true");
            config.addDataSourceProperty("cacheServerConfiguration", "true");
            config.addDataSourceProperty("elideSetAutoCommits", "true");
            config.addDataSourceProperty("maintainTimeStats", "false");

            dataSource = new HikariDataSource(config);
            getServer().getServicesManager().register(HikariDataSource.class, dataSource, this, ServicePriority.Normal);
        } catch (DatabaseTransactionError error) {
            error.printStackTrace();
            getLogger().severe("Nie udalo sie polaczyc z baza danych. Wylaczanie pluginu...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        try {
            DatabaseManager databaseManager = new DatabaseManager(dataSource.getConnection());
            repository.initialize(databaseManager);
        } catch (SQLException e) {
            e.printStackTrace();
            getLogger().severe("Wystapil blad podczas wlaczania DatabaseManagera; wylaczanie pluginu");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            try {
                repository.save();
            } catch (DatabaseTransactionError transactionError) {
                getLogger().severe("Nie udalo sie zapisac danych! Skontaktuj sie ze mna TERAZ SZYPKO");
                transactionError.printStackTrace();
            }
        }, SAVE_INTERVAL, SAVE_INTERVAL);

        BlockRewardManager blockRewardManager = new BlockRewardManager();
        UserHandler handler = new UserHandler(repository);
        BossBarManager manager;
        try {
            Class.forName("org/bukkit/boss/BarColor");
            manager = new BossbarManagerImpl();
        } catch (ClassNotFoundException ex) {
            manager = new NopBossBarManager();
        }

        getServer().getPluginManager().registerEvents(
                new PlayerBlockBreakListener(handler, manager, blockRewardManager),
                this
        );
        getServer().getPluginManager().registerEvents(
                new InventoryListener(),
                this
        );

        getServer().getScheduler().runTaskTimer(this, manager::doTick, 20L, 20L);

        getCommand("money").setExecutor(new MoneyCommand(handler));
        getCommand("wygrana").setExecutor(new FakeRewardCommand(handler));
        getCommand("przegrana").setExecutor(new TakeMoneyCommand(handler));
        getCommand("tops").setExecutor(new TopCommand(handler));
        getCommand("podlozpitos").setExecutor(new BlockMoneyCommand(blockRewardManager));
        getCommand("boost").setExecutor(new PlayerBoostCommand(handler));

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new CashBlockPlaceholderExpansion(handler).register();
        } else {
            getLogger().warning("Nie znaleziono pluginu PlaceholderAPI! Nie bedziesz mogl uzyc danych z tego pluginu w innych pluginach xd.");
        }
    }

    @Override
    public void onDisable() {
        if (dataSource != null) {
            try {
                repository.save();
            } catch (DatabaseTransactionError transactionError) {
                getLogger().severe("Nie udalo sie zapisac danych! Skontaktuj sie ze mna TERAZ SZYPKO");
                transactionError.printStackTrace();
            }
            if(!isDataSourceStolen)
            dataSource.close();
        }
    }

  public UserHandler getHandler() {
    return handler;
  }
}
