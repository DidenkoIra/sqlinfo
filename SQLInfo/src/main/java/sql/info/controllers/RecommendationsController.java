package sql.info.controllers;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sql.info.dao.RecommendationDAO;
import sql.info.models.Recommendation;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("recommendations")
public class RecommendationsController {
    private final RecommendationDAO recommendationDAO;
    private final String USER_DIRECTORY = System.getProperty("user.dir") + "/";
    private final Logger logger;

    @Autowired
    public RecommendationsController(RecommendationDAO recommendationDAO, Logger logger) {
        this.recommendationDAO = recommendationDAO;
        this.logger = logger;
    }

    @GetMapping("")
    public String index(Model model) {
        model.addAttribute("recommendations", recommendationDAO.index());
        logger.info("/recommendations/checks");
        return "recommendations/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model) {
        logger.info(String.format("/sqlinfo/recommendations/%d", id));
        model.addAttribute("recommendation", recommendationDAO.show(id));
        return "recommendations/show";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model) {
        model.addAttribute("recommendation", recommendationDAO.show(id));
        logger.info(String.format("/sqlinfo/recommendations/%d/edit", id));
        return "recommendations/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("recommendation") Recommendation recommendation,
                         @PathVariable("id") int id, Model model) {
        try {
            recommendationDAO.update(id, recommendation);
            logger.info(String.format("Update recommendation with id %d", id));
            return "redirect:/recommendations";
        } catch (Exception exception) {
            model.addAttribute("errorMessage", "Error: "
                    + exception.getMessage());
            logger.warn(String.format("Error in /sqlinfo/recommendations/%d/edit", id), exception);
            return "recommendations/edit";
        }
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id, Model model) {
        try {
            recommendationDAO.delete(id);
            logger.info(String.format("Delete recommendation with id %d", id));
            return "redirect:/recommendations";
        } catch (Exception exception) {
            model.addAttribute("recommendation", recommendationDAO.show(id));
            model.addAttribute("errorMessage", "Error: "
                    + exception.getMessage());
            logger.warn(String.format("Error in delete recommendation with id %d", id), exception);
            return "checks/show";
        }
    }

    @GetMapping("/new")
    public String newRecommendation(Model model) {
        model.addAttribute("recommendation", new Recommendation());
        logger.info("/sqlinfo/recommendations/new");
        return "recommendations/new";
    }

    @PostMapping
    public String create(@ModelAttribute("recommendation") Recommendation recommendation, Model model) {
        try {
            recommendationDAO.save(recommendation);
            logger.info(String.format("Save recommendation %s", recommendation));
            return "redirect:/recommendations";
        } catch (Exception exception) {
            model.addAttribute("errorMessage", "Error: " + exception.getMessage());
            logger.warn(String.format("Error in create recommendation %s", recommendation), exception);
            return "recommendations/edit";
        }
    }

    @GetMapping("/import")
    public String showImport() {
        logger.info("/sqlinfo/checks/import");
        return "recommendations/import";
    }

    @GetMapping("/export")
    public void exportToCsv(HttpServletResponse response) {
        try {
            logger.info("Start /sqlinfo/recommendations/export");
            response.setContentType("application/octet-stream");
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename = recommendations.csv";
            response.setHeader(headerKey, headerValue);
            ServletOutputStream outputStream = response.getOutputStream();
            recommendationDAO.exportToCsv(outputStream);
            outputStream.close();
            logger.info("Finish /sqlinfo/recommendations/export");
        } catch (Exception exception) {
            logger.warn("Error in /sqlinfo/recommendations/export", exception);
        }
    }

    @PostMapping("/import")
    public String importFromCsv(@RequestParam("file") MultipartFile file, Model model) {
       try {
           logger.info("Start /sqlinfo/recommendations/import");
           String originalFilename = file.getOriginalFilename();
           if (originalFilename == null) {
               model.addAttribute("errorMessage", "Error: file error");
               return "friends/import";
           }
           String fileName = StringUtils.cleanPath(originalFilename);
           Path path = Paths.get(USER_DIRECTORY + fileName);
           Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
           recommendationDAO.importFromCsv(path.toAbsolutePath().toString());
           logger.info("Finish /sqlinfo/recommendations/import");
           return "redirect:/recommendations";
       } catch (Exception exception) {
           model.addAttribute("errorMessage", "Error: " + exception.getMessage());
           logger.warn("Error in /sqlinfo/recommendations/import", exception);
           return "recommendations/import";
       }
    }

    @GetMapping("/*")
    public String handle() {
        return "404page";
    }
}
