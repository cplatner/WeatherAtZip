package com.platner.cayuseapp.model;

public class WeatherMessage
{
    private String message;

    public WeatherMessage(final String city, final String temperature, final String timezone, final String elevation)
    {
        this.message = String.format(
                "At the location %s, the temperature is %.1fF, the timezone is %s, and the elevation is %.1fm",
                city, Double.valueOf(temperature), timezone, Double.valueOf(elevation));
    }

    public String getMessage()
    {
        return this.message;
    }

    public void setMessage(String message)
    {

    }
}
