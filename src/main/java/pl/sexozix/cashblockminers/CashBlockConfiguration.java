package pl.sexozix.cashblockminers;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.Header;
import org.bukkit.Material;
import pl.sexozix.cashblockminers.system.reward.Reward;

import java.util.Arrays;
import java.util.List;

@Header("## CashBlockPlugin - dojebany plugin na cashblocka")
@Header("## Pozdro")
public class CashBlockConfiguration extends OkaeriConfig {
    private static CashBlockConfiguration configuration;
    @CustomKey("bossbar")
    public Bossbar bossbar = new Bossbar(
            "&8>> &7Gracz &2{PLAYER} &7wykopal &2{MONEY} &7hajsiwa &8<<",
            "BLUE",
            "SOLID"
    );
    @CustomKey("rewardsMoney")
    @Comment("Nagrody za kopanie")
    public List<Reward> rewardList = Arrays.asList(
            new Reward(50, 1),
            new Reward(50, 2)
    );
    @CustomKey("database")
    public Database database = new Database(
            "root",
            "",
            "jdbc:mysql://localhost:3306/database"
    );
    @CustomKey("messages")
    public Messages messages = new Messages(
            "&8>> &7Posiadasz &2{MONEY} &7hajsiwa!",
            "&7Wykopales &2{MINED-MONEY} &8hajsu!",
            "&7Twoj aktualny stan portfela &2{ACTUAL-MONEY}",
            "&7Gracz &2{PLAYER} &7wykopal &2{MINED-MONEY} hajsiwa"
    );
    @CustomKey("blocks")
    @Comment("Na jakie bloki ma dzialac ten plugin?")
    public List<Material> blocks = Arrays.asList(
            Material.STONE,
            Material.BEDROCK
    );

    public static CashBlockConfiguration getConfiguration() {
        return configuration;
    }

    public static void setConfiguration(CashBlockConfiguration configuration) {
        CashBlockConfiguration.configuration = configuration;
    }

    public class Bossbar extends OkaeriConfig {
        @CustomKey("message")
        public String bossbarMoneyDisplayMessage;
        @CustomKey("color")
        @Comment("Kolor bossbara")
        public String bossbarColor;
        @CustomKey("style")
        @Comment("Styl bossbara")
        public String bossbarStyle;

        public Bossbar(String bossbarMoneyDisplayMessage, String bossbarColor, String bossbarStyle) {
            this.bossbarMoneyDisplayMessage = bossbarMoneyDisplayMessage;
            this.bossbarColor = bossbarColor;
            this.bossbarStyle = bossbarStyle;
        }
    }

    public class Database extends OkaeriConfig {
        @CustomKey("user")
        @Comment("Nazwa uzytkownika bazy danych")
        public String databaseUser;
        @CustomKey("password")
        @Comment("Haslo uzytkownika bazy danych")
        public String databasePassword;
        @CustomKey("url")
        @Comment("Url bazy danych")
        public String databaseUrl;

        public Database(String databaseUser, String databasePassword, String databaseUrl) {
            this.databaseUser = databaseUser;
            this.databasePassword = databasePassword;
            this.databaseUrl = databaseUrl;
        }
    }

    public class Messages extends OkaeriConfig {
        @CustomKey("commandHajs")
        @Comment("Wiadomosc po wpisaniu /hajs (Zmienna: {MONEY} pokazuje ilosc twojego hajsu)")
        public String hajsCommandMessage;
        @CustomKey("titleMessage")
        @Comment("Title po wykopaniu hajsu (Zmienne: {ACTUAL-MONEY}, {MINED-MONEY})")
        public String titleMessage;
        @CustomKey("subtitleMessage")
        @Comment("SubTitle po wykopaniu hajsu (Zmienne: {ACTUAL-MONEY}, {MINED-MONEY})")
        public String subtitleMessage;
        @CustomKey("playerMinedBroadcast")
        public String playerMinedBroadcast;

        public Messages(String hajsCommandMessage, String titleMessage, String subtitleMessage, String playerMinedBroadcast) {
            this.hajsCommandMessage = hajsCommandMessage;
            this.titleMessage = titleMessage;
            this.subtitleMessage = subtitleMessage;
            this.playerMinedBroadcast = playerMinedBroadcast;
        }
    }
}
