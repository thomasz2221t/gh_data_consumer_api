package pl.cichon.retrivegithubprofile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
public class RetriveGithubProfileApplication {

    public static void main(String[] args) {
        SpringApplication.run(RetriveGithubProfileApplication.class, args);
    }

}
