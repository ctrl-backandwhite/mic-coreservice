package com.backandwhite.common.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propiedades de configuración JWT.
 * Habilitar en cada servicio que lo necesite con @EnableConfigurationProperties(JwtProperties.class).
 *
 * <pre>
 * app:
 *   jwt:
 *     secret: your-256-bit-secret
 *     expiration-ms: 3600000
 *     issuer: mic-iamservice
 * </pre>
 */
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        String secret,
        long expirationMs,
        String issuer
) {}
