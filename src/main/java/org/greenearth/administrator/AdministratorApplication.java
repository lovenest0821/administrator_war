package org.greenearth.administrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class AdministratorApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(AdministratorApplication.class, args);
    }
}
