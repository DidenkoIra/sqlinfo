package sql.info.controllers;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sql.info.dao.XPDAO;
import sql.info.models.XP;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("xp")
public class XPController {
    private final XPDAO xpDAO;
    private final String USER_DIRECTORY = System.getProperty("user.dir") + "/";
    private final Logger logger;

    @Autowired
    public XPController(XPDAO xpDAO, Logger logger) {
        this.xpDAO = xpDAO;
        this.logger = logger;
    }

    @GetMapping("")
    public String index(Model model) {
        model.addAttribute("xp", xpDAO.index());
        logger.info("/sqlinfo/xp");
        return "xp/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model) {
        model.addAttribute("xp", xpDAO.show(id));
        logger.info(String.format("/sqlinfo/xp/%d", id));
        return "xp/show";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model) {
        model.addAttribute("xp", xpDAO.show(id));
        logger.info(String.format("/sqlinfo/xp/%d/edit", id));
        return "xp/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("xp") XP xp,
                         @PathVariable("id") int id, Model model) {
        try {
            xpDAO.update(id, xp);
            logger.info(String.format("Update xp with id %d", id));
            return "redirect:/xp";
        } catch (Exception exception) {
            model.addAttribute("errorMessage", "Error: "
                    + exception.getMessage());
            logger.warn(String.format("Error in /sqlinfo/xp/%d/edit", id), exception);
            return "xp/edit";
        }
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id, Model model) {
        try {
            xpDAO.delete(id);
            logger.info(String.format("Delete xp with id %d", id));
            return "redirect:/xp";
        } catch (Exception exception) {
            model.addAttribute("xp", xpDAO.show(id));
            model.addAttribute("errorMessage", "Error: "
                    + exception.getMessage());
            logger.warn(String.format("Error in delete xp with id %d", id), exception);
            return "xp/show";
        }
    }

    @GetMapping("/new")
    public String newXP(Model model) {
        model.addAttribute("xp", new XP());
        return "xp/new";
    }

    @PostMapping
    public String create(@ModelAttribute("xp") XP xp, Model model) {
        try {
            xpDAO.save(xp);
            logger.info("/sqlinfo/xp/new");
            return "redirect:/xp";
        } catch (Exception exception) {
            model.addAttribute("errorMessage", "Error: " + exception.getMessage());
            logger.warn(String.format("Error in create xp %s", xp), exception);
            return "xp/edit";
        }
    }

    @GetMapping("/import")
    public String showImport() {
        logger.info("sqlinfo/xp/import");
        return "xp/import";
    }

    @GetMapping("/export")
    public void exportToCsv(HttpServletResponse response) {
        try {
            logger.info("Start /sqlinfo/xp/export");
            response.setContentType("application/octet-stream");
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename = xp.csv";
            response.setHeader(headerKey, headerValue);
            ServletOutputStream outputStream = response.getOutputStream();
            xpDAO.exportToCsv(outputStream);
            outputStream.close();
            logger.info("Finish /sqlinfo/xp/export");
        } catch (Exception exception) {
            logger.warn("Error in /sqlinfo/verter/export", exception);
        }
    }

    @PostMapping("/import")
    public String importFromCsv(@RequestParam("file") MultipartFile file, Model model) {
        try {
            logger.info("Start /sqlinfo/xp/import");
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                model.addAttribute("errorMessage", "Error: file error");
                return "xp/import";
            }
            String fileName = StringUtils.cleanPath(originalFilename);
            Path path = Paths.get(USER_DIRECTORY + fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            xpDAO.importFromCsv(path.toAbsolutePath().toString());
            logger.info("Finish /sqlinfo/xp/import");
            return "redirect:/xp";
        } catch (Exception exception) {
            model.addAttribute("errorMessage", "Error: " + exception.getMessage());
            logger.warn("Error in /sqlinfo/xp/import", exception);
            return "xp/import";
        }
    }
    @GetMapping("/*")
    public String handle() {
        return "404page";
    }
}
