package pl.sexozix.cashblockminers.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.sexozix.cashblockminers.system.data.UserHandler;
import pl.sexozix.cashblockminers.utils.ChatUtil;

public class SetMoneyCommand implements CommandExecutor {

  private final UserHandler userHandler;

  public SetMoneyCommand(UserHandler userHandler) {
    this.userHandler = userHandler;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    Player player = (Player) sender;
    if (!player.hasPermission("cashblock.sethajs")) return false;
    if (args.length == 0) {
      ChatUtil.sendMessage(player, "&7Poprawne użycie: &f/sethajs <ilosc> <gracz>");
      return false;
    }
    if (args.length == 1) {
      var dataModel = userHandler.getCachedDataModel(player.getUniqueId());
      dataModel.setMoney(Double.parseDouble(args[0]));
      ChatUtil.sendMessage(player, "&8>> &7Ustawiłeś sobie kaske na " + args[0]);
      return false;
    }
    var otherDataModel = userHandler.findUserByName(args[1]);
    otherDataModel.setMoney(Double.parseDouble(args[0]));
    ChatUtil.sendMessage(player, "&7Ustawiłeś graczowi " + args[1] + " kase na " +  args[0]);
    return false;
  }
}
