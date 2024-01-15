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

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

@Controller
@RequestMapping("operations")
public class OperationsController {
    private final OperationDAO operationDAO;
    private final Logger logger;
    @Autowired
    public OperationsController(OperationDAO operationDAO, Logger logger) {
        this.operationDAO = operationDAO;
        this.logger = logger;
    }
    @GetMapping("/transferred_points")
    public String transferredPoints(Model model) {
        try {
            Operation operation = new Operation();
            operationDAO.showTransferredPoints(operation);
            model.addAttribute("operation", operation);
            logger.info(String.format("/sqlinfo/operations/ %s", operation));
            return "operations/result";
        } catch (SQLException exception) {
            model.addAttribute("errorMessage", "Error: " + exception.getMessage());
            logger.warn("/sqlinfo/operations/transferred_points", exception);
            return "operations/result";
        }
    }

    @GetMapping("/checked_peers_with_XP")
    public String checkedPeersWithXP(Model model) {
        try {
            Operation operation = new Operation();
            operationDAO.showCheckedPeersWithXP(operation);
            model.addAttribute("operation", operation);
            logger.info(String.format("/sqlinfo/operations/ %s", operation));
            return "operations/result";
        } catch (SQLException exception) {
            model.addAttribute("errorMessage", "Error: " + exception.getMessage());
            logger.warn("/sqlinfo/operations/checked_peers_with_XP", exception);
            return "operations/result";
        }
    }

    @GetMapping("/peers_in_campus")
    public String peersInCampus(Model model) {
        Operation operation = new Operation("Peers in campus","showPeersInCampus");
        operation.getParametersList().add("date");
        operation.getParameters().put("date", null);
        model.addAttribute("operation", operation);
        logger.info(String.format("/sqlinfo/operations/parameters %s", operation));
        return "operations/parameters.html";
    }

    @PostMapping("/execute")
    public String execute(@ModelAttribute("operation") Operation operation, Model model) {
        try {
            operationDAO.execute(operation);
            logger.info(String.format("/sqlinfo/operations/execute %s", operation));
            return "operations/result";
        } catch (Exception exception) {
            model.addAttribute("errorMessage", "Error: " + exception.getMessage());
            logger.warn(String.format("/sqlinfo/operations/execute %s", operation), exception);
            return "operations/result";
        }
    }

    @GetMapping("/points_change")
    public String pointsChange(Model model) {
        try {
            Operation operation = new Operation();
            operationDAO.showPointsChange(operation);
            model.addAttribute("operation", operation);
            logger.info(String.format("/sqlinfo/operations/ %s", operation));
            return "operations/result";
        } catch (SQLException exception) {
            model.addAttribute("errorMessage", "Error: " + exception.getMessage());
            logger.warn("/sqlinfo/operations/points_change", exception);
            return "operations/result";
        }
    }

    @GetMapping("/points_change_v2")
    public String pointsChangeV2(Model model) {
        try {
            Operation operation = new Operation();
            operationDAO.showPointsChangeV2(operation);
            model.addAttribute("operation", operation);
            logger.info(String.format("/sqlinfo/operations/ %s", operation));
            return "operations/result";
        } catch (SQLException exception) {
            model.addAttribute("errorMessage", "Error: " + exception.getMessage());
            logger.warn("/sqlinfo/operations/points_change_v2", exception);
            return "operations/result";
        }
    }

    @GetMapping("/most_frequent_task_daily")
    public String MostFrequentTaskDaily(Model model) {
        try {
            Operation operation = new Operation();
            operationDAO.showMostFrequentTaskDaily(operation);
            model.addAttribute("operation", operation);
            logger.info(String.format("/sqlinfo/operations/ %s", operation));
            return "operations/result";
        } catch (SQLException exception) {
            model.addAttribute("errorMessage", "Error: " + exception.getMessage());
            logger.warn("/sqlinfo/operations/most_frequent_task_daily", exception);
            return "operations/result";
        }
    }

    @GetMapping("/complete_block")
    public String completeBlock(Model model) {
        Operation operation = new Operation("Complete block","showCompleteBlock");
        operation.getParametersList().add("block");
        operation.getParameters().put("block", null);
        model.addAttribute("operation", operation);
        logger.info(String.format("/sqlinfo/operations/parameters %s", operation));
        return "operations/parameters.html";
    }

    @GetMapping("/find_peer_for_check")
    public String findPeerForCheck(Model model) {
        try {
            Operation operation = new Operation();
            operationDAO.showFindPeerForCheck(operation);
            model.addAttribute("operation", operation);
            logger.info(String.format("/sqlinfo/operations/ %s", operation));
            return "operations/result";
        } catch (SQLException exception) {
            model.addAttribute("errorMessage", "Error: " + exception.getMessage());
            logger.warn("/sqlinfo/operations/find_peer_for_check", exception);
            return "operations/result";
        }
    }

    @GetMapping("/peers_by_groups")
    public String peersByGroups(Model model) {
        Operation operation = new Operation("Peers by groups","showPeersByGroups");
        operation.getParametersList().add("block1");
        operation.getParametersList().add("block2");
        operation.getParameters().put("block1", null);
        operation.getParameters().put("block2", null);
        model.addAttribute("operation", operation);
        logger.info(String.format("/sqlinfo/operations/parameters %s", operation));
        return "operations/parameters.html";
    }

    @GetMapping("/peers_with_birthday_check")
    public String peersWithBirthdayCheck(Model model) {
        try {
            Operation operation = new Operation();
            operationDAO.showPeersWithBirthdayCheck(operation);
            model.addAttribute("operation", operation);
            logger.info(String.format("/sqlinfo/operations/ %s", operation));
            return "operations/result";
        } catch (SQLException exception) {
            model.addAttribute("errorMessage", "Error: " + exception.getMessage());
            logger.warn("/sqlinfo/operations/peers_with_birthday_check", exception);
            return "operations/result";
        }
    }

    @GetMapping("/given_and_not_given_tasks")
    public String givenAndNotGivenTasks(Model model) {
        Operation operation = new Operation("Given and not given tasks","showGivenAndNotGivenTasks");
        operation.getParametersList().add("task1");
        operation.getParametersList().add("task2");
        operation.getParametersList().add("task3");
        operation.getParameters().put("task1", null);
        operation.getParameters().put("task2", null);
        operation.getParameters().put("task3", null);
        model.addAttribute("operation", operation);
        logger.info(String.format("/sqlinfo/operations/parameters %s", operation));
        return "operations/parameters.html";
    }

    @GetMapping("/count_of_previous_tasks")
    public String countOfPreviousTasks(Model model) {
        try {
            Operation operation = new Operation();
            operationDAO.showCountOfPreviousTasks(operation);
            model.addAttribute("operation", operation);
            logger.info(String.format("/sqlinfo/operations/ %s", operation));
            return "operations/result";
        } catch (SQLException exception) {
            model.addAttribute("errorMessage", "Error: " + exception.getMessage());
            logger.warn("/sqlinfo/operations/count_of_previous_tasks", exception);
            return "operations/result";
        }
    }

    @GetMapping("/find_lucky_days_for_checks")
    public String findLuckyDaysForChecks(Model model) {
        Operation operation = new Operation("Find lucky days for checks","showFindLuckyDaysForChecks");
        operation.getParametersList().add("N");
        operation.getParameters().put("N", null);
        model.addAttribute("operation", operation);
        logger.info(String.format("/sqlinfo/operations/parameters %s", operation));
        return "operations/parameters.html";
    }

    @GetMapping("/get_peer_with_max_xp")
    public String getPeerWithMaxXP(Model model) {
        try {
            Operation operation = new Operation();
            operationDAO.showGetPeerWithMaxXP(operation);
            model.addAttribute("operation", operation);
            logger.info(String.format("/sqlinfo/operations/ %s", operation));
            return "operations/result";
        } catch (SQLException exception) {
            model.addAttribute("errorMessage", "Error: " + exception.getMessage());
            logger.warn("/sqlinfo/operations/get_peer_with_max_xp", exception);
            return "operations/result";
        }
    }

    @GetMapping("/get_peers_max_time_spent")
    public String getPeersMaxTimeSpent(Model model) {
        Operation operation = new Operation("Get peers max time spent","showGetPeersMaxTimeSpent");
        operation.getParametersList().add("Time");
        operation.getParametersList().add("N");
        operation.getParameters().put("Time", null);
        operation.getParameters().put("N", null);
        model.addAttribute("operation", operation);
        logger.info(String.format("/sqlinfo/operations/parameters %s", operation));
        return "operations/parameters.html";
    }

    @GetMapping("/get_peers_left_campus")
    public String getPeersLeftCampus(Model model) {
        Operation operation = new Operation("Get peers who left the campus","showGetPeersLeftCampus");
        operation.getParametersList().add("N");
        operation.getParametersList().add("M");
        operation.getParameters().put("N", null);
        operation.getParameters().put("M", null);
        model.addAttribute("operation", operation);
        logger.info(String.format("/sqlinfo/operations/parameters %s", operation));
        return "operations/parameters.html";
    }

    @GetMapping("/percentage_of_early_entries")
    public String percentageOfEarlyEntries(Model model) {
        try {
            Operation operation = new Operation();
            operationDAO.showPercentageOfEarlyEntries(operation);
            model.addAttribute("operation", operation);
            logger.info(String.format("/sqlinfo/operations/ %s", operation));
            return "operations/result";
        } catch (SQLException exception) {
            model.addAttribute("errorMessage", "Error: " + exception.getMessage());
            logger.warn("/sqlinfo/operations/percentage_of_early_entries", exception);
            return "operations/result";
        }
    }

    @PostMapping("/export")
    public void exportToCSV(@ModelAttribute("operation") Operation operation, HttpServletResponse response) {
        try {
            logger.info(String.format("Start export operation %s", operation));
            response.setContentType("application/octet-stream");
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename = result.csv";
            response.setHeader(headerKey, headerValue);
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.print(operationDAO.exportToCSV(operation));
            outputStream.close();
            logger.info(String.format("Finish export operation %s", operation));
        } catch (Exception exception) {
            logger.warn(String.format("Error in export operation %s", operation), exception);
        }
    }

    @GetMapping("/*")
    public String handle() {
        return "404page";
    }
}
