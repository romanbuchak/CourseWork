package ua.lviv.iot.coursework.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ValidationError extends Error {
    private Error error;
    private List<ValidationFieldError> validationError;
}
