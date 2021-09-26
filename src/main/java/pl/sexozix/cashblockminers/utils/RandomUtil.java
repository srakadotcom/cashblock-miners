package pl.sexozix.cashblockminers.utils;

import java.util.concurrent.ThreadLocalRandom;

public final class RandomUtil {
    private RandomUtil() {
    }

    public static boolean chance(double value) {
        return ThreadLocalRandom.current().nextDouble(100) < value;
    }
}
