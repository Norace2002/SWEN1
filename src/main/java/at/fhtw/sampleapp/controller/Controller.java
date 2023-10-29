package at.fhtw.sampleapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

@Getter
public class Controller {
    private final ObjectMapper objectMapper;

    public Controller() {
        this.objectMapper = new ObjectMapper();
    }

}
