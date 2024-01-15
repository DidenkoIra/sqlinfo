package sql.info.controllers;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sql.info.dao.CheckDAO;
import sql.info.models.Check;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("checks")
public class ChecksController {
    private final CheckDAO checkDAO;
    private final String USER_DIRECTORY = System.getProperty("user.dir") + "/";
    private final Logger logger;

    @Autowired
    public ChecksController(CheckDAO checkDAO, Logger logger) {
        this.checkDAO = checkDAO;
        this.logger = logger;
    }

    @GetMapping("")
    public String index(Model model) {
        model.addAttribute("checks", checkDAO.index());
        logger.info("/sqlinfo/checks");
        return "checks/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model) {
        model.addAttribute("check", checkDAO.show(id));
        logger.info(String.format("/sqlinfo/checks/%d", id));
        return "checks/show";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model) {
        model.addAttribute("check", checkDAO.show(id));
        logger.info(String.format("/sqlinfo/checks/%d/edit", id));
        return "checks/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("check") @Valid Check check,
                         BindingResult bindingResult,
                         @PathVariable("id") int id, Model model) {
        if(bindingResult.hasErrors()) {
            logger.warn(String.format("%d errors in /sqlinfo/checks/%d/edit", bindingResult.getErrorCount(), id));
            return "checks/edit";
        }

        try {
            checkDAO.update(id, check);
            logger.info(String.format("Update check with id %d", id));
            return "redirect:/checks";
        } catch (Exception exception) {
            model.addAttribute("errorMessage", "Error: "
                    + exception.getMessage());
            logger.warn(String.format("Error in /sqlinfo/checks/%d/edit", id), exception);
            return "checks/edit";
        }

    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id, Model model) {
        try {
            checkDAO.delete(id);
            logger.info(String.format("Delete check with id %d", id));
            return "redirect:/checks";
        } catch (Exception exception) {
            model.addAttribute("check", checkDAO.show(id));
            model.addAttribute("errorMessage", "Error: "
                    + exception.getMessage());
            logger.warn(String.format("Error in delete check with id %d", id), exception);
            return "checks/show";
        }
    }

    @GetMapping("/new")
    public String newCheck(Model model) {
        model.addAttribute("check", new Check());
        logger.info("/sqlinfo/checks/new");
        return "checks/new";
    }

    @PostMapping
    public String create(@ModelAttribute("check") Check check, Model model) {
        try {
            checkDAO.save(check);
            logger.info(String.format("Save check %s", check));
            return "redirect:/checks";
        } catch (Exception exception) {
            model.addAttribute("errorMessage", "Error: " + exception.getMessage());
            logger.warn(String.format("Error in create check %s", check), exception);
            return "checks/edit";
        }
    }

    @GetMapping("/import")
    public String showImport() {
        logger.info("/sqlinfo/checks/import");
        return "checks/import";
    }

    @GetMapping("/export")
    public void exportToCsv(HttpServletResponse response) {
        try {
            logger.info("Start /sqlinfo/checks/export");
            response.setContentType("application/octet-stream");
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename = checks.csv";
            response.setHeader(headerKey, headerValue);
            ServletOutputStream outputStream = response.getOutputStream();
            checkDAO.exportToCsv(outputStream);
            outputStream.close();
            logger.info("Finish /sqlinfo/checks/export");
        } catch (Exception exception) {
            logger.warn("Error in /sqlinfo/checks/export", exception);
        }
    }

    @PostMapping("/import")
    public String importFromCsv(@RequestParam("file") MultipartFile file, Model model) {
        try {
            logger.info("Start /sqlinfo/checks/import");
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                model.addAttribute("errorMessage", "Error: file error");
                return "checks/import";
            }
            String fileName = StringUtils.cleanPath(originalFilename);
            Path path = Paths.get(USER_DIRECTORY + fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            checkDAO.importFromCsv(path.toAbsolutePath().toString());
            logger.info("Finish /sqlinfo/checks/import");
            return "redirect:/checks";
        } catch (Exception exception) {
            model.addAttribute("errorMessage", "Error: " + exception.getMessage());
            logger.warn("Error in /sqlinfo/checks/import", exception);
            return "checks/import";
        }
    }

    @GetMapping("/*")
    public String handle() {
        return "404page";
    }
}
