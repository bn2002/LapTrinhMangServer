package org.example.exceptions;

import org.example.dtos.InputErrorDto;

import java.util.ArrayList;
import java.util.HashMap;

public class InputNotValidException extends RuntimeException{
    public ArrayList<InputErrorDto> errors;
    public InputNotValidException(String errorMessage, ArrayList<InputErrorDto> errors) {
        super(errorMessage);
        this.errors = errors;
    }

}
