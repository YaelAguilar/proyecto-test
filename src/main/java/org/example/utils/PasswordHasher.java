package org.example.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {
    private static final int WORKLOAD_FACTOR = 12;

    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(WORKLOAD_FACTOR));
    }

    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}