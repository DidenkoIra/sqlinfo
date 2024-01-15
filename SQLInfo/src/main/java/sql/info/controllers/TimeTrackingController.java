package sql.info.controllers;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sql.info.dao.TaskDAO;
import sql.info.dao.TimeTrackingDAO;
import sql.info.models.Task;
import sql.info.models.Peer;
import sql.info.models.TimeTracking;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("timetracking")
public class TimeTrackingController {
    private final TimeTrackingDAO timeTrackingDAO;
    private final String USER_DIRECTORY = System.getProperty("user.dir") + "/";
    private final Logger logger;

    @Autowired
    public TimeTrackingController(TimeTrackingDAO timeTrackingDAO, Logger logger) {
        this.timeTrackingDAO = timeTrackingDAO;
        this.logger = logger;
    }

    @GetMapping("")
    public String index(Model model) {
        model.addAttribute("timetracking", timeTrackingDAO.index());
        logger.info("/sqlinfo/timetracking");
        return "timetracking/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model) {
        model.addAttribute("timetracking", timeTrackingDAO.show(id));
        logger.info(String.format("/sqlinfo/timetracking/%d", id));
        return "timetracking/show";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model) {
        model.addAttribute("timetracking", timeTrackingDAO.show(id));
        logger.info(String.format("/sqlinfo/timetracking/%d/edit", id));
        return "timetracking/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("id") @Valid TimeTracking timeTracking,
                         BindingResult bindingResult,
                         @PathVariable("id") int id, Model model) {
        if(bindingResult.hasErrors()) {
            logger.warn(String.format("%d errors in /sqlinfo/timetracking/%d/edit", bindingResult.getErrorCount(), id));
            return "timetracking/edit";
        }
        try {
            timeTrackingDAO.update(id, timeTracking);
            logger.info(String.format("Update timetracking with id %d", id));
            return "redirect:/timetracking";
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            model.addAttribute("errorMessage", "Error: " + exception.getMessage());
            logger.warn(String.format("Error in /sqlinfo/timetracking/%d/edit", id), exception);
            return "timetracking/edit";
        }
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id, Model model) {
        try {
            timeTrackingDAO.delete(id);
            logger.info(String.format("Delete timetracking with id %d", id));
            return "redirect:/timetracking";
        } catch (Exception exception) {
            model.addAttribute("timetracking", timeTrackingDAO.show(id));
            model.addAttribute("errorMessage", "Error: "
                    + exception.getMessage());
            logger.warn(String.format("Error in delete timetracking with id %d", id), exception);
            return "timetracking/show";
        }
    }

    @GetMapping("/new")
    public String newTimeTracking(Model model) {
        model.addAttribute("timetracking", new TimeTracking());
        logger.info("/sqlinfo/timetracking/new");
        return "timetracking/new";
    }

    @PostMapping
    public String create(@ModelAttribute("timetracking") TimeTracking timeTracking, Model model) {
        try {
            timeTrackingDAO.save(timeTracking);
            logger.info(String.format("Save timetracking %s", timeTracking));
            return "redirect:/timetracking";
        } catch (Exception exception) {
            model.addAttribute("errorMessage", "Error: " + exception.getMessage());
            logger.warn(String.format("Error in create timetracking %s", timeTracking), exception);
            return "timetracking/edit";
        }
    }

    @GetMapping("/import")
    public String showImport() {
        logger.info("sqlinfo/timetracking/import");
        return "timetracking/import";
    }

    @GetMapping("/export")
    public void exportToCsv(HttpServletResponse response) {
        String path = USER_DIRECTORY+"timetracking.csv";
        try {
            logger.info("Start /sqlinfo/timetracking/export");
            response.setContentType("application/octet-stream");
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename = tasks.csv";
            response.setHeader(headerKey, headerValue);
            ServletOutputStream outputStream = response.getOutputStream();
            timeTrackingDAO.exportToCsv(outputStream);
            outputStream.close();
            logger.info("Finish /sqlinfo/timetracking/export");
        } catch (Exception exception) {
            logger.warn("Error in /sqlinfo/timetracking/export", exception);
        }
    }

    @PostMapping("/import")
    public String importFromCsv(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        try {
            logger.info("Start /sqlinfo/timetracking/import");
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                model.addAttribute("errorMessage", "Error: file error");
                return "timetracking/import";
            }
            String fileName = StringUtils.cleanPath(originalFilename);
            Path path = Paths.get(USER_DIRECTORY + fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            timeTrackingDAO.importFromCsv(path.toAbsolutePath().toString());
            logger.info("Finish /sqlinfo/timetracking/import");
            return "redirect:/timetracking";
        } catch (Exception exception) {
            model.addAttribute("errorMessage", "Error: " + exception.getMessage());
            logger.warn("Error in /sqlinfo/timetracking/import", exception);
            return "timetracking/import";
        }
    }

    @GetMapping("/*")
    public String handle() {
        return "404page";
    }
}
