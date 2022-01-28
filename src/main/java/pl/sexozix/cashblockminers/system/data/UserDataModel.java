package pl.sexozix.cashblockminers.system.data;

import pl.memexurer.srakadb.sql.mapper.SerializableTableColumn;
import pl.memexurer.srakadb.sql.mapper.TableColumnInfo;
import pl.memexurer.srakadb.sql.mapper.TypedTableColumn;
import pl.memexurer.srakadb.sql.mapper.serializer.UuidValueDeserializer;

import java.util.UUID;

public final class UserDataModel {
    @TableColumnInfo(primary = true, name = "PlayerUniqueId", serialized = @SerializableTableColumn(UuidValueDeserializer.class))
    private final UUID uuid;
    @TableColumnInfo(name = "PlayerName", typed = @TypedTableColumn("varchar(16)"))
    private final String name;
    @TableColumnInfo(name = "PlayerMoney", typed = @TypedTableColumn("decimal(15,2)"))
    private double money;
    private boolean update;
    @TableColumnInfo(name = "BoostExpire", typed = @TypedTableColumn("integer(8)"))
    private long boostExpire;
    @TableColumnInfo(name = "BoostStart", typed = @TypedTableColumn("integer(8)"))
    private long boostStart;

    private double fakeReward;

    public UserDataModel(UUID uuid, String name, double money, long boostExpire, long boostStart) {
        this.uuid = uuid;
        this.name = name;
        this.money = money;
        this.boostExpire = boostExpire;
        this.boostStart = boostStart;
    }

    public void addMoney(double value) {
        if (value != value)
            throw new IllegalArgumentException("NaN value detected!");

        if (value < 0)
            throw new IllegalArgumentException("Cannot add negative money");

        this.money += value;
        this.update = true;
    }

    public void setMoney(double money) {
        if (money != money)
            throw new IllegalArgumentException("NaN value detected!");
        this.money = money;
        this.update = true;
    }

    public void setBoostExpire(long expire) {
        this.boostExpire = expire;
        this.boostStart = System.currentTimeMillis();
    }

    public long boostExpire() {
        return boostExpire;
    }

    long boostStart() {
        return boostStart;
    }

    public float boostPercentage() {
        return (float) (boostExpire - System.currentTimeMillis()) / (boostExpire - boostStart);
    }

    public boolean isBoostActive() {
        return boostExpire > System.currentTimeMillis();
    }

    public UUID uuid() {
        return uuid;
    }

    public String name() {
        return name;
    }

    public double money() {
        return money;
    }

    public double fakeReward() {
        if (fakeReward > 0.0d) {
            double reward = fakeReward;
            fakeReward = 0;
            return reward;
        }

        return fakeReward;
    }

    public void setFakeReward(double fakeReward) {
        this.fakeReward = fakeReward;
    }

    public boolean update() {
        if (update) {
            update = false;
            return true;
        }
        return false;
    }

    public void takeMoney(double reward) {
        this.money -= reward;
        this.update = true;
    }
}
