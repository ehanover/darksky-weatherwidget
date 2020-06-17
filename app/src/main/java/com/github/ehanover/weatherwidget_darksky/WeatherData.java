package com.github.ehanover.weatherwidget_darksky;

import com.github.ehanover.weatherwidget_darkskywidget.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WeatherData {
    static String LOG = "ASDF";
    static SimpleDateFormat sdfWeekly = new SimpleDateFormat("EEE d", Locale.ENGLISH); // looks like "Dec 31"
    //static Map<String, Integer> iconNamesToR = new HashMap<>().putAll();

    private Date date;
    private String iconName;
    private float precipProb;
    private float precipIntensity;
    private float tempHigh;
    private float tempLow;
    private float cloudCover;
    // Unused fields:
    // private String summary;
    // private float humidity;
    // private float windSpeed;

    public WeatherData(long dateSeconds, String iconName, float precipProb, float precipIntensity, float tempHigh, float tempLow, float cloudCover) {
        this.date = new Date(dateSeconds * 1000); // convert seconds to milliseconds for java date
        this.iconName = iconName;
        this.precipProb = precipProb;
        this.precipIntensity = precipIntensity;
        this.tempHigh = tempHigh;
        this.tempLow = tempLow;
        this.cloudCover = cloudCover;
        // this.summary = summary;
        // this.humidity = humidity;
        // this.windSpeed = windSpeed;
    }

    public String getDateString(){
        if(date == null)
            return "null";
        return sdfWeekly.format(date);
    }

    public String getTempsString(){
        return (int)tempHigh + "°/" + (int)tempLow + "°";
    }

    public String getCloudCoverString(){
        return (int)(cloudCover * 100) + "%";
    }

    public String getPrecipString(){
        String s = (int)(precipProb * 100) + "%";
        // if(isPrecipIntense())
            // return s + String.format(Locale.US, "\n%.1fi", precipIntensity).replace("0.", ".");
        return s;
    }

    public boolean isPrecipIntense(){ // used for text styling if precip intensity is extra high
        return precipIntensity >= 0.09;
    }

    public int getIconR(){
        try {
            return Widget1ServiceFactory.iconNamesToR.get(iconName);
        } catch (NullPointerException e){
            // Log.e(LOG, "Cannot find a matching icon in R map, returning the NA icon");
            return R.drawable.ic_wi_na;
        }
    }

    @Override
    public String toString(){
        return "WeatherData with date " + getDateString();
    }


}
