package org.example.util;

import java.util.UUID;

public abstract class CommonUtils {
    public static String uuid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-", "");
    }
}
