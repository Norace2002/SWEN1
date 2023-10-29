package at.fhtw.sampleapp.service.weather;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Response;
import at.fhtw.sampleapp.controller.Controller;
import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.dal.repository.WeatherRepository;
import at.fhtw.sampleapp.model.Weather;

import java.util.Collection;
import java.util.List;

public class WeatherController extends Controller {
    // GET /weather
    // gleich wie "public Response getWeather()" nur mittels Repository
    public Response getWeatherPerRepository() {
        UnitOfWork unitOfWork = new UnitOfWork();
        try (unitOfWork){
            Collection<Weather> weatherData = new WeatherRepository(unitOfWork).findAllWeather();

            // "[ { \"id\": 1, \"city\": \"Vienna\", \"temperature\": 9.0 }, { ... }, { ... } ]"
            String weatherDataJSON = this.getObjectMapper().writeValueAsString(weatherData);
            unitOfWork.commitTransaction();
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    weatherDataJSON
            );
        } catch (Exception e) {
            e.printStackTrace();

            unitOfWork.rollbackTransaction();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        }
    }
}
