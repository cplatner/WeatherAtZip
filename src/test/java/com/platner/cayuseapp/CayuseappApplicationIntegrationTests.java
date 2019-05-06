package com.platner.cayuseapp;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * POST, PUT and DELETE are not tested, since they shouldn't exist.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CayuseappApplicationIntegrationTests
{
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void invalidZip_noZip_failsWithBadRequest()
    {
        ResponseEntity<?> response = restTemplate.getForEntity(String.format("http://localhost:%d/weather", port), String.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void invalidZip_tooShortZip_failsWithBadRequest()
    {
        ResponseEntity<?> response = restTemplate.getForEntity(String.format("http://localhost:%d/weather?zipcode=9700", port), String.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * This zip is somewhere in Boston, but the OpenWeatherMap API doesn't seem to support Zip+4
     */
    @Test
    public void invalidZip_tooLongZip_failsWithBadRequest()
    {
        ResponseEntity<?> response = restTemplate.getForEntity(String.format("http://localhost:%d/weather?zipcode=02129-3011", port), String.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Post code for London without country identifier.
     */
    @Test
    public void invalidZip_nonUsZip_failsWithBadRequest()
    {
        ResponseEntity<?> response = restTemplate.getForEntity(String.format("http://localhost:%d/weather?zipcode=WC2N+5DU", port), String.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * This test has a zip code that is a valid length, but doesn't exist on the westher server.
     */
    @Test
    public void invalidZip_zipDoesntExist_failsWithNotFound()
    {
        ResponseEntity<?> response = restTemplate.getForEntity(String.format("http://localhost:%d/weather?zipcode=90000", port), String.class);
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void valid_5DigitZip_succeeds_1()
    {
        ResponseEntity<?> response = restTemplate.getForEntity(String.format("http://localhost:%d/weather?zipcode=97006", port), String.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void valid_5DigitZip_succeeds_2()
    {
        //* Somewhere in Boston
        ResponseEntity<?> response = restTemplate.getForEntity(String.format("http://localhost:%d/weather?zipcode=02129", port), String.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /**
     * Post code for London with country identifier.
     */
    @Test
    public void valid_nonUsZip_succeeds()
    {
        ResponseEntity<?> response = restTemplate.getForEntity(String.format("http://localhost:%d/weather?zipcode=WC2N+5DU,uk", port), String.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
