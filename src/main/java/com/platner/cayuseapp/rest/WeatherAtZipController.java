package com.platner.cayuseapp.rest;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platner.cayuseapp.SimpleRestTemplate;
import com.platner.cayuseapp.model.WeatherMessage;
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

    private Map<String, String> timezones = new HashMap<>();
    private Map<String, String> elevations = new HashMap<>();

    @GetMapping
    public ResponseEntity<?> weather(@RequestParam(value = "zipcode") String zipcode)
    {
        ResponseEntity<?> checkZip = validateZipcode(zipcode);
        if (checkZip.getStatusCode().isError()) {
            return checkZip;
        }

        ObjectMapper mapper = new ObjectMapper();
        SimpleRestTemplate restClient = new SimpleRestTemplate();

        String city = "", temp = "", timezone = "", elevation = "";
        String lat = "", lon = "";

        //* Get weather
        String weatherUrl = String.format(weatherApiUri, zipcode, weatherApiAppId);
        ResponseEntity<?> response = get(weatherUrl);
        if (response.getStatusCode().isError()) {
            return response;
        }
        try {
            JsonNode root = mapper.readTree(response.getBody().toString());
            //* temperature is at main.temp
            temp = root.at("/main/temp").asText();
            city = root.at("/name").asText();
            lat = root.at("/coord/lat").asText();
            lon = root.at("/coord/lon").asText();
        }
        catch (JsonParseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error parsing data from temperature server");
        }
        catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading data from temperature server");
        }

        //* Get timezonee
        if (timezones.containsKey(zipcode)) {
            timezone = timezones.get(zipcode);
        } else {
            String timezoneUrl = String.format(timezoneApiUrl, lat, lon, Instant.now().getEpochSecond(), timezoneApiAppId);
            String s = restClient.get(timezoneUrl);
            try {
                JsonNode root = mapper.readTree(s);
                timezone = root.at("/timeZoneName").asText();
                timezones.put(zipcode, timezone);
            }
            catch (JsonParseException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error parsing data from temperature server");
            }
            catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading data from temperature server");
            }
        }

        //* Get elevation
        if (elevations.containsKey(zipcode)) {
            elevation = elevations.get(zipcode);
        } else {
            String elevationUrl = String.format(elevationApiUrl, lat, lon, elevationApiAppId);
            String s = restClient.get(elevationUrl);
            try {
                JsonNode root = mapper.readTree(s);
                elevation = root.at("/results/0/elevation").asText();
                elevations.put(zipcode, elevation);
            }
            catch (JsonParseException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error parsing data from temperature server");
            }
            catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading data from temperature server");
            }
        }

        return new ResponseEntity<>(new WeatherMessage(city, temp, timezone, elevation), HttpStatus.OK);
    }

    private ResponseEntity<?> get(String uri)
    {
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "*/*");

        ResponseEntity<?> responseEntity = ResponseEntity.badRequest().build();

        try {
            responseEntity = rest.exchange(uri,
                    HttpMethod.GET, new HttpEntity<>("", headers), String.class);
        }
        catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body("Error getting data from outside service");
        }

        return responseEntity;
    }

    private ResponseEntity<?> validateZipcode(String zipcode)
    {
        if (zipcode == null || zipcode.isEmpty()) {
            return ResponseEntity.badRequest().body("Missing Zip Code");
        }

        //* check for out-of-country zips
        for (char c : zipcode.toCharArray()) {
            if (!Character.isDigit(c) || c == '-') {
                return ResponseEntity.badRequest().body("Zip Code has invalid characters");
            }
        }

        if (zipcode.length() < 5) {
            return ResponseEntity.badRequest().body("Zip Code is too short");
        }

        if (zipcode.length() > 5) {
            return ResponseEntity.badRequest().body("Zip Code is too long");
        }

        return ResponseEntity.ok().build();
    }
}
