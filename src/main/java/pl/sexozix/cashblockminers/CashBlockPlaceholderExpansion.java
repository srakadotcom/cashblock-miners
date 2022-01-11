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
        if(params.equals("int")) {
            return String.valueOf((int) dataModel.money());
        } else if(params.equals("float")) {
            if (dataModel == null)
                return "0.0";
            else
                return String.format("%.2f", dataModel.money());
        }

        return null;
    }
}
