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
import sql.info.models.Task;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("tasks")
public class TasksController {
    private final TaskDAO taskDAO;
    private final String USER_DIRECTORY = System.getProperty("user.dir") + "/";

    private final Logger logger;

    @Autowired
    public TasksController(TaskDAO taskDAO, Logger logger) {
        this.taskDAO = taskDAO;
        this.logger = logger;
    }

    @GetMapping("")
    public String index(Model model) {
        model.addAttribute("tasks", taskDAO.index());
        logger.info("/sqlinfo/tasks");
        return "tasks/index";
    }

    @GetMapping("/{title}")
    public String show(@PathVariable("title") String title, Model model) {
        model.addAttribute("task", taskDAO.show(title));
        logger.info(String.format("/sqlinfo/tasks/%s", title));
        return "tasks/show";
    }

    @GetMapping("/{title}/edit")
    public String edit(@PathVariable("title") String title, Model model) {
        model.addAttribute("task", taskDAO.show(title));
        logger.info(String.format("/sqlinfo/tasks/%s/edit", title));
        return "tasks/edit";
    }

    @PatchMapping("/{title}")
    public String update(@ModelAttribute("task") @Valid Task task,
                         BindingResult bindingResult,
                         @PathVariable("title") String title, Model model) {
        if(bindingResult.hasErrors()) {
            logger.warn(String.format("%d errors in /sqlinfo/tasks/%s/edit", bindingResult.getErrorCount(), title));
            return "tasks/edit";
        }
        try {
            taskDAO.update(title, task);
            logger.info(String.format("Update task with title %s", title));
            return "redirect:/tasks";
        } catch (Exception exception) {
            model.addAttribute("errorMessage", "Error: " + exception.getMessage());
            logger.warn(String.format("Error in /sqlinfo/tasks/%s/edit", title), exception);
            return "tasks/edit";
        }
    }

    @DeleteMapping("/{title}")
    public String delete(@PathVariable("title") String title, Model model) {
        try {
            taskDAO.delete(title);
            logger.info(String.format("Delete task with title %s", title));
            return "redirect:/tasks";
        } catch (Exception exception) {
            model.addAttribute("task", taskDAO.show(title));
            model.addAttribute("errorMessage", "Error: "
                    + exception.getMessage());
            logger.warn(String.format("Error in delete task with title %s", title), exception);
            return "tasks/show";
        }

    }

    @GetMapping("/new")
    public String newTask(Model model) {
        model.addAttribute("task", new Task());
        logger.info("/sqlinfo/tasks/new");
        return "tasks/new";
    }

    @PostMapping
    public String create(@ModelAttribute("task") Task task, Model model) {
        try {
            taskDAO.save(task);
            logger.info(String.format("Save check %s", task));
            return "redirect:/tasks";
        } catch (Exception exception) {
            model.addAttribute("errorMessage", "Error: " + exception.getMessage());
            logger.warn(String.format("Error in create task %s", task), exception);
            return "tasks/edit";
        }
    }

    @GetMapping("/import")
    public String showImport() {
        logger.info("/sqlinfo/tasks/import");
        return "tasks/import";
    }

    @GetMapping("/export")
    public void exportToCsv(HttpServletResponse response) {
        String path = USER_DIRECTORY+"tasks.csv";
        try {
            logger.info("Start /sqlinfo/tasks/export");
            response.setContentType("application/octet-stream");
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename = tasks.csv";
            response.setHeader(headerKey, headerValue);
            ServletOutputStream outputStream = response.getOutputStream();
            taskDAO.exportToCsv(outputStream);
            outputStream.close();
            logger.info("Finish /sqlinfo/tasks/export");
        } catch (Exception exception) {
            logger.warn("Error in /sqlinfo/tasks/export", exception);
        }
    }

    @PostMapping("/import")
    public String importFromCsv(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        try {
            logger.info("Start /sqlinfo/tasks/import");
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                model.addAttribute("errorMessage", "Error: file error");
                return "tasks/import";
            }
            String fileName = StringUtils.cleanPath(originalFilename);
            Path path = Paths.get(USER_DIRECTORY + fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            taskDAO.importFromCsv(path.toAbsolutePath().toString());
            logger.info("Finish /sqlinfo/tasks/import");
            return "redirect:/tasks";
        } catch (IOException exception) {
            model.addAttribute("errorMessage", "Error: " + exception.getMessage());
            logger.warn("Error in /sqlinfo/tasks/import", exception);
            return "tasks/import";
        }

    }

    @GetMapping("/*")
    public String handle() {
        return "404page";
    }
}
