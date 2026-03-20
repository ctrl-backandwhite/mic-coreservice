package com.backandwhite.api.exception;

import com.backandwhite.api.dto.ApiResponseDtoOut;
import com.backandwhite.common.exception.ArgumentException;
import com.backandwhite.common.exception.BaseException;
import com.backandwhite.common.exception.DomainException;
import com.backandwhite.common.exception.EntityNotFoundException;
import com.backandwhite.common.exception.Message;
import jakarta.validation.UnexpectedTypeException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para todos los microservicios.
 * Proporciona respuestas consistentes en formato JSON para todos los errores.
 */
@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

        /**
         * Maneja excepciones de entidad no encontrada.
         */
        @ExceptionHandler(EntityNotFoundException.class)
        public ResponseEntity<ApiResponseDtoOut<?>> handleEntityNotFound(
                        EntityNotFoundException ex,
                        WebRequest request) {
                log.warn("Entity not found: {}", ex.getMessage(), ex);

                ApiResponseDtoOut<?> response = ApiResponseDtoOut.builder()
                                .code(ex.getCode())
                                .message(ex.getMessage())
                                .details(ex.getDetail())
                                .timestamp(ZonedDateTime.now())
                                .build();

                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        /**
         * Maneja excepciones de argumentos inválidos.
         */
        @ExceptionHandler(ArgumentException.class)
        public ResponseEntity<ApiResponseDtoOut<?>> handleArgumentException(
                        ArgumentException ex,
                        WebRequest request) {
                log.warn("Argument exception: {} - Code: {}", ex.getMessage(), ex.getCode(), ex);

                ApiResponseDtoOut<?> response = ApiResponseDtoOut.builder()
                                .code(ex.getCode())
                                .message(ex.getMessage())
                                .details(ex.getDetail())
                                .timestamp(ZonedDateTime.now())
                                .build();

                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        /**
         * Maneja excepciones de dominio.
         */
        @ExceptionHandler(DomainException.class)
        public ResponseEntity<ApiResponseDtoOut<?>> handleDomainException(
                        DomainException ex,
                        WebRequest request) {
                log.warn("Domain exception: {} - Code: {}", ex.getMessage(), ex.getCode(), ex);

                ApiResponseDtoOut<?> response = ApiResponseDtoOut.builder()
                                .code(ex.getCode())
                                .message(ex.getMessage())
                                .details(ex.getDetail())
                                .timestamp(ZonedDateTime.now())
                                .build();

                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        /**
         * Maneja excepciones base personalizadas.
         */
        @ExceptionHandler(BaseException.class)
        public ResponseEntity<ApiResponseDtoOut<?>> handleBaseException(
                        BaseException ex,
                        WebRequest request) {
                log.warn("Base exception: {} - Code: {}", ex.getMessage(), ex.getCode(), ex);

                ApiResponseDtoOut<?> response = ApiResponseDtoOut.builder()
                                .code(ex.getCode())
                                .message(ex.getMessage())
                                .details(ex.getDetail())
                                .timestamp(ZonedDateTime.now())
                                .build();

                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        /**
         * Maneja excepciones de validación de argumentos de método.
         */
        @Override
        protected ResponseEntity<Object> handleMethodArgumentNotValid(
                        MethodArgumentNotValidException ex,
                        HttpHeaders headers,
                        HttpStatusCode status,
                        WebRequest request) {

                log.warn("Validation error: {}", ex.getMessage());

                List<String> details = ex.getBindingResult()
                                .getAllErrors()
                                .stream()
                                .map(ObjectError::getDefaultMessage)
                                .collect(Collectors.toList());

                ApiResponseDtoOut<?> response = ApiResponseDtoOut.builder()
                                .code("VE001")
                                .message("Validation error")
                                .details(details)
                                .timestamp(ZonedDateTime.now())
                                .build();

                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        /**
         * Maneja JSON con formato inválido.
         * Override del método protegido de ResponseEntityExceptionHandler para evitar ambigüedad.
         */
        @Override
        protected ResponseEntity<Object> handleHttpMessageNotReadable(
                        HttpMessageNotReadableException ex,
                        HttpHeaders headers,
                        HttpStatusCode status,
                        WebRequest request) {
                log.warn("JSON format error: {}", ex.getMessage());

                ApiResponseDtoOut<?> response = ApiResponseDtoOut.builder()
                                .code(Message.JSON_FORMAT_ERROR.getCode())
                                .message(Message.JSON_FORMAT_ERROR.getDetail())
                                .timestamp(ZonedDateTime.now())
                                .build();

                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        /**
         * Maneja anotaciones de validación usadas sobre tipos incompatibles.
         */
        @ExceptionHandler(UnexpectedTypeException.class)
        public ResponseEntity<ApiResponseDtoOut<?>> handleUnexpectedType(
                        UnexpectedTypeException ex,
                        WebRequest request) {
                log.warn("Unexpected type exception: {}", ex.getMessage());

                ApiResponseDtoOut<?> response = ApiResponseDtoOut.builder()
                                .code(Message.VALIDATION_ERROR.getCode())
                                .message(Message.VALIDATION_ERROR.getDetail())
                                .details(Collections.singletonList("Annotation used on an incompatible type"))
                                .timestamp(ZonedDateTime.now())
                                .build();

                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        /**
         * Maneja todas las excepciones no manejadas.
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponseDtoOut<?>> handleGlobalException(
                        Exception ex,
                        WebRequest request) {
                log.error("Unhandled exception: {}", ex.getMessage(), ex);

                ApiResponseDtoOut<?> response = ApiResponseDtoOut.builder()
                                .code("IS001")
                                .message("Internal server error")
                                .details(Collections.singletonList(ex.getMessage()))
                                .timestamp(ZonedDateTime.now())
                                .build();

                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
}
