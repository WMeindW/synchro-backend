package cz.meind.synchro.synchrobackend.controller;

import cz.meind.synchro.synchrobackend.config.SynchroConfig;
import cz.meind.synchro.synchrobackend.controller.main.Controller;
import cz.meind.synchro.synchrobackend.service.user.FileService;
import cz.meind.synchro.synchrobackend.service.user.auth.SecurityService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
public class FileController extends Controller {
    private final SynchroConfig synchroConfig;
    private final FileService fileService;

    /**
     * Constructs a new Controller with the specified SecurityService.
     *
     * @param securityService The security service responsible for user authentication and authorization.
     */
    public FileController(SecurityService securityService, SynchroConfig synchroConfig, FileService fileService) {
        super(securityService);
        this.synchroConfig = synchroConfig;
        this.fileService = fileService;
    }

    @CrossOrigin
    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file, @RequestParam("username") String username, HttpServletRequest request) {
        /*if (!super.handleApiSecureRequest(request, synchroConfig.getCombinedRole()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
         */
        if (!fileService.uploadFile(file, username, request)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
