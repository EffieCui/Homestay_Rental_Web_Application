package com.project.staybooking.exception;

public class UserNotExistException extends RuntimeException { //可以compile，运行才报错
    public UserNotExistException(String message) {
        super(message); //调用父类的constructor
    }
}
