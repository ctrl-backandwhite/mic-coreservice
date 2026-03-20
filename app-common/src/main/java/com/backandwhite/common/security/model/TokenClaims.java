package com.backandwhite.common.security.model;

import java.util.List;

/**
 * Modelo inmutable con los claims extraídos de un token JWT.
 * Propagado por el Gateway a los microservicios downstream via cabeceras HTTP.
 */
public record TokenClaims(
        String subject,
        String email,
        List<String> roles,
        Long customerId,
        Long employeeId
) {}
