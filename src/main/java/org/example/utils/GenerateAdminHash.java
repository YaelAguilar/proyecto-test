package org.example.utils;

import org.mindrot.jbcrypt.BCrypt;

public class GenerateAdminHash {
    public static void main(String[] args) {
        // Usa el mismo WORKLOAD_FACTOR que tu PasswordHasher
        int WORKLOAD_FACTOR = 12;
        
        // La contraseña que quieres hashear
        String password = "123456789";   
        
        // Generar el hash
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(WORKLOAD_FACTOR));
        
        System.out.println("=================================");
        System.out.println("Password: " + password);
        System.out.println("Hash: " + hashedPassword);
        System.out.println("=================================");
        
        // Verificar que funciona
        boolean matches = BCrypt.checkpw(password, hashedPassword);
        System.out.println("Verificación: " + (matches ? "CORRECTO" : "ERROR"));
    }
}