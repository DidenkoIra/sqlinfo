package sql.info.controllers;

import org.apache.logging.log4j.Logger;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.sql.SQLException;

@ControllerAdvice
public class GlobalExceptionHandler {
    private final Logger logger;

    @Autowired
    public GlobalExceptionHandler(Logger logger) {
        this.logger = logger;
    }
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(SQLException.class)
    public void handleSQLException() {
        logger.error("SQLException");
    }
}
