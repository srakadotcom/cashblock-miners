package pl.sexozix.cashblockminers.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

public final class ChatUtil {
    private ChatUtil() {
    }

    public static String fixColor(String message) {
        return ChatColor.translateAlternateColorCodes('&', message.replace(">>", "\u00bb").replace("<<", "\u00ab"));
    }

    public static void sendMessage(CommandSender player, String message) {
        player.sendMessage(fixColor(message));
    }
} //seks ruchanie
