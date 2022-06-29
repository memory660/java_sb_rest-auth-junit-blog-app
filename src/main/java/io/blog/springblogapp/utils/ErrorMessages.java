package io.blog.springblogapp.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessages {

    MISSING_REQUIRED_FIELD("Missing required field. Please check documentation for required fields"),
    RECORD_ALREADY_EXISTS("Record already exists"),
    INTERNAL_SERVER_ERROR("Internal server error"),
    NO_RECORD_FOUND_ID("Record with provided id is not found"),
    NO_RECORD_FOUND_USERNAME("Record with provided username not found"),
    AUTHENTICATION_FAILED("Wrong credentials"),
    COULD_NOT_UPDATE_RECORD("Could not update record"),
    COULD_NOT_DELETE_RECORD("Could not delete record"),
    ADDRESS_NOT_BELONG_USER("The address does not belong to the informed user"),
    INVALID_TOKEN_VALIDATION("The token provided is invalid or expired"),
    EMAIL_ALREADY_VALIDATED("Email address already has been validated"),
    NO_TOKEN_FOUND("Token provided is not found"),
    INVALID_PASSWORD_RESET("Password provided is not equals"),
    EMAIL_ADDRESS_NOT_VERIFIED("Email address could not be verified");

    private final String errorMessage;

}
