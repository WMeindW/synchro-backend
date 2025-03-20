package cz.meind.synchro.synchrobackend.controller;

import cz.meind.synchro.synchrobackend.config.SynchroConfig;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomErrorController implements ErrorController {

    private final SynchroConfig synchroConfig;

    public CustomErrorController(SynchroConfig synchroConfig) {
        this.synchroConfig = synchroConfig;
    }

    @RequestMapping("/error")
    public byte[] handleError() {
        return new byte[0];
    }
}
