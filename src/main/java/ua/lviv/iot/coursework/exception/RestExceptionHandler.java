package ua.lviv.iot.coursework.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFoundException(
            final EntityNotFoundException ex, final WebRequest request) {
        return handleInternal(ex, HttpStatus.NOT_FOUND, request);
    }


    private ResponseEntity<Object> handleInternal(Exception ex, HttpStatus status, WebRequest request) {
        return this.handleExceptionInternal(ex, null, copyHeaders(request), status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        List<ValidationFieldError> validationFieldErrorList = new ArrayList<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            ValidationFieldError fieldError = new ValidationFieldError();
            fieldError.setError(error.getDefaultMessage());
            fieldError.setField(error.getField());
            validationFieldErrorList.add(fieldError);
        }

        ValidationError validationError = new ValidationError();
        validationError.setValidationError(validationFieldErrorList);
        validationError.setTimestamp(LocalDateTime.now());
        validationError.setCode(status.value());
        validationError.setMessage(ex.getMessage());
        return new ResponseEntity<>(validationError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            Object body,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        Error error = new Error();
        error.setCode(status.value());
        error.setTimestamp(LocalDateTime.now());
        error.setMessage(ex.getMessage());

        return new ResponseEntity<>(error, new HttpHeaders(), status);
    }

    private HttpHeaders copyHeaders(final WebRequest request) {
        HttpHeaders headers = new HttpHeaders();
        Iterator<String> iterator = request.getHeaderNames();
        while (iterator.hasNext()) {
            String headerName = iterator.next();
            String headerValue = request.getHeader(headerName);
            headers.add(headerName, headerValue);
        }
        return headers;
    }
}
