package com.platner.cayuseapp.model;

/**
 * Store all of the data that is used to generate the final message.
 */
public class WeatherData
{
    private String zipcode;
    private String city;
    private String temperature;
    private String timezone;
    private String elevation;
    private String latitude;
    private String longitude;

    public String getZipcode()
    {
        return zipcode;
    }

    public WeatherData setZipcode(String zipcode)
    {
        this.zipcode = zipcode;
        return this;
    }

    public String getCity()
    {
        return city;
    }

    public WeatherData setCity(String city)
    {
        this.city = city;
        return this;
    }

    public String getTemperature()
    {
        return temperature;
    }

    public WeatherData setTemperature(String temperature)
    {
        this.temperature = temperature;
        return this;
    }

    public String getTimezone()
    {
        return timezone;
    }

    public WeatherData setTimezone(String timezone)
    {
        this.timezone = timezone;
        return this;
    }

    public String getElevation()
    {
        return elevation;
    }

    public WeatherData setElevation(String elevation)
    {
        this.elevation = elevation;
        return this;
    }

    public String getLatitude()
    {
        return latitude;
    }

    public WeatherData setLatitude(String latitude)
    {
        this.latitude = latitude;
        return this;
    }

    public String getLongitude()
    {
        return longitude;
    }

    public WeatherData setLongitude(String longitude)
    {
        this.longitude = longitude;
        return this;
    }
}
