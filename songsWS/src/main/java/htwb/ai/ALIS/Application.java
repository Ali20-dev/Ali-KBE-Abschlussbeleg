package htwb.ai.ALIS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        System.setProperty("server.servlet.context-path", "/songsWS-ALIS");
        SpringApplication.run(Application.class, args);
    }
}