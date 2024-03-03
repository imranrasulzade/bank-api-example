package com.bob.bankapispringapp.exception;

public class IsNotActiveException extends RuntimeException{
    public IsNotActiveException(String msg){
        super(msg);
    }
}
