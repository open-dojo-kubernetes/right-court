package org.open.dojo.rightcourt.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Configuration
public class CourtConfiguration {

    public final static String LEFT_COURT_BASE = "http://localhost:5000/left";
    public final static String LEFT_PLAY = LEFT_COURT_BASE + "/play";
    private RestTemplate restTemplate;

    @Autowired
    public CourtConfiguration(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public boolean checkLeftCourtStatus() {
        return Optional.ofNullable(
                restTemplate.getForObject(LEFT_COURT_BASE + "/health", String.class))
                .isPresent();
    }
}
