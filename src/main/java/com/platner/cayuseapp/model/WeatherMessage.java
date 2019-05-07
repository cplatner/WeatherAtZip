package com.platner.cayuseapp.model;

public class WeatherMessage
{
    private String message;
    private String city;
    private String zipcode;
    private double temperature;
    private String timezone;
    private double elevation;

    public WeatherMessage(final WeatherData weatherData)
    {
        this.city = weatherData.getCity();
        this.zipcode = weatherData.getZipcode();
        this.temperature = Double.valueOf(weatherData.getTemperature());
        this.timezone = weatherData.getTimezone();
        this.elevation = Double.valueOf(weatherData.getElevation());

        this.message = String.format(
                "At the location %s, the temperature is %.1fF, the timezone is %s, and the elevation is %.1fm",
                weatherData.getCity(),
                Double.valueOf(weatherData.getTemperature()),
                weatherData.getTimezone(),
                Double.valueOf(weatherData.getElevation()));
    }

    public String getMessage()
    {
        return this.message;
    }

    public String getCity()
    {
        return city;
    }

    public String getZipcode()
    {
        return zipcode;
    }

    public double getTemperature()
    {
        return temperature;
    }

    public String getTimezone()
    {
        return timezone;
    }

    public double getElevation()
    {
        return elevation;
    }
}
