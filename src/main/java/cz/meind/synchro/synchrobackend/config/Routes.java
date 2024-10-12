package cz.meind.synchro.synchrobackend.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class Routes {
    SynchroConfig config;

    public Routes(SynchroConfig config) {
        this.config = config;
    }

    public byte[] getFile(HttpServletRequest request) {
        try {
            return Files.readAllBytes(Path.of(config.getSecureRoute() + request.getRequestURI()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void redirect(HttpServletResponse response, String location) {
        try {
            response.sendRedirect(location);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
