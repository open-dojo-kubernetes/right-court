package org.open.dojo.rightcourt.serve.controller;

import org.open.dojo.rightcourt.serve.exception.PointException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(PointException.class)
    @ResponseBody String handlePointException(PointException ex) {
        log.error("Point accounted", ex);
        return ex.toString();
    }
}
