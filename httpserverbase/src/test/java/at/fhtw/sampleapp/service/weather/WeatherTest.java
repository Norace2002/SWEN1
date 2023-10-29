package at.fhtw.sampleapp.service.weather;

import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class WeatherTest {

    @Test
    void testWeatherServiceGetCompleteList() throws Exception {
        WeatherService service = new WeatherService();
        Request request = new Request();
        request.setMethod(Method.GET);
        Response response = service.handleRequest(request);
        assertEquals("Response{status=200, message='OK', contentType='application/json', content='[{\"id\":1,\"region\":\"Europe\",\"city\":\"Vienna\",\"temperature\":28.0},{\"id\":2,\"region\":\"Europe\",\"city\":\"Berlin\",\"temperature\":26.0},{\"id\":4,\"region\":\"Europe\",\"city\":\"Rome\",\"temperature\":35.0}]'}",
                response.toString());
    }
}