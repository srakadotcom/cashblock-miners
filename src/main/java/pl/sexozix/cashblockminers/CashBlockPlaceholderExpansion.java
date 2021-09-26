package pl.sexozix.cashblockminers;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import pl.sexozix.cashblockminers.system.data.UserDataModel;
import pl.sexozix.cashblockminers.system.data.UserHandler;

public final class CashBlockPlaceholderExpansion extends PlaceholderExpansion {
    private final UserHandler handler;

    public CashBlockPlaceholderExpansion(UserHandler handler) {
        this.handler = handler;
    }

    @Override
    public String getIdentifier() {
        return "cashblock";
    }

    @Override
    public String getAuthor() {
        return "sexozix";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        UserDataModel dataModel = handler.getCachedDataModel(player.getUniqueId());
        if (dataModel == null)
            return "";
        else
            return String.format("%.2f", dataModel.money());
    }
}
