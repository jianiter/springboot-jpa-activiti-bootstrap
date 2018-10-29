package com.dd.activiti.admin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // 409
public class UserIdMismatchException extends RuntimeException {

    public UserIdMismatchException(){
        super();
    }

    public UserIdMismatchException(final String id){
        super("user id "+id+" mismatch.");
    }
}
