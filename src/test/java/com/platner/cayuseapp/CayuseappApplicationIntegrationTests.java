package com.platner.cayuseapp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CayuseappApplicationIntegrationTests
{
//    @Autowired
//    WebApplicationContext webApplicationContext;

    @LocalServerPort
    int randomServerPort;

    @Test
    public void invalidZip_noZip_failsWith404()
    {
        SimpleRestTemplate rest = new SimpleRestTemplate();
        String response = rest.get(String.format("http://localhost:%d/weather", randomServerPort));


        //Verify request succeed
//        Assert.assertEquals(200, result.getStatusCodeValue());
//        Assert.assertEquals(true, result.getBody().contains("employeeList"));

    }

    @Test
    public void invalidZip_tooShortZip_failsWith404()
    {

    }

    @Test
    public void invalidZip_tooLongZip_failsWith404()
    {

    }

    @Test
    public void invalidZip_nonUsZip_failsWith404()
    {

    }

    //* POST returns error
    //* PUT returns error
    //* DELETE returns error

    //* Valid tests
    @Test
    public void valid_zip_succeeds()
    {
        SimpleRestTemplate rest = new SimpleRestTemplate();
        String response = rest.get(String.format("http://localhost:%d/weather/zipcode=97006", randomServerPort));

System.out.println(response);
        //Verify request succeed
//        Assert.assertEquals(200, result.getStatusCodeValue());
//        Assert.assertEquals(true, result.getBody().contains("employeeList"));

    }
}
