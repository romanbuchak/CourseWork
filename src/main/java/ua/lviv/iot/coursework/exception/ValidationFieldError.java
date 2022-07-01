package ua.lviv.iot.coursework.exception;

import lombok.Data;

@Data
public class ValidationFieldError {
    private String field;
    private String error;
}