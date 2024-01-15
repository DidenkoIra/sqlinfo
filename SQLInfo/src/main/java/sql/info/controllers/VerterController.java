package sql.info.controllers;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sql.info.dao.VerterDAO;
import sql.info.models.Verter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("verter")
public class VerterController {
    private final VerterDAO verterDAO;
    private final String USER_DIRECTORY = System.getProperty("user.dir") + "/";
    private final Logger logger;

    @Autowired
    public VerterController(VerterDAO verterDAO, Logger logger) {
        this.verterDAO = verterDAO;
        this.logger = logger;
    }

    @GetMapping("")
    public String index(Model model) {
        model.addAttribute("verter", verterDAO.index());
        logger.info("/sqlinfo/verter");
        return "verter/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model) {
        model.addAttribute("verter", verterDAO.show(id));
        logger.info(String.format("/sqlinfo/verter/%d", id));
        return "verter/show";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model) {
        model.addAttribute("verter", verterDAO.show(id));
        logger.info(String.format("/sqlinfo/verter/%d/edit", id));
        return "verter/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("verter") Verter verter,
                         @PathVariable("id") int id, Model model) {
        try {
            verterDAO.update(id, verter);
            logger.info(String.format("Update verter with id %d", id));
            return "redirect:/verter";
        } catch (Exception exception) {
            model.addAttribute("errorMessage", "Error: "
                    + exception.getMessage());
            logger.warn(String.format("Error in /sqlinfo/verter/%d/edit", id), exception);
            return "verter/edit";
        }
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id, Model model) {
        try {
            verterDAO.delete(id);
            logger.info(String.format("Delete verter with id %d", id));
            return "redirect:/verter";
        } catch (Exception exception) {
            model.addAttribute("verter", verterDAO.show(id));
            model.addAttribute("errorMessage", "Error: "
                    + exception.getMessage());
            logger.warn(String.format("Error in delete verter with id %d", id), exception);
            return "verter/show";
        }
    }

    @GetMapping("/new")
    public String newVerter(Model model) {
        model.addAttribute("verter", new Verter());
        logger.info("/sqlinfo/verter/new");
        return "verter/new";
    }

    @PostMapping
    public String create(@ModelAttribute("verter") Verter verter, Model model) {
        try {
            verterDAO.save(verter);
            logger.info(String.format("Save verter %s", verter));
            return "redirect:/verter";
        } catch (Exception exception) {
            model.addAttribute("errorMessage", "Error: " + exception.getMessage());
            logger.warn(String.format("Error in create verter %s", verter), exception);
            return "verter/edit";
        }
    }

    @GetMapping("/import")
    public String showImport() {
        logger.info("sqlinfo/verter/import");
        return "verter/import";
    }

    @GetMapping("/export")
    public void exportToCsv(HttpServletResponse response) {
        try {
            logger.info("Start /sqlinfo/verter/export");
            response.setContentType("application/octet-stream");
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename = verter.csv";
            response.setHeader(headerKey, headerValue);
            ServletOutputStream outputStream = response.getOutputStream();
            verterDAO.exportToCsv(outputStream);
            outputStream.close();
            logger.info("Finish /sqlinfo/verter/export");
        } catch (Exception exception) {
            logger.warn("Error in /sqlinfo/verter/export", exception);
        }
    }

    @PostMapping("/import")
    public String importFromCsv(@RequestParam("file") MultipartFile file, Model model) {
        try {
            logger.info("Start /sqlinfo/verter/import");
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                model.addAttribute("errorMessage", "Error: file error");
                return "verter/import";
            }
            String fileName = StringUtils.cleanPath(originalFilename);
            Path path = Paths.get(USER_DIRECTORY + fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            verterDAO.importFromCsv(path.toAbsolutePath().toString());
            logger.info("Finish /sqlinfo/verter/import");
            return "redirect:/verter";
        } catch (Exception exception) {
            model.addAttribute("errorMessage", "Error: " + exception.getMessage());
            logger.warn("Error in /sqlinfo/verter/import", exception);
            return "verter/import";
        }
    }
    @GetMapping("/*")
    public String handle() {
        return "404page";
    }
}
