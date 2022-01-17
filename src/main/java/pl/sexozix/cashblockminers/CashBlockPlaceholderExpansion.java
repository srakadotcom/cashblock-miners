package pl.sexozix.cashblockminers;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import pl.sexozix.cashblockminers.system.data.UserDataModel;
import pl.sexozix.cashblockminers.system.data.UserHandler;

import java.util.List;

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
        if(params.equals("moneyint")) {
            return String.valueOf((int) dataModel.money());
        } else if(params.equals("moneyfloat")) {
            if (dataModel == null)
                return "0.0";
            else
                return String.format("%.2f", dataModel.money());
        } else if(params.startsWith("moneytop-")) {
            List<UserDataModel> tops = handler.getOrCreateCachedTops();
            if(tops == null)
                return "Prosze czekac...";

            int amount = Integer.parseInt(params.substring(9));
            if(amount >= tops.size())
                return "Brak";

            UserDataModel dataModel1 = tops.get(amount);
            return dataModel1.name() + " (" + dataModel1.money() + "zł)";
        } else if(params.equals("moneytop")) {
            List<UserDataModel> tops = handler.getOrCreateCachedTops();
            if(tops == null)
                return "Prosze czekac...";

            return Integer.toString(tops.indexOf(dataModel));
        }

        return null;
    }
}
