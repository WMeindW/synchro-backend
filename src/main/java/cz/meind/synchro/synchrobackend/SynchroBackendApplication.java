package cz.meind.synchro.synchrobackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main entry point for the Synchro Backend application.
 * This class is responsible for starting the Spring Boot application.
 *
 * <p>The class is annotated with {@link org.springframework.boot.autoconfigure.SpringBootApplication}
 * to enable automatic configuration, component scanning, and configuration setup for the Spring Boot application.</p>
 *
 * <p>When this class is run, the {@link org.springframework.boot.SpringApplication#run(Class, String[])}
 * method is invoked, which starts the Spring context and boots up the application.</p>
 *
 * <p>This class doesn't require any specific configuration itself, as the annotations provide all the necessary setup
 * to configure and launch the application. All beans, services, repositories, and controllers within the application
 * context will be automatically discovered and managed by Spring Boot.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *     java -jar synchro-backend-application.jar
 * </pre>
 * <p>This will start the application and run it on the default server.</p>
 *
 * @author [Daniel Linda]
 * @version 0.89
 */
@SpringBootApplication
public class SynchroBackendApplication {

    /**
     * The main method is the entry point of the application.
     * It runs the Spring Boot application.
     *
     * @param args Command-line arguments passed to the application at runtime.
     */
    public static void main(String[] args) {
        SpringApplication.run(SynchroBackendApplication.class, args);
    }

}


