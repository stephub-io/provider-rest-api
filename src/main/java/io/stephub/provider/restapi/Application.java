package io.stephub.provider.restapi;

import io.stephub.provider.util.controller.EnableProviderUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableProviderUtil
public class Application {

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
