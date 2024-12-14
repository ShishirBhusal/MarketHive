package com.example.iconnect;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTokenGenerator {

    // Replace with your actual Secret Key (ensure it's a single-line string without newlines)
    private static final String SECRET_KEY = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCpVs2gnFKE18yxS/jcddvfvE9mwE2EaXUESHe7HPJudLFr+qp7MMWmfkWYXU3Ibc1GbJgiajxo611H4evL4deYkm9KNYE4xLMYKdCFrdGUa9GMXsg3i2a7OWqiK+/JfPta7OKm6EMmN8DQF/cnKou0prS43++6+VKwYVUTLwzToKskYD7yJZCG8WhOXJg3Cj4thPdR3aW/3dK0BlSlqcOHkMwmc9zlh7HlbPsGtTSie4Tv+7n3fRpXshyuvCM5IRLbV8U0XSd0HP48/jQbekgifN1qswmAnD3c7r9KIHsg3td4y4nt/ntf+f3WRJCNif2Ak7QDYC25JR24698HXzDbAgMBAAECggEAWDFRHpjLkUwi2yPKFGd9f+S/JbB5rsCNpRFo6haFXGmCDc6bA1W/Wprg/B176SrmggsEoMfbdnk+N7W97I1Oj7OUKn37MumerLL6WuTQBhCxLf4fIG6FrnSUyKlBVRq2fgBpeqv0bO8UiEwy+D78S7WW4cIfRK2dFydZ+4fBkmV+GBpHfztIumjWGGcEplISxlqCOHU2qCF5WFv+J8hdJtsHiDdCEcXUQ55lJV4fi0E+mcDLC8MEnVayLDvrHzAhFCPqne3WGtKh9Vin6sYtgSknabJTrFxzKZN1ex7OpTOxErGIrESbC68cQU6CAOz36lTLq5NJdSr1gDBqsDrcAQKBgQDazJMsm87GkI8y8ahu/jSuncOAUnpPf0d6Hz859+072yEUaLR3FLxy4KQVNoPOwlZohxcby4Lcg0cGaO52bqXKE2jia7nNDCYIXbwIIvJm0XRIeV4RCnIgB/oWr3xNEDuzLxws8/I+jZRLr+dYAlVslX9H0QsNMTrjlUKipFnczgQKBgQDGIW+mptdfzjr2j3UXpx5z/gl5pNyisr5y7eYBqKfdpM2kcvhaEUMeYvOY2TS2puaInSUrzHmWwbT9rCpx4IpmuxdvJFbEKQRZjyxIJWEZpC+MvdipzLULMRLzFmqJF95CAjHcGF1rCy1VPZW/w/0Yk2LPcLKQSgSyP5i484jiWwKBgC7sZrfPZmYRRJSxXOTwo6cSPwxNAfujP60ytN8x0Tp9sswtVtAeDXO1YGzIktqRCvApQUR/Subf9TxFgsSKb4PsZO1dLBz5Ygvyg6YqBHC48RTOwg+jEEwVMMeuXKxg4wnE0zUuGQP6vjcbanjPjy+RwC45TzcDPwX26WTx63iBAoGAewBa/kzpvHKdrGAXQ13ezAfT/g7ZYrAxB8ylVDZOMI2VlRhDEjdRQdYQgL2nuhUXXYyhfPVuLqeHI0z09Ml/YpOX0snui80jBC6cQRXjmDWTx348uf16D76hkwRBk2ab9sLCDW+a30+LDzYyd2DHCbDGUR4X3Pf2vmo49+lI+60CgYBWzqJW6vmPpm0cFb1BYIM/5dyhPXDtp7YRW5iKQFYWC/xkBemiUe/KDfR9WylkIs4rYFfMhOW5twed2b4v6Y26IAEx2AZBbWH1Ee2ub+EifptgmdlMizCQ03CN9YAaplAftSYIkjC0EPqK8kHZAs2J75DtE/e82wsgXYhzd6yYAw==";

    public static String generateModeratorToken(String userId, String userName, String KEY_ID) {
        long currentTime = System.currentTimeMillis();
        Date expirationTime = new Date(currentTime + 3600000); // Token valid for 1 hour

        Map<String, Object> context = new HashMap<>();
        context.put("user", userName);
        context.put("user_id", userId);

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", userId);
        claims.put("name", userName);
        claims.put("moderator", true); // This user is a moderator
        claims.put("context", context);
        claims.put("kid", KEY_ID);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuer("jitsi")
                .setSubject(userId)
                .setIssuedAt(new Date(currentTime))
                .setExpiration(expirationTime)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
}
