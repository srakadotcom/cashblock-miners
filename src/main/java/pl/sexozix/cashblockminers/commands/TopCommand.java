package pl.sexozix.cashblockminers.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import pl.sexozix.cashblockminers.system.data.UserDataModel;
import pl.sexozix.cashblockminers.system.data.UserHandler;
import pl.sexozix.cashblockminers.system.inventory.ClickableInventory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TopCommand implements CommandExecutor {
    private final UserHandler userHandler;

    public TopCommand(UserHandler userHandler) {
        this.userHandler = userHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Komenda dostepna tylko dla graczy.");
            return true;
        }

        if (!sender.hasPermission("cashblockminers.top")) {
            sender.sendMessage(ChatColor.RED + "Sussy baka >:(");
            return true;
        }

        CompletableFuture<List<UserDataModel>> tops = userHandler.fetchTops();//wyjebane
        sender.sendMessage(ChatColor.GRAY + "Czekaj, trwa generowanie topek...");
        tops.whenComplete((userDataModels, throwable) -> {
            if (throwable != null) {
                sender.sendMessage(ChatColor.RED + "Wystapil blad podczas generowania topek: " + throwable.getMessage());
                throwable.printStackTrace();
            } else {
                ((Player) sender).openInventory(new TopInventory(userDataModels).getInventory());
            }
        });
        return true;
    }

    private static class TopInventory implements ClickableInventory {
        private final List<UserDataModel> dataModels;

        private TopInventory(List<UserDataModel> dataModels) {
            this.dataModels = dataModels;
        }

        private static ItemStack createHeadItemStack() {
            try {
                return new ItemStack(Material.PLAYER_HEAD);
            } catch (Exception ex) {
                return new ItemStack(Material.getMaterial("SKULL_ITEM"), 1, (short) 3);
            }
        }

        @Override
        public Inventory getInventory() {
            Inventory inventory = Bukkit.createInventory(this, 9, "Topki");
            int i = 1;

            for (UserDataModel dataModel : dataModels) {
                inventory.setItem(i - 1, createPlayerSkull(dataModel, i++));
            }
            return inventory;
        }

        private ItemStack createPlayerSkull(UserDataModel dataModel, int topNumber) {
            ItemStack headStack = createHeadItemStack();
            headStack.setAmount(topNumber);

            SkullMeta meta = (SkullMeta) headStack.getItemMeta();
            meta.setDisplayName(ChatColor.GRAY.toString() + topNumber + ". " + dataModel.name());
            meta.setLore(List.of(ChatColor.GRAY + "Ilosc hajsu: " + dataModel.money()));
            headStack.setItemMeta(meta);
            return headStack;
        }

    }
}
