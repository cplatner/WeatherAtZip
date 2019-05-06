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

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CayuseappApplicationIntegrationTests
{
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void invalidZip_noZip_failsWith400()
    {
        TestRestTemplate rest = new TestRestTemplate();
        ResponseEntity<String> s = rest.getForEntity(String.format("http://localhost:%d/weather", port), String.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, s.getStatusCode());
    }

    @Test
    public void invalidZip_tooShortZip_failsWith400()
    {
        TestRestTemplate rest = new TestRestTemplate();
        ResponseEntity<String> s = rest.getForEntity(String.format("http://localhost:%d/weather?zipcode=9700", port), String.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, s.getStatusCode());

    }

    @Test
    public void invalidZip_tooLongZip_failsWith400()
    {
        TestRestTemplate rest = new TestRestTemplate();
        ResponseEntity<String> s = rest.getForEntity(String.format("http://localhost:%d/weather?zipcode=97006-00011", port), String.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, s.getStatusCode());

    }

    @Test
    public void invalidZip_nonUsZip_failsWith404()
    {
        TestRestTemplate rest = new TestRestTemplate();
        //* Post code for London
        ResponseEntity<String> s = rest.getForEntity(String.format("http://localhost:%d/weather?zipcode=WC2N+5DU", port), String.class);
        Assert.assertEquals(HttpStatus.NOT_FOUND, s.getStatusCode());

    }

    @Test
    public void invalidZip_zipDoesntExist_failsWith404()
    {
        TestRestTemplate rest = new TestRestTemplate();
        ResponseEntity<String> s = rest.getForEntity(String.format("http://localhost:%d/weather?zipcode=90000", port), String.class);
        Assert.assertEquals(HttpStatus.NOT_FOUND, s.getStatusCode());

    }

    //* POST returns error
    //* PUT returns error
    //* DELETE returns error

    //* Valid tests
    @Test
    public void valid_zip_succeeds()
    {
        TestRestTemplate rest = new TestRestTemplate();

        ResponseEntity<String> s = rest.getForEntity(String.format("http://localhost:%d/weather?zipcode=97006", port), String.class);

        Assert.assertEquals(HttpStatus.OK, s.getStatusCode());
    }

    @Test
    public void valid_zip4_succeeds()
    {
        TestRestTemplate rest = new TestRestTemplate();

        ResponseEntity<String> s = rest.getForEntity(String.format("http://localhost:%d/weather?zipcode=970060001", port), String.class);

        Assert.assertEquals(HttpStatus.OK, s.getStatusCode());
    }
}
