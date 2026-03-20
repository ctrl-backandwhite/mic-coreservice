package com.backandwhite.common.security.jwt;

import com.backandwhite.common.security.model.TokenClaims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilsTest {

    private static final String SECRET = "test-secret-key-must-be-at-least-256-bits-long-for-hmac-sha256-algorithm";
    private static final String ISSUER = "test-issuer";
    private static final long EXPIRATION_MS = 3_600_000L;

    private String validToken;

    @BeforeEach
    void setUp() {
        validToken = JwtUtils.generateToken("user@test.com", List.of("CUSTOMER"), SECRET, EXPIRATION_MS, ISSUER);
    }

    @Test
    void validateAndExtract_withValidToken_returnsTokenClaims() {
        Optional<TokenClaims> result = JwtUtils.validateAndExtract(validToken, SECRET);

        assertThat(result).isPresent();
        assertThat(result.get().subject()).isEqualTo("user@test.com");
        assertThat(result.get().roles()).containsExactly("CUSTOMER");
    }

    @Test
    void validateAndExtract_withExpiredToken_returnsEmpty() {
        String expiredToken = JwtUtils.generateToken("user@test.com", List.of("CUSTOMER"), SECRET, -1000L, ISSUER);

        Optional<TokenClaims> result = JwtUtils.validateAndExtract(expiredToken, SECRET);

        assertThat(result).isEmpty();
    }

    @Test
    void validateAndExtract_withWrongSecret_returnsEmpty() {
        Optional<TokenClaims> result = JwtUtils.validateAndExtract(validToken, "wrong-secret-key-that-is-definitely-long-enough-for-hmac-sha-256");

        assertThat(result).isEmpty();
    }

    @Test
    void validateAndExtract_withMalformedToken_returnsEmpty() {
        Optional<TokenClaims> result = JwtUtils.validateAndExtract("not.a.valid.jwt.token", SECRET);

        assertThat(result).isEmpty();
    }

    @Test
    void validateAndExtract_withBlankToken_returnsEmpty() {
        Optional<TokenClaims> result = JwtUtils.validateAndExtract("", SECRET);

        assertThat(result).isEmpty();
    }

    @Test
    void validateAndExtract_withFullClaimsToken_returnsAllClaims() {
        String token = JwtUtils.generateToken(
                "emp@test.com", "emp@test.com", List.of("ADMIN", "BACKOFFICE"),
                null, 42L, SECRET, EXPIRATION_MS, ISSUER
        );

        Optional<TokenClaims> result = JwtUtils.validateAndExtract(token, SECRET);

        assertThat(result).isPresent();
        TokenClaims claims = result.get();
        assertThat(claims.subject()).isEqualTo("emp@test.com");
        assertThat(claims.email()).isEqualTo("emp@test.com");
        assertThat(claims.roles()).containsExactlyInAnyOrder("ADMIN", "BACKOFFICE");
        assertThat(claims.customerId()).isNull();
        assertThat(claims.employeeId()).isEqualTo(42L);
    }

    @Test
    void validateAndExtract_withMultipleRoles_extractsAllRoles() {
        String token = JwtUtils.generateToken(
                "admin@test.com", List.of("ADMIN", "BACKOFFICE", "CUSTOMER"), SECRET, EXPIRATION_MS, ISSUER
        );

        Optional<TokenClaims> result = JwtUtils.validateAndExtract(token, SECRET);

        assertThat(result).isPresent();
        assertThat(result.get().roles()).containsExactlyInAnyOrder("ADMIN", "BACKOFFICE", "CUSTOMER");
    }

    @Test
    void validateAndExtract_withCustomerToken_extractsCustomerId() {
        String token = JwtUtils.generateToken(
                "customer@test.com", "customer@test.com", List.of("CUSTOMER"),
                99L, null, SECRET, EXPIRATION_MS, ISSUER
        );

        Optional<TokenClaims> result = JwtUtils.validateAndExtract(token, SECRET);

        assertThat(result).isPresent();
        assertThat(result.get().customerId()).isEqualTo(99L);
        assertThat(result.get().employeeId()).isNull();
    }

    @Test
    void generateToken_producesNonBlankCompactJwt() {
        String token = JwtUtils.generateToken("subject", List.of("GUEST"), SECRET, EXPIRATION_MS, ISSUER);

        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3);
    }
}
