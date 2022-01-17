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
import pl.sexozix.cashblockminers.CashBlockPlugin;
import pl.sexozix.cashblockminers.system.data.UserDataModel;
import pl.sexozix.cashblockminers.system.data.UserHandler;
import pl.sexozix.cashblockminers.system.inventory.ClickableInventory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TopCommand implements CommandExecutor {
    private final CashBlockPlugin plugin;
    private final UserHandler userHandler;

    public TopCommand(CashBlockPlugin plugin, UserHandler userHandler) {
        this.plugin = plugin;
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

        CompletableFuture<List<UserDataModel>> tops = userHandler.fetchTops();
        sender.sendMessage(ChatColor.GRAY + "Czekaj, trwa generowanie topek...");
        tops.whenComplete((userDataModels, throwable) -> {
            if (throwable != null) {
                sender.sendMessage(ChatColor.RED + "Wystapil blad podczas generowania topek: " + throwable.getMessage());
                throwable.printStackTrace();
            } else {
                sender.sendMessage(ChatColor.GREEN + "Wygenerowano topki!");
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    ((Player) sender).openInventory(new TopInventory(userDataModels).getInventory());
                });
            }
        });
        return true;
    }

    private static class TopInventory implements ClickableInventory {
        private final List<UserDataModel> dataModels;

        private TopInventory(List<UserDataModel> dataModels) {
            this.dataModels = dataModels.subList(0, Math.min(27, dataModels.size()));
        }

        private static ItemStack createHeadItemStack() {
            try {
                return new ItemStack(Material.PLAYER_HEAD);
            } catch (NoSuchFieldError ex) {
                return new ItemStack(Material.getMaterial("SKULL_ITEM"), 1, (short) 3);
            }
        }

        @Override
        public Inventory getInventory() {
            Inventory inventory = Bukkit.createInventory(this, 27, "Topki");
            for(int i = 0; i < dataModels.size(); i++) {
                inventory.setItem(i, createPlayerSkull(dataModels.get(i), i + 1));
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
