package pl.sexozix.cashblockminers.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.sexozix.cashblockminers.system.blockreward.BlockRewardManager;

public class BlockMoneyCommand implements CommandExecutor {
    private final BlockRewardManager blockRewardManager;

    public BlockMoneyCommand(BlockRewardManager blockRewardManager) {
        this.blockRewardManager = blockRewardManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        if (!sender.hasPermission("cashblockminers.win")) {
            sender.sendMessage(ChatColor.RED + "Sussy baka >:(");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Poprawne uzycie: /" + label + " (ilosc hajsu)");
            return true;
        }

        Block block = player.getTargetBlock(null, 5);
        if (block.getType() == Material.AIR) {
            sender.sendMessage(ChatColor.RED + "Aktualnie nie patrzysz sie na zaden blok.");
            return true;
        }

        double reward = 0.0d;
        try {
            reward = Double.parseDouble(args[0]);

            if (!Double.isFinite(reward))
                throw new NumberFormatException();
        } catch (NumberFormatException exception) {
            sender.sendMessage(ChatColor.RED + "Nieprawidlowa liczba!");
        }
        blockRewardManager.setReward(block.getLocation(), reward);
        sender.sendMessage(ChatColor.GREEN + "Pomyslnie ustawiono scama!");
        return true;
    }
}
