package jwt设计;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    // Secret Key for signing and verifying the JWT token (keep it safe)
    private static final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256); // HMAC-SHA256

    // Token validity period (e.g., 1 hour)
    private static final long EXPIRATION_TIME = 3600000; // 1 hour in milliseconds

    // Generate JWT token
    public static String generateToken(Long userId, String role) {
        // Define claims (the data stored inside the JWT)
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId.toString()) // Subject (typically the user identifier)
                .setIssuedAt(new Date(System.currentTimeMillis())) // Token creation date
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Expiration time
                .signWith(secretKey) // Sign with the secret key
                .compact(); // Build the token
    }

    // Same secret key used for both signing and verifying

    // Parse and validate JWT token
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey) // Set the same secret key
                .build()
                .parseClaimsJws(token) // Parse the token
                .getBody(); // Get the claims (payload)
    }


    public static void main(String[] args) {
        // Example: signing a JWT for a user
        Long userId = 123L;
        String role = "SUPER_ADMIN"; // Other roles: REVIEWER, AGENT_MANAGER

        String token = generateToken(userId, role);
        System.out.println("Generated Token: " + token);

        //*****************************

        // Example token from the previous step
        String tokenGet = generateToken(123L, "SUPER_ADMIN");

        // Parse the token and extract claims
        Claims claims = parseToken(tokenGet);

        Long userIdGet = Long.valueOf(claims.get("userId").toString());
        String roleGet = claims.get("role", String.class);

        System.out.println("UserId: " + userIdGet);
        System.out.println("Role: " + roleGet);
    }
}
