package pl.sexozix.cashblockminers.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.sexozix.cashblockminers.system.data.UserDataModel;
import pl.sexozix.cashblockminers.system.data.UserHandler;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class TopCommand implements CommandExecutor
{
    private final UserHandler userHandler;

    public TopCommand(UserHandler userHandler) {
        this.userHandler = userHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("cashblockminers.top")) {
            sender.sendMessage(ChatColor.RED + "Sussy baka >:(");
            return true;
        }

        CompletableFuture<List<UserDataModel>> tops = userHandler.fetchTops();//wyjebane
        sender.sendMessage(ChatColor.GRAY + "Czekaj, trwa generowanie topek...");
        tops.whenComplete((userDataModels, throwable) -> {
            if(throwable != null) {
                sender.sendMessage(ChatColor.RED + "Wystapil blad podczas generowania topek: " + throwable.getMessage());
                throwable.printStackTrace();
            } else {
                int i = 1;

                for(UserDataModel dataModel: userDataModels) {
                    sender.sendMessage(ChatColor.GRAY + (i++ + ": " + dataModel.name() + " - " + dataModel.money()));
                }
            }
        });
        return true;
    }
}
