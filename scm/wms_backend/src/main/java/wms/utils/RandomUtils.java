package wms.utils;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {

    /**
     * Create random uid for Entity
     *
     * @return
     */
    public static String getRandomId() {
        return UUID.randomUUID().toString().replace('-', '_');
    }

    /**
     * Create random number from in [min, max)
     *
     * @param min
     * @param max
     * @return
     */
    public static long getRandomNumber(long min, long max) {
        return ThreadLocalRandom.current().nextLong(min, max);
    }

    /**
     * Returns a pseudorandom int value between the specified origin (inclusive) and the specified bound (exclusive).
     *
     * @param min inclusive
     * @param max exclusive
     * @return random number
     * @throws IllegalArgumentException - if origin is greater than or equal to bound
     */
    public static int getRandomNumber(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    /**
     * Get random password number from 0-999999
     *
     * @return random string from 000000-999999
     */
    public static String getRandomPasswordNumber() {
        long value = getRandomNumber(0L, 1000000L);
        return String.format("%08d", value);
    }

}
