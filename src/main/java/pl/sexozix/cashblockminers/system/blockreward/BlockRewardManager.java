package pl.sexozix.cashblockminers.system.blockreward;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalDouble;

public class BlockRewardManager {
    private final Map<Location, Double> rewards = new HashMap<>();

    public OptionalDouble getReward(Location location) {
        for (Map.Entry<Location, Double> reward : rewards.entrySet()) {
            Location location1 = reward.getKey();
            if (location1.getWorld().equals(location.getWorld()) &&
                    location1.getBlockX() == location.getBlockX() &&
                    location1.getBlockY() == location.getBlockY() &&
                    location1.getBlockZ() == location.getBlockZ())
                return OptionalDouble.of(reward.getValue());
        }
        return OptionalDouble.empty();
    }

    public void setReward(Location location, double reward) {
        this.rewards.put(location, reward);
    }
}
