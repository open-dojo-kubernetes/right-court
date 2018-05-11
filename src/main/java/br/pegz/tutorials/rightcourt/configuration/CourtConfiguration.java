package br.pegz.tutorials.rightcourt.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Configuration
public class CourtConfiguration {

    @Autowired
    private RestTemplate restTemplate;

    public static String LEFT_COURT_BASE  = "http://localhost:5000/left";
    public static String LEFT_PLAY = LEFT_COURT_BASE + "/play";


    public boolean checkLeftCourtStatus() {
        return Optional.ofNullable(
                restTemplate.getForObject(LEFT_COURT_BASE + "/health", String.class))
                .isPresent();
    }
}
