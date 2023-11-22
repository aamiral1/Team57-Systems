package main.misc;

import java.util.UUID;

public class UniqueUserIDGenerator {
    public static String generateUniqueUserID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}