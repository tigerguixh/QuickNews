
package com.tiger.quicknews.http.json;

import android.content.Context;

import com.tiger.quicknews.bean.WeatherModle;
import com.tiger.quicknews.utils.TimeUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WeatherListJson extends JsonPacket {

    public List<WeatherModle> weatherListModles = new ArrayList<WeatherModle>();

    public static WeatherListJson weatherListJson;

    public WeatherListJson(Context context) {
        super(context);
    }

    public static WeatherListJson instance(Context context) {
        if (weatherListJson == null) {
            weatherListJson = new WeatherListJson(context);
        }

        return weatherListJson;
    }

    public List<WeatherModle> readJsonWeatherListModles(String res) {
        weatherListModles.clear();
        try {
            if (res == null || res.equals("")) {
                return null;
            }
            WeatherModle weatherModle = null;
            JSONObject jsonObject = new JSONObject(res);
            JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("forecast");
            for (int i = 0; i < jsonArray.length(); i++) {
                weatherModle = readJsonWeatherModles(jsonArray.getJSONObject(i));
                weatherListModles.add(weatherModle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.gc();
        }
        return weatherListModles;
    }

    public List<WeatherModle> readJsonweatherListModles(String res) {
        weatherListModles.clear();
        try {
            if (res == null || res.equals("")) {
                return null;
            }
            WeatherModle weatherModle = null;
            JSONObject jsonObject = new JSONObject(res);
            JSONObject jsonArray = jsonObject.getJSONObject("result").getJSONObject("future");
            for (int i = 0; i < 7; i++) {
                weatherModle = readJsonWeatherModles(jsonArray.getJSONObject("day_"
                        + TimeUtils.dateToWeek(i)));
                weatherListModles.add(weatherModle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.gc();
        }
        return weatherListModles;
    }

    private WeatherModle readJsonWeatherModles(JSONObject jsonObject) throws Exception {
        WeatherModle weatherModle = null;

        String temperature = "";
        String weather = "";
        String wind = "";
        String date = "";

        temperature = getString("high", jsonObject) + " " + getString("low", jsonObject);
        weather = getString("type", jsonObject);
        wind = getString("fengxiang", jsonObject);
        date = getString("date", jsonObject);

        weatherModle = new WeatherModle();

        weatherModle.setDate(TimeUtils.getCurrentTime() + date);
        weatherModle.setTemperature(temperature);
        weatherModle.setWeather(weather);
        weatherModle.setWeek("");
        weatherModle.setWind(wind);

        return weatherModle;
    }

    private WeatherModle readJsonWeatherModle(JSONObject jsonObject) throws Exception {

        WeatherModle weatherModle = null;

        String temperature = "";
        String weather = "";
        String wind = "";
        String week = "";
        String date = "";

        temperature = getString("temperature", jsonObject);
        weather = getString("weather", jsonObject);
        wind = getString("wind", jsonObject);
        week = getString("week", jsonObject);
        date = getString("date", jsonObject);

        weatherModle = new WeatherModle();

        weatherModle.setDate(date);
        weatherModle.setTemperature(temperature);
        weatherModle.setWeather(weather);
        weatherModle.setWeek(week);
        weatherModle.setWind(wind);

        return weatherModle;
    }

}
