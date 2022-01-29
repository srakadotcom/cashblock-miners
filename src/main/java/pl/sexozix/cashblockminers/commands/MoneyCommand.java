package pl.sexozix.cashblockminers.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.sexozix.cashblockminers.CashBlockConfiguration;
import pl.sexozix.cashblockminers.utils.ChatUtil;
import pl.sexozix.cashblockminers.system.data.UserDataModel;
import pl.sexozix.cashblockminers.system.data.UserHandler;

public class MoneyCommand implements CommandExecutor
{
    private final UserHandler userHandler;

    public MoneyCommand(UserHandler userHandler) {
        this.userHandler = userHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        if(!sender.hasPermission("cashblockminers.balance") || args.length == 0){
            UserDataModel user = userHandler.getUserDataModel(player);
            ChatUtil.sendMessage(player, CashBlockConfiguration.getConfiguration().messages.hajsCommandMessage
                    .replace("{MONEY}", String.valueOf(user.money())));
            return true;
        }

        if(sender.hasPermission("cashblockminers.balance")) {
            if(args.length != 1) {
                sender.sendMessage(ChatColor.RED + "Poprawne uzycie: /" + label + " (nick)");
            } else {
                UserDataModel dataModel = userHandler.findOnlineUserByName(args[0]);
                if (dataModel == null) {
                    sender.sendMessage(ChatColor.RED + "Nie znaleziono gracza o takim nicku!");
                    return true;
                }

                sender.sendMessage(ChatColor.GRAY + "Gracz posiada " + dataModel.money() +"$ hajsu.");
            }
        }
        return true;
    }
}
