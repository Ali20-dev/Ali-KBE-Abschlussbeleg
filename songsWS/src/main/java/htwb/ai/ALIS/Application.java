package htwb.ai.ALIS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
class Application extends SpringBootServletInitializer {
    public static void main(String[] args) {
        System.setProperty("server.servlet.context-path", "/songsWS-ALIS");
        SpringApplication.run(Application.class, args);
    }

    // for deployment in tomcat
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Application.class);
    }
}