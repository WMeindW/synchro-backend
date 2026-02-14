package cz.meind.synchro.synchrobackend.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class Routes {
    SynchroConfig synchroConfig;

    /**
     * Constructs a new Routes object.
     *
     * @param config The SynchroConfig object containing configuration settings.
     */
    public Routes(SynchroConfig config) {
        this.synchroConfig = config;
    }

    /**
     * Retrieves the contents of a file based on the request URI.
     *
     * @param request The HttpServletRequest object containing the request information.
     * @return A byte array containing the contents of the requested file.
     * @throws RuntimeException If an IOException occurs while reading the file.
     */
    public byte[] getFile(HttpServletRequest request) {
        try {
            return Files.readAllBytes(Path.of(synchroConfig.getSecureRoute() + request.getRequestURI()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Redirects the response to a specified location.
     *
     * @param response The HttpServletResponse object to send the redirect.
     * @param location The URL or path to redirect to.
     * @throws RuntimeException If an IOException occurs while sending the redirect.
     */
    public void redirect(HttpServletResponse response, String location) {
        try {
            response.sendRedirect(location);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
