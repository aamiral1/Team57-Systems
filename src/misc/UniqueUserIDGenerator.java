package misc;

import java.util.UUID;

public class UniqueUserIDGenerator {
    public static String generateUniqueUserID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public static void main(String[] args) {
        UniqueUserIDGenerator myUuid = new UniqueUserIDGenerator();
        System.out.println(myUuid.generateUniqueUserID());

    }
}