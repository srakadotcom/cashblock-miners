package pl.sexozix.cashblockminers.system.bossbar.impl.protocollib;


import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Bossy {
    private static int CUSTOM_ID;

    static {
        try {
            String version = Bukkit.getServer().getClass().getName().split("\\.")[3];
            Field field = Class.forName("net.minecraft.server." + version + ".Entity").getDeclaredField("entityCount");
            field.setAccessible(true);
            CUSTOM_ID = field.getInt(null);
            field.set(null, CUSTOM_ID + 1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private final Map<Player, BossBar> bossBars;

    public Bossy(Plugin plugin) {
        this(plugin, 5);
    }

    public Bossy(Plugin plugin, int frequency) throws IllegalArgumentException {
        this.bossBars = new HashMap<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (BossBar bar : bossBars.values()) {
                    if (bar.isSpawned()) {
                        despawn(bar);
                        spawn(bar, getDistantLocation(bar.getPlayer()));
                    }
                }
            }
        }.runTaskTimer(plugin, 0, frequency);
    }

    /**
     * Reveal the boss bar to a player. Also creates a boss bar
     * for the player if it didn't exist.
     *
     * @param player
     */
    public void show(Player player) {
        BossBar bar = getBossBar(player);

        if (bar == null)
            bar = newBossBar(player, "null", 1);

        if (bar.isSpawned())
            despawn(bar);
        spawn(bar, getDistantLocation(player));
    }

    /**
     * Hides the boss bar from a player.
     *
     * @param player
     */
    public void hide(Player player) {
        BossBar bar = getBossBar(player);
        if (bar != null && bar.isSpawned())
            despawn(bar);
    }

    /**
     * Sets the text of the boss bar for a player. Also
     * creates the boss bazr if it didn't exist.
     *
     * @param player
     * @param text
     */
    public void setText(Player player, String text) {
        BossBar bar = getBossBar(player);

        if (bar == null)
            bar = newBossBar(player, text, 1);
        else
            bar.setText(text);

        if (bar.isSpawned())
            despawn(bar);
        spawn(bar, getDistantLocation(player));
    }

    /**
     * Sets the percent of the boss bar for a player. Also
     * creates the boss bazr if it didn't exist.
     *
     * @param player
     * @param percent A value in the range [0,1]
     */
    public void setPercent(Player player, float percent) {
        BossBar bar = getBossBar(player);

        if (bar == null)
            bar = newBossBar(player, "null", (300 * percent));
        else
            bar.setHealth((300 * percent));

        if (bar.isSpawned())
            despawn(bar);
        spawn(bar, getDistantLocation(player));
    }

    /**
     * Sets the text and percent of the boss bar for a player.
     * Also creates the boss bazr if it didn't exist.
     *
     * @param player
     * @param text
     * @param percent A value in the range [0,1]
     */
    public void set(Player player, String text, float percent) {
        BossBar bar = getBossBar(player);

        if (bar == null) {
            bar = newBossBar(player, text, (300 * percent));
        } else {
            bar.setText(text);
            bar.setHealth((300 * percent));
        }

        if (bar.isSpawned())
            despawn(bar);
        spawn(bar, getDistantLocation(player));
    }

    private BossBar getBossBar(Player player) {
        return bossBars.get(player);
    }

    private BossBar newBossBar(Player player, String text, float percent) {
        BossBar bossBar = new BossBar(player, text, percent, null, false);
        bossBars.put(player, bossBar);
        return bossBar;
    }
    private Location getDistantLocation(Player player) {
        return player.getEyeLocation().add(player.getEyeLocation().getDirection().normalize().multiply(5));
    }

    private void spawn(BossBar bar, Location location) {
        bar.setSpawned(true);
        bar.setLocation(location);

        PacketContainer spawnPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
        StructureModifier<Object> spawnPacketModifier = spawnPacket.getModifier();
        spawnPacketModifier.write(0, CUSTOM_ID);
        spawnPacketModifier.write(1, (byte) 64);
        spawnPacketModifier.write(2, location.getBlockX() * 32);
        spawnPacketModifier.write(3, location.getBlockY() * 32);
        spawnPacketModifier.write(4, location.getBlockZ() * 32);
        spawnPacket.getDataWatcherModifier().write(0, bar.getDataWatcher());

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(bar.getPlayer(), spawnPacket, false);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void despawn(BossBar bar) {
        bar.setSpawned(false);
        bar.setLocation(null);

        PacketContainer spawnPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        spawnPacket.getIntegerArrays().write(0, new int[]{CUSTOM_ID});

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(bar.getPlayer(), spawnPacket, false);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private class BossBar {
        private final Player player;
        private String text;
        private float health;
        private Location location;
        private boolean spawned;

        public BossBar(Player player, String text, float health, Location location, boolean spawned) {
            this.player = player;
            this.text = text;
            this.health = health;
            this.location = location;
            this.spawned = spawned;
        }

        public Player getPlayer() {
            return player;
        }


        public void setText(String text) {
            this.text = text;
        }


        public void setHealth(float health) {
            this.health = health;
        }


        public void setLocation(Location location) {
            this.location = location;
        }

        public boolean isSpawned() {
            return spawned;
        }

        public void setSpawned(boolean spawned) {
            this.spawned = spawned;
        }

        private WrappedDataWatcher getDataWatcher() {
            WrappedDataWatcher watcher = new WrappedDataWatcher();
            watcher.setObject(0, (byte) 32);
            watcher.setObject(2, this.text);
            watcher.setObject(6, this.health, true); // Set health
            watcher.setObject(10, this.text);
            watcher.setObject(20, 881);
            return watcher;
        }
    }
}