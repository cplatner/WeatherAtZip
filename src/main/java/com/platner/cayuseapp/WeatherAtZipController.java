package com.platner.cayuseapp;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
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

    @GetMapping("/weather")
    public WeatherMessage weather(@RequestParam(value = "zipcode") String zipcode)
    {
        ObjectMapper mapper = new ObjectMapper();
        SimpleRestTemplate restClient = new SimpleRestTemplate();

        String city = "", temp = "", timezone = "", elevation = "";
        String lat = "", lon = "";

        //* Get weather
        String weatherUrl = String.format(weatherApiUri, zipcode, weatherApiAppId);
        String s = restClient.get(weatherUrl);
        try {
            JsonNode root = mapper.readTree(s);
            //* temperature is at main.temp
            temp = root.at("/main/temp").asText();
            city = root.at("/name").asText();
            lat = root.at("/coord/lat").asText();
            lon = root.at("/coord/lon").asText();
        } catch (JsonParseException e) {
            // return a 500
        } catch (IOException e) {
            //* return a 500
        }

        //* Get timezonee
        if (timezones.containsKey(zipcode)) {
            timezone = timezones.get(zipcode);
        } else {
            String timezoneUrl = String.format(timezoneApiUrl, lat, lon, Instant.now().getEpochSecond(), timezoneApiAppId);
            s = restClient.get(timezoneUrl);
            try {
                JsonNode root = mapper.readTree(s);
                timezone = root.at("/timeZoneName").asText();
                timezones.put(zipcode, timezone);
            } catch (JsonParseException e) {
                // return a 500
            } catch (IOException e) {
                //* return a 500
            }
        }

        //* Get elevation
        if (elevations.containsKey(zipcode)) {
            elevation = elevations.get(zipcode);
        } else {
            String elevationUrl = String.format(elevationApiUrl, lat, lon, elevationApiAppId);
            s = restClient.get(elevationUrl);
            try {
                JsonNode root = mapper.readTree(s);
                elevation = root.at("/results/0/elevation").asText();
                elevations.put(zipcode, elevation);
            } catch (JsonParseException e) {
                // return a 500
            } catch (IOException e) {
                //* return a 500
            }
        }

        return new WeatherMessage(city, temp, timezone, elevation);
    }
}
