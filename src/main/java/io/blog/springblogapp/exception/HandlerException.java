package io.blog.springblogapp.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

@AllArgsConstructor
@RestControllerAdvice
public class HandlerException {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handlerUserNotFoundException(UserNotFoundException exception) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        ErrorMessage errorMessage = ErrorMessage
                .builder()
                .timestamp(OffsetDateTime.now())
                .status(status.value())
                .message(exception.getMessage())
                .build();

        return ResponseEntity.status(status).body(errorMessage);
    }

    @ExceptionHandler(AddressNotFoundException.class)
    public ResponseEntity<Object> handlerAddressNotFoundException(AddressNotFoundException exception) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        ErrorMessage errorMessage = ErrorMessage
                .builder()
                .timestamp(OffsetDateTime.now())
                .status(status.value())
                .message(exception.getMessage())
                .build();

        return ResponseEntity.status(status).body(errorMessage);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handlerBusinessException(BusinessException exception) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        ErrorMessage errorMessage = ErrorMessage
                .builder()
                .timestamp(OffsetDateTime.now())
                .status(status.value())
                .message(exception.getMessage())
                .build();

        return ResponseEntity.status(status).body(errorMessage);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Object> handlerAuthException(AuthException exception) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        ErrorMessage errorMessage = ErrorMessage
                .builder()
                .timestamp(OffsetDateTime.now())
                .status(status.value())
                .message(exception.getMessage())
                .build();

        return ResponseEntity.status(status).body(errorMessage);
    }

}
