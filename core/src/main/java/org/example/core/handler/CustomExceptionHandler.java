package org.example.core.handler;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
    /**
     * Override method to handle validation errors in method arguments.
     *
     * @param ex      The MethodArgumentNotValidException instance that occurred.
     * @param headers The HttpHeaders associated with the response.
     * @param status  The HttpStatusCode representing the status of the response.
     * @param request The WebRequest associated with the request.
     * @return A ResponseEntity containing a HashMap of field names and error messages with HttpStatus.BAD_REQUEST.
     */
    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                               HttpHeaders headers,
                                                               HttpStatusCode status,
                                                               WebRequest request) {
        log.warn(ex.getMessage());
        HashMap<String, String> map = new HashMap<>();
        List<ObjectError> errors = ex.getBindingResult().getAllErrors();

        errors.forEach((error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            map.put(fieldName, message);
        }));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
    }

    /**
     * Method intercept exception {@link ConstraintViolationException}.
     *
     * @param ex          The CreationException instance that occurred.
     * @param webRequest The WebRequest associated with the request.
     * @return A ResponseEntity containing the ExceptionResponse with HttpStatus.BAD_REQUEST.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex,
                                                                           WebRequest webRequest) {
        log.warn(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
