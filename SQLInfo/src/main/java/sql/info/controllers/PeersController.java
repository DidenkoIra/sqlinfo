package sql.info.controllers;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sql.info.dao.PeerDAO;
import sql.info.models.Peer;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("peers")
public class PeersController {
    private final PeerDAO peerDAO;
    private final String USER_DIRECTORY = System.getProperty("user.dir") + "/";
    private final Logger logger;

    @Autowired
    public PeersController(PeerDAO peerDAO, Logger logger) {
        this.peerDAO = peerDAO;
        this.logger = logger;
    }

    @GetMapping("")
    public String index(Model model) {
        model.addAttribute("peers", peerDAO.index());
        logger.info("/sqlinfo/peers");
        return "peers/index";
    }

    @GetMapping("/{nickname}")
    public String show(@PathVariable("nickname") String nickname, Model model) {
        model.addAttribute("peer", peerDAO.show(nickname));
        logger.info(String.format("/sqlinfo/peers/%s", nickname));
        return "peers/show";
    }

    @GetMapping("/{nickname}/edit")
    public String edit(@PathVariable("nickname") String nickname, Model model) {
        model.addAttribute("peer", peerDAO.show(nickname));
        logger.info(String.format("/sqlinfo/peers/%s/edit", nickname));
        return "peers/edit";
    }

    @DeleteMapping("/{nickname}")
    public String delete(@PathVariable("nickname") String nickname, Model model) {
        try {
            peerDAO.delete(nickname);
            logger.info(String.format("Delete peer with nickname %s", nickname));
            return "redirect:/peers";
        } catch (Exception exception) {
            model.addAttribute("errorMessage", "Error: "+exception.getMessage());
            model.addAttribute("peer", peerDAO.show(nickname));
            logger.warn(String.format("Error in delete peer with nickname %s", nickname), exception);
            return "peers/show";
        }
    }

    @PatchMapping("/{nickname}")
    public String update(@ModelAttribute("peer") @Valid Peer peer,
                         BindingResult bindingResult,
                         @PathVariable("nickname") String nickname) {
        if(bindingResult.hasErrors()) {
            logger.warn(String.format("%d errors in /sqlinfo/peers/%s/edit", bindingResult.getErrorCount(), nickname));
            return "peers/edit";
        }

        peerDAO.update(nickname, peer);
        logger.info(String.format("Update peer with nickname %s", nickname));
        return "redirect:/peers";
    }

    @GetMapping("/new")
    public String newPeer(Model model) {
        model.addAttribute("peer", new Peer());
        logger.info("/sqlinfo/peers/new");
        return "peers/new";
    }

    @PostMapping
    public String create(@ModelAttribute("peer") @Valid Peer peer,
                         BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            logger.warn(String.format("%d errors in create peer %s", bindingResult.getErrorCount(), peer));
            return "peers/new";
        }
        try {
            peerDAO.save(peer);
            logger.info(String.format("Save peer %s", peer));
            return "redirect:/peers";
        } catch (Exception exception) {
            model.addAttribute("errorMessage", "Error: "+exception.getMessage());
            logger.warn(String.format("Error in create peer %s", peer), exception);
            return "peers/new";
        }

    }

    @GetMapping("/import")
    public String showImport() {
        logger.info("/sqlinfo/peers/import");
        return "peers/import";
    }

    @GetMapping("/export")
    public void exportToCsv(HttpServletResponse response) {
        try {
            logger.info("Start /sqlinfo/peers/export");
            response.setContentType("application/octet-stream");
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename = peers.csv";
            response.setHeader(headerKey, headerValue);
            ServletOutputStream outputStream = response.getOutputStream();
            peerDAO.exportToCsv(outputStream);
            outputStream.close();
            logger.info("Finish /sqlinfo/peers/export");
        } catch (Exception exception) {
            logger.warn("Error in /sqlinfo/peers/export", exception);
        }
    }

    @PostMapping("/import")
    public String importFromCsv(@RequestParam("file") MultipartFile file, Model model){
        try {
            logger.info("Start /sqlinfo/peers/import");
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                model.addAttribute("errorMessage", "Error: file error");
                return "friends/import";
            }
            String fileName = StringUtils.cleanPath(originalFilename);
            Path path = Paths.get(USER_DIRECTORY + fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            peerDAO.importFromCsv(path.toAbsolutePath().toString());
            logger.info("Finish /sqlinfo/peers/import");
            return "redirect:/peers";
        } catch (Exception exception) {
            model.addAttribute("errorMessage", "Error: " + exception.getMessage());
            logger.warn("Error in /sqlinfo/peers/import", exception);
            return "peers/import";
        }
    }

    @GetMapping("/*")
    public String handle() {
        return "404page";
    }
}
