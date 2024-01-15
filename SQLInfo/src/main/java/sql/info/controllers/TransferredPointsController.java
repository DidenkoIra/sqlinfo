package sql.info.controllers;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sql.info.dao.TransferredPointsDAO;
import sql.info.models.TransferredPoints;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("transferredpoints")
public class TransferredPointsController {
    private final TransferredPointsDAO transferredPointsDAO;
    private final String USER_DIRECTORY = System.getProperty("user.dir") + "/";
    private final Logger logger;

    @Autowired
    public TransferredPointsController(TransferredPointsDAO transferredPointsDAO, Logger logger) {
        this.transferredPointsDAO = transferredPointsDAO;
        this.logger = logger;
    }

    @GetMapping("")
    public String index(Model model) {
        model.addAttribute("transferredpoints", transferredPointsDAO.index());
        logger.info("/sqlinfo/transferredpoints");
        return "transferredpoints/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model) {
        model.addAttribute("transferredpoints", transferredPointsDAO.show(id));
        logger.info(String.format("/sqlinfo/transferredpoints/%d", id));
        return "transferredpoints/show";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model) {
        model.addAttribute("transferredpoints", transferredPointsDAO.show(id));
        logger.info(String.format("/sqlinfo/transferredpoints/%d/edit", id));
        return "transferredpoints/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("id") @Valid TransferredPoints transferredPoints,
                         BindingResult bindingResult,
                         @PathVariable("id") int id, Model model) {
        if(bindingResult.hasErrors()) {
            logger.warn(String.format("%d errors in /sqlinfo/transferredpoints/%d/edit", bindingResult.getErrorCount(), id));
            return "transferredpoints/edit";
        }
        try {
            transferredPointsDAO.update(id, transferredPoints);
            logger.info(String.format("Update transferredpoints with id %d", id));
            return "redirect:/transferredpoints";
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            model.addAttribute("errorMessage", "Error: " + exception.getMessage());
            logger.warn(String.format("Error in /sqlinfo/transferredpoints/%d/edit", id), exception);
            return "transferredpoints/edit";
        }
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id, Model model) {
        try {
            transferredPointsDAO.delete(id);
            logger.info(String.format("Delete transferredpoints with id %d", id));
            return "redirect:/transferredpoints";
        } catch (Exception exception) {
            model.addAttribute("transferredpoints", transferredPointsDAO.show(id));
            model.addAttribute("errorMessage", "Error: "
                    + exception.getMessage());
            logger.warn(String.format("Error in delete transferredpoints with id %d", id), exception);
            return "transferredpoints/show";
        }

    }

    @GetMapping("/new")
    public String newTransferredPoints(Model model) {
        model.addAttribute("transferredpoints", new TransferredPoints());
        logger.info("/sqlinfo/transferredpoints/new");
        return "transferredpoints/new";
    }

    @PostMapping
    public String create(@ModelAttribute("transferredpoints") TransferredPoints transferredPoints, Model model) {
        try {
            transferredPointsDAO.save(transferredPoints);
            logger.info(String.format("Save transferredpoints %s", transferredPoints));
            return "redirect:/transferredpoints";
        } catch (Exception exception) {
            model.addAttribute("errorMessage", "Error: " + exception.getMessage());
            logger.warn(String.format("Error in create transferredpoints %s", transferredPoints), exception);
            return "transferredpoints/edit";
        }
    }

    @GetMapping("/import")
    public String showImport() {
        logger.info("sqlinfo/transferredpoints/import");
        return "transferredpoints/import";
    }

    @GetMapping("/export")
    public void exportToCsv(HttpServletResponse response) {
        String path = USER_DIRECTORY+"transferredpoints.csv";
        try {
            logger.info("Start /sqlinfo/transferredpoints/export");
            response.setContentType("application/octet-stream");
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename = tasks.csv";
            response.setHeader(headerKey, headerValue);
            ServletOutputStream outputStream = response.getOutputStream();
            transferredPointsDAO.exportToCsv(outputStream);
            outputStream.close();
            logger.info("Finish /sqlinfo/transferredpoints/export");
        } catch (Exception exception) {
            logger.warn("Error in /sqlinfo/transferredpoints/export", exception);
        }
    }

    @PostMapping("/import")
    public String importFromCsv(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        try {
            logger.info("Start /sqlinfo/transferredpoints/import");
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                model.addAttribute("errorMessage", "Error: file error");
                return "transferredpoints/import";
            }
            String fileName = StringUtils.cleanPath(originalFilename);
            Path path = Paths.get(USER_DIRECTORY + fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            transferredPointsDAO.importFromCsv(path.toAbsolutePath().toString());
            logger.info("Finish /sqlinfo/transferredpoints/import");
            return "redirect:/transferredpoints";
        } catch (Exception exception) {
            model.addAttribute("errorMessage", "Error: " + exception.getMessage());
            logger.warn("Error in /sqlinfo/transferredpoints/import", exception);
            return "transferredpoints/import";
        }
    }
    @GetMapping("/*")
    public String handle() {
        return "404page";
    }
}
