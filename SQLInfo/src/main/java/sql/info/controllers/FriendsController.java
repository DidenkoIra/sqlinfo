package sql.info.controllers;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sql.info.dao.FriendDAO;
import sql.info.models.Friend;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("friends")
public class FriendsController {
    private final FriendDAO friendDAO;
    private final String USER_DIRECTORY = System.getProperty("user.dir") + "/";
    private final Logger logger;

    @Autowired
    public FriendsController(FriendDAO friendDAO, Logger logger) {
        this.friendDAO = friendDAO;
        this.logger = logger;
    }

    @GetMapping("")
    public String index(Model model) {
        model.addAttribute("friends", friendDAO.index());
        logger.info("/sqlinfo/friends");
        return "friends/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model) {
        model.addAttribute("friend", friendDAO.show(id));
        logger.info(String.format("/sqlinfo/friends/%d", id));
        return "friends/show";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model) {
        model.addAttribute("friend", friendDAO.show(id));
        logger.info(String.format("/sqlinfo/friends/%d/edit", id));
        return "friends/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("friend") Friend friend,
                         @PathVariable("id") int id, Model model) {
        try {
            friendDAO.update(id, friend);
            logger.info(String.format("Update friends with id %d", id));
            return "redirect:/friends";
        } catch (Exception exception) {
            model.addAttribute("errorMessage", "Error: "
                    + exception.getMessage());
            logger.warn(String.format("Error in /sqlinfo/friends/%d/edit", id), exception);
            return "friends/edit";
        }
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id, Model model) {
        try {
            friendDAO.delete(id);
            logger.info(String.format("Delete friends with id %d", id));
            return "redirect:/friends";
        } catch (Exception exception) {
            model.addAttribute("friend", friendDAO.show(id));
            model.addAttribute("errorMessage", "Error: "
                    + exception.getMessage());
            logger.warn(String.format("Error in delete friends with id %d", id), exception);
            return "friends/show";
        }
    }

    @GetMapping("/new")
    public String newFriend(Model model) {
        model.addAttribute("friend", new Friend());
        logger.info("/sqlinfo/friends/new");
        return "friends/new";
    }

    @PostMapping
    public String create(@ModelAttribute("friend") Friend friend, Model model) {
        try {
            friendDAO.save(friend);
            logger.info(String.format("Save friend %s", friend));
            return "redirect:/friends";
        } catch (Exception exception) {
            model.addAttribute("errorMessage", "Error: " + exception.getMessage());
            logger.warn(String.format("Error in create friend %s", friend), exception);
            return "friends/edit";
        }
    }

    @GetMapping("/import")
    public String showImport() {
        logger.info("sqlinfo/friends/import");
        return "friends/import";
    }

    @GetMapping("/export")
    public void exportToCsv(HttpServletResponse response) {
        try {
            logger.info("Start /sqlinfo/friends/export");
            response.setContentType("application/octet-stream");
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename = friends.csv";
            response.setHeader(headerKey, headerValue);
            ServletOutputStream outputStream = response.getOutputStream();
            friendDAO.exportToCsv(outputStream);
            outputStream.close();
            logger.info("Finish /sqlinfo/friends/export");
        } catch (Exception exception) {
            logger.warn("Error in /sqlinfo/friends/export", exception);
        }
    }

    @PostMapping("/import")
    public String importFromCsv(@RequestParam("file") MultipartFile file, Model model) {
        try {
            logger.info("Start /sqlinfo/friends/import");
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                model.addAttribute("errorMessage", "Error: file error");
                return "friends/import";
            }
            String fileName = StringUtils.cleanPath(originalFilename);
            Path path = Paths.get(USER_DIRECTORY + fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            friendDAO.importFromCsv(path.toAbsolutePath().toString());
            logger.info("Finish /sqlinfo/friends/import");
            return "redirect:/friends";
        } catch (Exception exception) {
            model.addAttribute("errorMessage", "Error: " + exception.getMessage());
            logger.warn("Error in /sqlinfo/friends/import", exception);
            return "friends/import";
        }
    }

    @GetMapping("/*")
    public String handle() {
        return "404page";
    }
}
