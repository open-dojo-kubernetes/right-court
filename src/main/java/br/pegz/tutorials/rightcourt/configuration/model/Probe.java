package br.pegz.tutorials.rightcourt.configuration.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Probe {
    private String description;
    private Boolean status;
    private String endpoint;
}
