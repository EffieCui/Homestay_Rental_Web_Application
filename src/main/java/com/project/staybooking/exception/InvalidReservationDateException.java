package com.project.staybooking.exception;

// reservation 日期必须在今天之后
public class InvalidReservationDateException extends RuntimeException {
    public InvalidReservationDateException(String message) {
        super(message);
    }
}
