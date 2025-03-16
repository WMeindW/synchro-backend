package cz.meind.synchro.synchrobackend.service.user;

import cz.meind.synchro.synchrobackend.config.SynchroConfig;
import cz.meind.synchro.synchrobackend.database.entities.FileEntity;
import cz.meind.synchro.synchrobackend.database.entities.UserEntity;
import cz.meind.synchro.synchrobackend.database.repositories.FileRepository;
import cz.meind.synchro.synchrobackend.database.repositories.UserRepository;
import cz.meind.synchro.synchrobackend.service.user.auth.SecurityService;
import cz.meind.synchro.synchrobackend.service.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;

@Service
public class FileService {
    private final SynchroConfig synchroConfig;
    private final JwtUtil jwtUtil;
    private final SecurityService securityService;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    public FileService(SynchroConfig synchroConfig, JwtUtil jwtUtil, SecurityService securityService, FileRepository fileRepository, UserRepository userRepository) {
        this.synchroConfig = synchroConfig;
        this.jwtUtil = jwtUtil;
        this.securityService = securityService;
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void setup() throws IOException {
        Path path = Paths.get(synchroConfig.getUserFileLocation());
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    public boolean uploadFile(MultipartFile file, String username, HttpServletRequest request) {
        //if (!hasPermissions(request, username)) return false;
        if (!checkFile(file, username)) return false;
        try {
            Files.createDirectories(Path.of(synchroConfig.getUserFileLocation() + "/" + username + "/"));
            Files.write(Path.of(synchroConfig.getUserFileLocation() + "/" + username + "/" + file.getOriginalFilename()), file.getBytes(), StandardOpenOption.CREATE_NEW);
            fileRepository.save(new FileEntity(file.getOriginalFilename(), file.getSize(), userRepository.findByUsername(username).get()));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean deleteFile(String file, String username, HttpServletRequest request) {
        //if (!hasPermissions(request, username)) return false;
        try {
            Files.delete(Path.of(synchroConfig.getUserFileLocation() + "/" + username + "/" + file));
            fileRepository.deleteDistinctByFileName(file);
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    public List<FileEntity> queryFiles(String username, HttpServletRequest request) {
        //if (!hasPermissions(request, username)) return false;
        Optional<UserEntity> user = userRepository.findByUsername(username);
        return user.map(fileRepository::findFileEntitiesByUser).orElse(null);
    }

    public byte[] queryFile(String file, String username, HttpServletRequest request) throws IOException {
        //if (!hasPermissions(request, username)) return false;
        return Files.readAllBytes(Path.of(synchroConfig.getUserFileLocation() + "/" + username + "/" + file));
    }

    /**
     * Checks if the user has the necessary permissions to perform an action on the event.
     *
     * @param request  The HTTP request containing authentication details.
     * @param username The username of the user requesting the action.
     * @return true if the user has the necessary permissions, false otherwise.
     */
    private boolean hasPermissions(HttpServletRequest request, String username) {
        Claims c = jwtUtil.extractClaims(securityService.extractCookie(request));
        if (c == null) return false;
        return c.get("role").toString().equals(synchroConfig.getAdminRole()) || c.getSubject().equals(username);
    }

    private boolean checkFile(MultipartFile file, String username) {
        if (file.isEmpty()) return false;
        Optional<UserEntity> user = userRepository.findByUsername(username);
        if (user.isEmpty()) return false;
        List<FileEntity> userFiles = fileRepository.findFileEntitiesByUser(user.get());
        long totalSize = userFiles.stream().mapToLong(FileEntity::getSize).sum();
        return (totalSize + file.getSize()) <= synchroConfig.getMaxUserFileSize();
    }
}
