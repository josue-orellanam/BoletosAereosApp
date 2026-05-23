package com.api.boletos.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // Generamos una llave secreta segura y moderna (Actualizado para JJWT 0.12+)
    private static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();

    // Tiempo de expiración del token: 1 hora (en milisegundos)
    private static final long EXPIRATION_TIME = 3600000;

    // Método principal que fabrica el token
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email) // Nuevo formato de la v0.12
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    // Método para leer el token y sacar el correo
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    // Método interno para descifrar el token (Actualizado para JJWT 0.12+)
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY) // Nueva forma de verificar la firma
                .build()
                .parseSignedClaims(token) // Reemplaza al viejo parseClaimsJws
                .getPayload(); // Reemplaza al viejo getBody
    }
}