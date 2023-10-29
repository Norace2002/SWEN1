package at.fhtw.sampleapp.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;

@Getter
public class Weather {
    @JsonAlias({"id"})
    private Integer id;
    @JsonAlias({"region"})
    private String region;
    @JsonAlias({"city"})
    private String city;
    @JsonAlias({"temperature"})
    private float temperature;

    // Jackson needs the default constructor
    public Weather() {}

    public Weather(Integer id, String region, String city, float temperature) {
        this.id = id;
        this.region = region;
        this.city = city;
        this.temperature = temperature;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }
}
