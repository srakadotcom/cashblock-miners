package pl.sexozix.cashblockminers.system.data;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class UserHandler {
    private final UserRepository repository;

    public UserHandler(UserRepository repository) {
        this.repository = repository;
    }

    public UserDataModel getUserDataModel(Player player) {
        return repository.getOrCreateUser(player.getName(), player.getUniqueId());
    }

    public UserDataModel getCachedDataModel(UUID uuid) {
        return repository.getExistingUser(uuid);
    }

    public CompletableFuture<List<UserDataModel>> fetchTops() {
        return CompletableFuture.supplyAsync(repository::createTops);
    }

    public UserDataModel findUserByName(String name) {
        return repository.findUserByName(name);
    }
}
