package com.bob.bankapispringapp.exception.handler;

import com.bob.bankapispringapp.exception.EntityNotFoundException;
import com.bob.bankapispringapp.model.ExceptionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDTO handleNotFound(EntityNotFoundException entityNotFoundException){
        log.error("ActionLog.error not found: {} ", entityNotFoundException.getMessage());
        return new ExceptionDTO(HttpStatus.NOT_FOUND.value(), entityNotFoundException.getMessage());
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionDTO handleGlobal(Exception e){
        log.error("ActionLog.error not found: {} ", e.getMessage());
        return new ExceptionDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }






}
