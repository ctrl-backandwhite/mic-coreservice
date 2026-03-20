package com.backandwhite.common.security.jwt;

import com.backandwhite.common.security.model.TokenClaims;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Utilidades JWT compartidas por todo el ecosistema de microservicios.
 * Clase de utilidades estática: no instanciable.
 *
 * <p>Utilizada por:
 * <ul>
 *   <li>mic-gatewayservice — validación de tokens en cada request</li>
 *   <li>mic-iamservice    — generación y validación de tokens</li>
 * </ul>
 */
@Log4j2
public final class JwtUtils {

    private JwtUtils() {
    }

    /**
     * Valida el token y extrae los claims si es válido.
     *
     * @param token  token JWT en formato compacto
     * @param secret clave secreta HMAC-SHA256 (mínimo 256 bits)
     * @return Optional con los claims si el token es válido, vacío si no lo es
     */
    public static Optional<TokenClaims> validateAndExtract(String token, String secret) {
        try {
            SecretKey key = buildKey(secret);
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            @SuppressWarnings("unchecked")
            List<String> roles = claims.get("roles", List.class);

            return Optional.of(new TokenClaims(
                    claims.getSubject(),
                    claims.get("email", String.class),
                    roles != null ? List.copyOf(roles) : List.of(),
                    claims.get("customerId", Long.class),
                    claims.get("employeeId", Long.class)
            ));
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("JWT validation failed: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Genera un token JWT firmado con HMAC-SHA256.
     *
     * @param subject      identificador del sujeto (email o username)
     * @param roles        lista de roles asignados
     * @param secret       clave secreta HMAC-SHA256
     * @param expirationMs tiempo de expiración en milisegundos
     * @param issuer       emisor del token
     * @return token JWT en formato compacto
     */
    public static String generateToken(String subject, List<String> roles, String secret,
                                       long expirationMs, String issuer) {
        SecretKey key = buildKey(secret);
        return Jwts.builder()
                .subject(subject)
                .issuer(issuer)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    /**
     * Genera un token JWT con claim de email adicional.
     *
     * @param subject      identificador del sujeto
     * @param email        email del usuario
     * @param roles        lista de roles asignados
     * @param customerId   id de cliente (nullable)
     * @param employeeId   id de empleado (nullable)
     * @param secret       clave secreta HMAC-SHA256
     * @param expirationMs tiempo de expiración en milisegundos
     * @param issuer       emisor del token
     * @return token JWT en formato compacto
     */
    public static String generateToken(String subject, String email, List<String> roles,
                                       Long customerId, Long employeeId, String secret,
                                       long expirationMs, String issuer) {
        SecretKey key = buildKey(secret);
        var builder = Jwts.builder()
                .subject(subject)
                .issuer(issuer)
                .claim("email", email)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs));

        if (customerId != null) {
            builder.claim("customerId", customerId);
        }
        if (employeeId != null) {
            builder.claim("employeeId", employeeId);
        }

        return builder.signWith(key).compact();
    }

    private static SecretKey buildKey(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
