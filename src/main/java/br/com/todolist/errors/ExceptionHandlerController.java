package br.com.todolist.errors;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class ExceptionHandlerController {
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex){
        return ResponseEntity.badRequest().body(ex.getMostSpecificCause().getMessage());
    }
}
