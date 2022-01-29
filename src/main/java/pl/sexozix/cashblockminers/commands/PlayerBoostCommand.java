package pl.sexozix.cashblockminers.commands;

import java.util.Stack;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.sexozix.cashblockminers.system.data.UserDataModel;
import pl.sexozix.cashblockminers.system.data.UserHandler;

public class PlayerBoostCommand implements CommandExecutor {

  private final UserHandler userHandler;

  public PlayerBoostCommand(UserHandler userHandler) {
    this.userHandler = userHandler;
  }

  public static long parseTime(String string) {
    if (string == null || string.isEmpty()) {
      return 0;
    }

    Stack<Character> type = new Stack<>();
    StringBuilder value = new StringBuilder();

    boolean calc = false;
    long time = 0;

    for (char c : string.toCharArray()) {
      switch (c) {
        case 'd', 'h', 'm', 's' -> {
          if (!calc) {
            type.push(c);
          }
          long i = Integer.parseInt(value.toString());
          switch (type.pop()) {
            case 'd' -> time += i * 86400000L;
            case 'h' -> time += i * 3600000L;
            case 'm' -> time += i * 60000L;
            case 's' -> time += i * 1000L;
          }
          type.push(c);
          calc = true;
        }
        default -> value.append(c);
      }
    }

    return time;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if(!sender.hasPermission("cashblockminers.boost") && sender instanceof Player) {
      UserDataModel model = userHandler.getUserDataModel((Player) sender);
      if(!model.isBoostActive()) {
        sender.sendMessage(ChatColor.RED + "Nie posiadasz turbocasha aktywnego!");
        return true;
      }
      sender.sendMessage(ChatColor.GRAY + "Twoj turbocash wygasnie za: " + getDurationBreakdown(model.boostExpire() - System.currentTimeMillis()));
      return true;
    }

    if (args.length != 2) {
      sender.sendMessage(ChatColor.RED + "Uzycie: /turbocash (nick) (czas)");
      return true;
    }

    UserDataModel model = userHandler.findOnlineUserByName(args[0]);;
    if (model == null) {
      sender.sendMessage(ChatColor.RED + "Nie znaleziono gracza!");
      return true;
    }

    long time;
    try {
      time = parseTime(args[1]);
    } catch (NumberFormatException e) {
      time = 0;
    }

    if(time == 0) {
      sender.sendMessage(ChatColor.RED + "Podales nieprawidlowy czas! Format czasu to: 1h1m1s itd gowno");
      return true;
    }

    model.setBoostExpire(System.currentTimeMillis() + time);
    sender.sendMessage(ChatColor.GREEN + "Aktywowano boosta!");
    return true;
  }

  public static String getDurationBreakdown(long millis) {
    if (millis == 0) {
      return "0";
    }

    long days = TimeUnit.MILLISECONDS.toDays(millis);
    if (days > 0) {
      millis -= TimeUnit.DAYS.toMillis(days);
    }

    long hours = TimeUnit.MILLISECONDS.toHours(millis);
    if (hours > 0) {
      millis -= TimeUnit.HOURS.toMillis(hours);
    }

    long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
    if (minutes > 0) {
      millis -= TimeUnit.MINUTES.toMillis(minutes);
    }

    long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
    if (seconds > 0) {
      millis -= TimeUnit.SECONDS.toMillis(seconds);
    }

    StringBuilder sb = new StringBuilder();

    if (days > 0) {
      sb.append(days);

      if (days == 1) {
        sb.append(" dzien ");
      }
      else {
        sb.append(" dni ");
      }
    }

    if (hours > 0) {
      sb.append(hours);

      long last = hours % 10;
      long lastTwo = hours % 100;

      if (hours == 1) {
        sb.append(" godzine ");
      }
      else if (last < 5 && (lastTwo < 11 || lastTwo > 14)) {
        sb.append(" godziny ");
      }
      else {
        sb.append(" godzin ");
      }
    }

    if (minutes > 0) {
      sb.append(minutes);

      long last = minutes % 10;
      long lastTwo = minutes % 100;

      if (minutes == 1) {
        sb.append(" minute ");
      }
      else if (last < 5 && (lastTwo < 11 || lastTwo > 14)) {
        sb.append(" minuty ");
      }
      else {
        sb.append(" minut ");
      }
    }

    if (seconds > 0) {
      sb.append(seconds);
      long last = seconds % 10;
      long lastTwo = seconds % 100;

      if (seconds == 1) {
        sb.append(" sekunde ");
      }
      else if (last < 5 && (lastTwo < 11 || lastTwo > 14)) {
        sb.append(" sekundy ");
      }
      else {
        sb.append(" sekund ");
      }
    }

    return (sb.toString());
  }
}
