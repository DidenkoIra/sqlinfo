package sql.info.controllers;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

@Controller
public class MainController {
    private final Logger logger;
    @Autowired
    public MainController(Logger logger) {
        this.logger = logger;
    }
    @GetMapping("/")
    public String index() {
        logger.info("/sqlinfo");
        return "index";
    }

    @GetMapping("sqldata")
    public String data() {
        logger.info("/sqlinfo/sqldata");
        return "data";
    }

    @GetMapping("operations")
    public String operations() {
        logger.info("/sqlinfo/operations");
        return "operations";
    }
    @GetMapping("/*")
    public String handle() {
        return "404page";
    }
}
