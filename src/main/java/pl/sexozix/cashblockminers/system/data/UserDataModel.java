package pl.sexozix.cashblockminers.system.data;

import pl.memexurer.srakadb.sql.ResultSetDeserializer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public final class UserDataModel {
    private final UUID uuid;
    private final String name;
    private double money;
    private boolean update;

    private double fakeReward;

    public UserDataModel(UUID uuid, String name, double money) {
        this.uuid = uuid;
        this.name = name;
        this.money = money;
    }

    public void addMoney(double value) {
        if (value != value)
            throw new IllegalArgumentException("NaN value detected!");

        if (value < 0)
            throw new IllegalArgumentException("Cannot add negative money");

        this.money += value;
        this.update = true;
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
        if(fakeReward > 0.0d) {
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
        if(update) {
            update = false;
            return true;
        }
        return false;
    }

    public void takeMoney(double reward) {
        this.money -= reward;
        this.update = true;
    }

    static class Deserializer implements
            ResultSetDeserializer<UserDataModel> {

        @Override
        public UserDataModel deserialize(ResultSet resultSet) throws SQLException {
            return new UserDataModel(
                    UUID.fromString(resultSet.getString("PlayerUniqueId")),
                    resultSet.getString("PlayerName"),
                    resultSet.getDouble("PlayerMoney")
            );
        }
    }
}
