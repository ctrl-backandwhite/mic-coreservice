package com.backandwhite.common.utils;

import com.backandwhite.common.exception.ArgumentException;
import com.backandwhite.common.exception.Message;

import java.util.Objects;

/**
 * Utilidades para validación de argumentos.
 * Proporciona métodos reutilizables para validar condiciones comunes.
 */
public class ValidationUtils {

    private ValidationUtils() {
        // Clase utilitaria, no se debe instanciar
    }

    /**
     * Valida que un objeto no sea nulo.
     *
     * @param object  El objeto a validar
     * @param message El mensaje de error
     */
    public static void notNull(Object object, String message) {
        if (Objects.isNull(object)) {
            throw new ArgumentException(Message.REQUIRED_ARGUMENT.getCode(), message);
        }
    }

    /**
     * Valida que una cadena no sea nula o vacía.
     *
     * @param value     La cadena a validar
     * @param fieldName El nombre del campo
     */
    public static void notBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ArgumentException(
                    Message.REQUIRED_ARGUMENT.getCode(),
                    fieldName + " no puede estar vacío");
        }
    }

    /**
     * Valida que un ID sea válido (mayor a 0).
     *
     * @param id        El ID a validar
     * @param fieldName El nombre del campo
     */
    public static void validId(Long id, String fieldName) {
        if (id == null || id <= 0) {
            throw new ArgumentException(
                    Message.INVALID_ARGUMENT.getCode(),
                    fieldName + " debe ser un ID válido (mayor a 0)");
        }
    }

    /**
     * Valida que una condición sea verdadera.
     *
     * @param condition La condición a validar
     * @param code      El código de error
     * @param message   El mensaje de error
     */
    public static void assertTrue(boolean condition, String code, String message) {
        if (!condition) {
            throw new ArgumentException(code, message);
        }
    }
}
