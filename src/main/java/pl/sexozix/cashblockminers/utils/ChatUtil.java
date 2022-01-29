package pl.sexozix.cashblockminers.utils;

import java.lang.reflect.Constructor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.concurrent.TimeUnit;
import org.bukkit.entity.Player;

public final class ChatUtil {
    private ChatUtil() {
    }

    public static String fixColor(String message) {
        return ChatColor.translateAlternateColorCodes('&', message.replace(">>", "\u00bb").replace("<<", "\u00ab"));
    }

    public static void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Class<?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void sendActionBar(Player player, String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        Class<?> chatSerializer = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0];
        Class<?> chatComponent = getNMSClass("IChatBaseComponent");
        Class<?> packetActionbar = getNMSClass("PacketPlayOutChat");
        try {
            Constructor<?> ConstructorActionbar = packetActionbar.getDeclaredConstructor(chatComponent, byte.class);
            Object actionbar = chatSerializer.getMethod("a", String.class).invoke(chatSerializer, "{\"text\": \"" + message + "\"}");
            Object packet = ConstructorActionbar.newInstance(actionbar, (byte) 2);
            sendPacket(player, packet);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
            } else {
                sb.append(" dni ");
            }
        }

        if (hours > 0) {
            sb.append(hours);

            long last = hours % 10;
            long lastTwo = hours % 100;

            if (hours == 1) {
                sb.append(" godzine ");
            } else if (last < 5 && (lastTwo < 11 || lastTwo > 14)) {
                sb.append(" godziny ");
            } else {
                sb.append(" godzin ");
            }
        }

        if (minutes > 0) {
            sb.append(minutes);

            long last = minutes % 10;
            long lastTwo = minutes % 100;

            if (minutes == 1) {
                sb.append(" minute ");
            } else if (last < 5 && (lastTwo < 11 || lastTwo > 14)) {
                sb.append(" minuty ");
            } else {
                sb.append(" minut ");
            }
        }

        if (seconds > 0) {
            sb.append(seconds);
            long last = seconds % 10;
            long lastTwo = seconds % 100;

            if (seconds == 1) {
                sb.append(" sekunde ");
            } else if (last < 5 && (lastTwo < 11 || lastTwo > 14)) {
                sb.append(" sekundy ");
            } else {
                sb.append(" sekund ");
            }
        }

        if(sb.isEmpty()) {
            sb.append(":(");
        }

        return (sb.toString());
    }

    public static void sendMessage(CommandSender player, String message) {
        player.sendMessage(fixColor(message));
    }
} //seks ruchanie
