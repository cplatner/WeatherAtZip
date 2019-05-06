package com.platner.cayuseapp.model;

public class WeatherMessage
{
    private String message;

    public WeatherMessage(final WeatherData weatherData)
    {
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

    public void setMessage(String message)
    {

    }
}
