package sql.info.controllers;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import sql.info.dao.OperationDAO;
import sql.info.models.Operation;

@Controller
@RequestMapping("userquery")
public class UserQueryController {

    private final OperationDAO operationDAO;
    private final Logger logger;

    @Autowired
    public UserQueryController(OperationDAO operationDAO, Logger logger) {
        this.operationDAO = operationDAO;
        this.logger = logger;
    }
    @GetMapping("")
    public String index(Model model) {
        model.addAttribute("userQuery", new Operation("User query"));
        logger.info("/sqlinfo/userquery/");
        return "userquery/index";
    }

    @PostMapping
    public String parseParameters(@ModelAttribute("userQuery") Operation userQuery, Model model) {
        userQuery.parseParameters();
        if (userQuery.getParameters().isEmpty()) {
            try {
                operationDAO.executeQuery(userQuery);
                logger.info(String.format("/sqlinfo/userquery/result for %s", userQuery));
                return "userquery/result";
            } catch (Exception exception) {
                model.addAttribute("errorMessage", "Error: " + exception.getMessage());
                logger.warn(String.format("Error /sqlinfo/userquery/result for %s", userQuery), exception);
                return "userquery/index";
            }
        } else {
            logger.info(String.format("/sqlinfo/userquery/ Parameters for %s", userQuery));
            return "userquery/index_with_parameters.html";
        }
    }

    @PostMapping("/execute")
    public String execute(@ModelAttribute("userQuery") Operation userQuery, Model model) {
        try {
            userQuery.parseParameters();
            operationDAO.executeQuery(userQuery);
            logger.info(String.format("/sqlinfo/userquery/result for %s", userQuery));
            return "userquery/result";
        } catch (Exception exception) {
            model.addAttribute("errorMessage", "Error: " + exception.getMessage());
            logger.warn(String.format("Error /sqlinfo/userquery/execute for %s", userQuery), exception);
            return "userquery/result";
        }
    }

    @GetMapping("/*")
    public String handle() {
        return "404page";
    }
}
