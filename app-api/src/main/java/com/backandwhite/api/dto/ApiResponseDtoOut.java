package com.backandwhite.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * DTO genérico para todas las respuestas de API.
 * Proporciona una estructura consistente para éxito y error.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDtoOut<T> {

    @Schema(description = "Código de estado de la operación", example = "SUCCESS")
    private String code;

    @Schema(description = "Mensaje descriptivo de la operación", example = "Operación completada exitosamente")
    private String message;

    @Schema(description = "Datos de la respuesta")
    private T data;

    @Schema(description = "Detalles adicionales o errores específicos")
    private List<String> details;

    @Schema(description = "Timestamp de la respuesta")
    private ZonedDateTime timestamp;

    /**
     * Constructor para respuesta exitosa con datos.
     */
    public static <T> ApiResponseDtoOut<T> success(T data, String message) {
        return ApiResponseDtoOut.<T>builder()
                .code("SUCCESS")
                .message(message)
                .data(data)
                .timestamp(ZonedDateTime.now())
                .build();
    }

    /**
     * Constructor para respuesta exitosa sin datos.
     */
    public static <T> ApiResponseDtoOut<T> success(String message) {
        return ApiResponseDtoOut.<T>builder()
                .code("SUCCESS")
                .message(message)
                .timestamp(ZonedDateTime.now())
                .build();
    }

    /**
     * Constructor para respuesta de error.
     */
    public static <T> ApiResponseDtoOut<T> error(String code, String message, List<String> details) {
        return ApiResponseDtoOut.<T>builder()
                .code(code)
                .message(message)
                .details(details)
                .timestamp(ZonedDateTime.now())
                .build();
    }
}
