package cz.meind.synchro.synchrobackend.controller;

import cz.meind.synchro.synchrobackend.config.SynchroConfig;
import cz.meind.synchro.synchrobackend.controller.main.Controller;
import cz.meind.synchro.synchrobackend.database.entities.FileEntity;
import cz.meind.synchro.synchrobackend.service.user.FileService;
import cz.meind.synchro.synchrobackend.service.user.auth.SecurityService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/files")
public class FileController extends Controller {
    private final FileService fileService;
    private final SynchroConfig synchroConfig;

    /**
     * Constructs a new Controller with the specified SecurityService.
     *
     * @param securityService The security service responsible for user authentication and authorization.
     */
    public FileController(SecurityService securityService, FileService fileService, SynchroConfig synchroConfig) {
        super(securityService);
        this.fileService = fileService;
        this.synchroConfig = synchroConfig;
    }

    @CrossOrigin
    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file, @RequestParam("username") String username, HttpServletRequest request) {
        if (!super.handleApiSecureRequest(request, synchroConfig.getCombinedRole()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (!fileService.uploadFile(file, username, request)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @CrossOrigin
    @GetMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam("file") String file, @RequestParam("username") String username, HttpServletRequest request) {
        if (!super.handleApiSecureRequest(request, synchroConfig.getCombinedRole()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (!fileService.deleteFile(file, username, request)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @CrossOrigin
    @GetMapping("/query")
    public ResponseEntity<?> queryFiles(@RequestParam("username") String username, HttpServletRequest request) {
        if (!super.handleApiSecureRequest(request, synchroConfig.getCombinedRole()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        List<FileEntity> files = fileService.queryFiles(username, request);
        if (files == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(files, HttpStatus.OK);
    }

    @CrossOrigin
    @GetMapping("/get")
    public ResponseEntity<?> getFile(@RequestParam("file") String file, @RequestParam("username") String username, HttpServletRequest request) {
        if (!super.handleApiSecureRequest(request, synchroConfig.getCombinedRole()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + encodeFilename(new String(Base64.getDecoder().decode(file), StandardCharsets.UTF_8)));
            return new ResponseEntity<>(fileService.queryFile(file, username, request), headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private String encodeFilename(String filename) {
        return URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
