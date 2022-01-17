package pl.sexozix.cashblockminers.system.data;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class UserHandler {
    private static final long CACHE_EXPIRATION_TIME = 60000;
    private final UserRepository repository;
    private List<UserDataModel> cachedTops;
    private long topCacheExpiration;

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
        if (cachedTops == null || System.currentTimeMillis() > topCacheExpiration) {
            return CompletableFuture.supplyAsync(repository::createTops)
                    .thenApply(tops -> {
                        cachedTops = tops;
                        topCacheExpiration = System.currentTimeMillis() + CACHE_EXPIRATION_TIME;
                        return cachedTops;
                    });
        }
        return CompletableFuture.completedFuture(cachedTops);
    }

    public List<UserDataModel> getOrCreateCachedTops() {
        List<UserDataModel> tops = null;

        CompletableFuture<List<UserDataModel>> completableFuture = fetchTops();
        if(completableFuture.isDone()) {
            try {
                tops = completableFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        } else {
            return cachedTops;
        }

        return tops;
    }

    public UserDataModel findUserByName(String name) {
        return repository.findUserByName(name);
    }
}
