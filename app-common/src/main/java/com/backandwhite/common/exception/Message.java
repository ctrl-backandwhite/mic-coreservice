package com.backandwhite.common.exception;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public enum Message {

    CREATE_SINGLE("CR001", "The %s has been created successfully."),
    UPDATE_SINGLE("UP001", "The %s has been updated successfully."),
    DELETE_SINGLE("DL002", "The %s has been deleted successfully."),
    CREATE_MULTIPLE("CR002", "The %s have been created successfully."),
    UPDATE_MULTIPLE("UP002", "The %s have been updated successfully."),
    DELETE_MULTIPLE("DL002", "The %s have been deleted successfully."),
    JSON_FORMAT_ERROR("JF001", "The JSON format is invalid."),
    VALIDATION_ERROR("VE001", "One or more validation errors occurred."),
    ENTITY_NOT_FOUND("NF001", "%s with id %s is not found."),
    INVALID_ARGUMENT("IA001", "Invalid or inactive permission"),
    UNASSOCIATED_ARGUMENT("IA002", "There is no %s associated record"),
    REQUIRED_ARGUMENT("IA003", "Required argument is missing: %s"),
    UNAUTHORIZED("AU001", "Unauthorized access."),
    FORBIDDEN("FB001", "Access forbidden."),
    DATABASE_ERROR("DB001", "Database error occurred."),
    EXTERNAL_SERVICE_ERROR("SV001", "External service error."),
    INTERNAL_SERVER_ERROR("IS001", "Internal server error.");

    private static final Logger log = LoggerFactory.getLogger(Message.class);

    private final String code;
    private final String detail;

    Message(String code, String message) {
        this.code = code;
        this.detail = message;
    }

    public String format(Object... args) {
        return String.format(this.detail, args);
    }

    public EntityNotFoundException toEntityNotFound(Object... args) {
        log.info("{} with id {} is not found.", args);
        return new EntityNotFoundException(this.code, format(args));
    }

    public ArgumentException toArgumentException(Object... args) {
        log.info("{} Invalid or inactive permission", args);
        return new ArgumentException(this.code, format(args));
    }
}
