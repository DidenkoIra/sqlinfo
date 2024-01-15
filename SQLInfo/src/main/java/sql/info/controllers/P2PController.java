package sql.info.controllers;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sql.info.dao.P2PDAO;
import sql.info.models.P2P;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("p2p")
public class P2PController {
    private final P2PDAO p2pDAO;
    private final String USER_DIRECTORY = System.getProperty("user.dir") + "/";

    private final Logger logger;

    @Autowired
    public P2PController(P2PDAO p2pDAO, Logger logger) {
        this.p2pDAO = p2pDAO;
        this.logger = logger;
    }

    @GetMapping("")
    public String index(Model model) {
        model.addAttribute("p2ps", p2pDAO.index());
        logger.info("/sqlinfo/p2p");
        return "p2p/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model) {
        model.addAttribute("p2p", p2pDAO.show(id));
        logger.info(String.format("/sqlinfo/p2p/%d", id));
        return "p2p/show";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model) {
        model.addAttribute("p2p", p2pDAO.show(id));
        logger.info(String.format("/sqlinfo/p2p/%d/edit", id));
        return "p2p/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("p2p") P2P p2p,
                         @PathVariable("id") int id, Model model) {
        try {
            p2pDAO.update(id, p2p);
            logger.info(String.format("Update p2p with id %d", id));
            return "redirect:/p2p";
        } catch (Exception exception) {
            model.addAttribute("errorMessage", "Error: "
                    + exception.getMessage());
            logger.warn(String.format("Error in /sqlinfo/p2p/%d/edit", id), exception);
            return "p2p/edit";
        }
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id, Model model) {
        try {
            p2pDAO.delete(id);
            logger.info(String.format("Delete p2p with id %d", id));
            return "redirect:/p2p";
        } catch (Exception exception) {
            model.addAttribute("p2p", p2pDAO.show(id));
            model.addAttribute("errorMessage", "Error: "
                    + exception.getMessage());
            logger.warn(String.format("Error in delete p2p with id %d", id), exception);
            return "p2p/show";
        }
    }

    @GetMapping("/new")
    public String newP2P(Model model) {
        model.addAttribute("p2p", new P2P());
        logger.info("/sqlinfo/p2p/new");
        return "p2p/new";
    }

    @PostMapping
    public String create(@ModelAttribute("p2p") P2P p2p, Model model) {
        try {
            p2pDAO.save(p2p);
            logger.info(String.format("Save p2p %s", p2p));
            return "redirect:/p2p";
        } catch (Exception exception) {
            model.addAttribute("errorMessage", "Error: " + exception.getMessage());
            logger.warn(String.format("Error in create p2p %s", p2p), exception);
            return "p2p/edit";
        }
    }

    @GetMapping("/import")
    public String showImport() {
        logger.info("/sqlinfo/p2p/import");
        return "p2p/import";
    }

    @GetMapping("/export")
    public void exportToCsv(HttpServletResponse response) {
        try {
            logger.info("Start /sqlinfo/p2p/export");
            response.setContentType("application/octet-stream");
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename = p2p.csv";
            response.setHeader(headerKey, headerValue);
            ServletOutputStream outputStream = response.getOutputStream();
            p2pDAO.exportToCsv(outputStream);
            outputStream.close();
            logger.info("Finish /sqlinfo/p2p/export");
        } catch (Exception exception) {
            logger.warn("Error in /sqlinfo/p2p/export", exception);
        }
    }

    @PostMapping("/import")
    public String importFromCsv(@RequestParam("file") MultipartFile file, Model model) {
        try {
            logger.info("Start /sqlinfo/p2p/import");
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                model.addAttribute("errorMessage", "Error: file error");
                return "p2p/import";
            }
            String fileName = StringUtils.cleanPath(originalFilename);
            Path path = Paths.get(USER_DIRECTORY + fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            p2pDAO.importFromCsv(path.toAbsolutePath().toString());
            logger.info("Finish /sqlinfo/p2p/import");
            return "redirect:/p2p";
        } catch (Exception exception) {
            model.addAttribute("errorMessage", "Error: " + exception.getMessage());
            logger.warn("Error in /sqlinfo/p2p/import", exception);
            return "p2p/import";
        }
    }

    @GetMapping("/*")
    public String handle() {
        return "404page";
    }
}
