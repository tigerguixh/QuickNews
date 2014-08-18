
package com.tiger.quicknews.bean;

public class WeatherModle extends BaseModle {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * temperature
     */
    private String temperature;
    /**
     * weather
     */
    private String weather;
    /**
     * wind
     */
    private String wind;
    /**
     * week
     */
    private String week;
    /**
     * date
     */
    private String date;

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
