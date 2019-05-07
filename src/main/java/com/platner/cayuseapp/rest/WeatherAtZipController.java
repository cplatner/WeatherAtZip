package com.platner.cayuseapp.rest;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platner.cayuseapp.model.WeatherData;
import com.platner.cayuseapp.model.WeatherMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/weather")
public class WeatherAtZipController
{
    private static Logger logger = LoggerFactory.getLogger(WeatherAtZipController.class);
    @Value("${weatherapi.url}")
    private String weatherApiUri;
    @Value("${weatherapi.appid}")
    private String weatherApiAppId;
    @Value("${timezoneapi.url}")
    private String timezoneApiUrl;
    @Value("${timezoneapi.appid}")
    private String timezoneApiAppId;
    @Value("${elevationapi.url}")
    private String elevationApiUrl;
    @Value("${elevationapi.appid}")
    private String elevationApiAppId;

    /**
     * Cache for timezones, since they don't change between calls
     */
    private final Map<String, String> timezonesByZip = new HashMap<>();
    /**
     * Cache for elevations, since they don't change between calls
     */
    private final Map<String, String> elevationsByZip = new HashMap<>();

    private static final ObjectMapper mapper = new ObjectMapper();

    @GetMapping
    public ResponseEntity<?> weather(@RequestParam(value = "zipcode") String zipcode)
    {
        WeatherData data = new WeatherData();
        try {
            validateZipcode(zipcode);

            data.setZipcode(zipcode);
            getWeather(zipcode, data);
            getTimezone(zipcode, data);
            getElevation(zipcode, data);
        }
        catch (WeatherRestException e) {
            return e.getInternalEntity();
        }

        return new ResponseEntity<>(new WeatherMessage(data), HttpStatus.OK);
    }

    private void getWeather(final String zipcode, WeatherData data) throws WeatherRestException
    {
        ResponseEntity<?> response = getFromExternalService(String.format(weatherApiUri, zipcode, weatherApiAppId));
        if (response.getStatusCode().isError()) {
            throw new WeatherRestException(response);
        }
        try {
            JsonNode root = mapper.readTree(response.getBody().toString());
            data.setTemperature(root.at("/main/temp").asText())
                    .setCity(root.at("/name").asText())
                    .setLatitude(root.at("/coord/lat").asText())
                    .setLongitude(root.at("/coord/lon").asText());

        }
        catch (JsonParseException e) {
            throw new WeatherRestException(
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error parsing data from temperature server"));
        }
        catch (IOException e) {
            throw new WeatherRestException(
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading data from temperature server"));
        }
    }

    private void getTimezone(final String zipcode, WeatherData data) throws WeatherRestException
    {
        if (timezonesByZip.containsKey(zipcode)) {
            data.setTimezone(timezonesByZip.get(zipcode));
        } else {
            ResponseEntity<?> response = getFromExternalService(
                    String.format(timezoneApiUrl,
                            data.getLatitude(), data.getLongitude(), Instant.now().getEpochSecond(), timezoneApiAppId));

            try {
                JsonNode root = mapper.readTree(response.getBody().toString());
                timezonesByZip.put(zipcode,
                        data.setTimezone(root.at("/timeZoneName").asText()).getTimezone());
            }
            catch (JsonParseException e) {
                throw new WeatherRestException(
                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error parsing data from timezone server"));
            }
            catch (IOException e) {
                throw new WeatherRestException(
                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading data from timezone server"));
            }
        }
    }

    private void getElevation(final String zipcode, WeatherData data) throws WeatherRestException
    {
        if (elevationsByZip.containsKey(zipcode)) {
            data.setElevation(elevationsByZip.get(zipcode));
        } else {
            ResponseEntity<?> response = getFromExternalService(
                    String.format(elevationApiUrl, data.getLatitude(), data.getLongitude(), elevationApiAppId));
            try {
                JsonNode root = mapper.readTree(response.getBody().toString());
                elevationsByZip.put(zipcode,
                        data.setElevation(root.at("/results/0/elevation").asText()).getElevation());
            }
            catch (JsonParseException e) {
                throw new WeatherRestException(
                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error parsing data from elevation server"));
            }
            catch (IOException e) {
                throw new WeatherRestException(
                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading data from elevation server"));
            }
        }
    }

    private static ResponseEntity<?> getFromExternalService(String uri)
            throws WeatherRestException
    {
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "*/*");

        ResponseEntity<?> responseEntity;

        try {
            responseEntity = rest.exchange(uri,
                    HttpMethod.GET, new HttpEntity<>("", headers), String.class);
        }
        catch (HttpStatusCodeException e) {
            logger.debug(String.format("Can't get values from external service at %s", uri), e);
            throw new WeatherRestException(
                    ResponseEntity.status(e.getStatusCode()).body("Error getting data from outside service"));
        }

        return responseEntity;
    }

    /**
     * Basic checks for valid zip codes
     */
    private static void validateZipcode(String zipcode)
            throws WeatherRestException
    {
        if (zipcode == null || zipcode.isEmpty()) {
            throw new WeatherRestException(ResponseEntity.badRequest().body("Missing Zip Code"));
        }

        //* check for out-of-country zips
        for (char c : zipcode.toCharArray()) {
            if (!Character.isDigit(c)) {
                throw new WeatherRestException(ResponseEntity.badRequest().body("Zip Code has invalid characters"));
            }
        }

        if (zipcode.length() < 5) {
            throw new WeatherRestException(ResponseEntity.badRequest().body("Zip Code is too short"));
        }

        if (zipcode.length() > 5) {
            throw new WeatherRestException(ResponseEntity.badRequest().body("Zip Code is too long"));
        }
    }
}
