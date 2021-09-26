package pl.sexozix.cashblockminers.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pl.sexozix.cashblockminers.system.data.UserDataModel;
import pl.sexozix.cashblockminers.system.data.UserHandler;

public class TakeMoneyCommand implements CommandExecutor {
    private final UserHandler handler;

    public TakeMoneyCommand(UserHandler handler) {
        this.handler = handler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("cashblockminers.takemoney")) {
            sender.sendMessage(ChatColor.RED + "Sussy baka >:(");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Poprawne uzycie: /" + label + " (nick) (hajs)");
            return true;
        }

        double reward = 0.0d;
        try {
            reward = Double.parseDouble(args[1]);

            if(!Double.isFinite(reward))
                throw new NumberFormatException();
        } catch (NumberFormatException exception) {
            sender.sendMessage(ChatColor.RED + "Nieprawidlowa liczba!");
        }

        UserDataModel dataModel = handler.findUserByName(args[0]);
        if (dataModel == null) {
            sender.sendMessage(ChatColor.RED + "Nie znaleziono gracza o takim nicku!");
            return true;
        }

        dataModel.takeMoney(reward);
        sender.sendMessage(ChatColor.GREEN + "Ustawiono!");
        return true;
    }
}
