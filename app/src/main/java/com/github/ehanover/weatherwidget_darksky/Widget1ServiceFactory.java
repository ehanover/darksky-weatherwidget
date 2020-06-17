package com.github.ehanover.weatherwidget_darksky;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.github.ehanover.weatherwidget_darkskywidget.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

//https://github.com/commonsguy/cw-advandroid/blob/master/AppWidget/LoremWidget/src/com/commonsware/android/appwidget/lorem/LoremViewsFactory.java
public class Widget1ServiceFactory implements RemoteViewsService.RemoteViewsFactory {
    final static String LOG = "ASDF";
    final static String PACKAGE = "com.github.ehanover.weatherwidget_darksky";

    static Map<String, Integer> iconNamesToR;

    private Context context;
    String key;
    int numItems;
    String location;
    boolean apparent;
    WeatherData[] items;

    Widget1ServiceFactory(Context context, Intent intent){
        Log.d(LOG, "Widget1ServiceFactory constructor");
        this.context = context;

        // svg icons from https://github.com/erikflowers/weather-icons/tree/master/svg
        iconNamesToR = new HashMap<String, Integer>();
        String[] icons = {"clear-day", "clear-night", "rain", "snow", "sleet", "wind", "fog", "cloudy", "partly-cloudy-day", "partly-cloudy-night", "hail", "thunderstorm", "tornado"};
        int[] rs = {R.drawable.ic_wi_day_sunny, R.drawable.ic_wi_night_clear, R.drawable.ic_wi_rain, R.drawable.ic_wi_snow, R.drawable.ic_wi_sleet, R.drawable.ic_wi_strong_wind, R.drawable.ic_wi_cloudy_windy_fog, R.drawable.ic_wi_cloudy, R.drawable.ic_wi_day_cloudy, R.drawable.ic_wi_night_alt_cloudy, R.drawable.ic_wi_sleet, R.drawable.ic_wi_thunderstorm, R.drawable.ic_wi_tornado};
        for(int i=0; i<rs.length; i++){
            iconNamesToR.put(icons[i], rs[i]);
        }
    }

    @Override
    public RemoteViews getViewAt(int position) {
        // Log.d(LOG, "getViewAt update " + position + ". WD: " + items[position].getTemp());
        RemoteViews rvsRow = new RemoteViews(context.getPackageName(), R.layout.widget1_row);
        try {
            WeatherData wd = items[position];

            rvsRow.setTextViewText(R.id.row_textview_date, wd.getDateString());
            rvsRow.setTextViewText(R.id.row_textview_temperature, wd.getTempsString());
            rvsRow.setTextViewText(R.id.row_textview_precip, wd.getPrecipString());
            rvsRow.setTextViewText(R.id.row_textview_cloud, wd.getCloudCoverString());
            rvsRow.setImageViewResource(R.id.row_imageview_condition, wd.getIconR());
            // rvsRow.setTextViewText(R.id.row_textview_condition, wd.getCondition());
            if(wd.isPrecipIntense()) {
                rvsRow.setTextColor(R.id.row_textview_precip, ContextCompat.getColor(context, R.color.colorHighPrecipIntensity));
            } else {
                rvsRow.setTextColor(R.id.row_textview_precip, ContextCompat.getColor(context, R.color.colorHighlightText));
            }

        } catch(Exception e){
            Log.e(LOG, "getViewAtError: " + e);
        }

        return rvsRow;
    }


    @Override
    public void onDataSetChanged() {
        try {
            // Log.d(LOG, "onDataSetChanged() starting");
            SharedPreferences prefs = context.getSharedPreferences(PACKAGE, Context.MODE_PRIVATE);
            location = prefs.getString("location", "").replace(" ", "").replace("°", "");
            key = prefs.getString("key", "");
            numItems = prefs.getInt("days", 8);
            apparent = prefs.getBoolean("apparent", false);

            if(key.equals("") || location.equals("")){
                Log.e(LOG, "key or location SharedPref is not valid, returning and not changing data");
                return;
            }

            Log.d(LOG, "onDSC() start with location " + location + ", key " + key + ", days " + numItems);
            // Toast.makeText(context, "Fetching weather for zipcode " + zip, Toast.LENGTH_SHORT).show();
            String url = String.format("https://api.darksky.net/forecast/%s/%s?exclude=minutely,hourly,flags", key, location);
            String result = new JSONTask(url).execute().get(); // waits until finished
            // Log.d(LOG, "full result from JSONTask: " + result);

            if(numItems > 8 || numItems < 1){
                Log.e(LOG, "numItems SharedPref is not valid, forcing value to 8 items");
                numItems = 8;
            }

            String tempHName = "temperatureHigh";
            String tempLName = "temperatureLow";
            if(apparent){ // show the "apparent" temperature returned by the api instead of the real temperature
                tempHName = "apparentTemperatureHigh";
                tempLName = "apparentTemperatureLow";
            }

            JSONObject data = new JSONObject(result);
            JSONArray daily = data.getJSONObject("daily").getJSONArray("data");

            items = new WeatherData[numItems];
            for(int i=0; i<numItems; i++){
                JSONObject day = daily.getJSONObject(i);

                long dateSeconds =  Integer.parseInt(day.getString("time"));
                String icon =                        day.getString("icon");
                float precipProb =   (float)Math.min(day.getDouble("precipProbability"), 0.99); // don't allow 100% because the spacing causes wrapping
                float precipIntensity =       (float)day.getDouble("precipIntensityMax");
                float tempH =                 (float)day.getDouble(tempHName); // depends on apparent temperature user setting
                float tempL =                 (float)day.getDouble(tempLName);
                float cloudCover =   (float)Math.min(day.getDouble("cloudCover"), 0.99);
                // String summary = day.getString("summary"); // unused
                // float humidity = (float)day.getDouble("humidity"); // unused
                // float windSpeed = (float)day.getDouble("windSpeed"); // unused
                items[i] = new WeatherData(dateSeconds, icon, precipProb, precipIntensity, tempH, tempL, cloudCover);
            }

        } catch(InterruptedException | ExecutionException | JSONException error) {
            Log.e(LOG, "onDataSetChanged() error: " + error);
        }

    }

    @Override
    public void onCreate() {}
    @Override
    public void onDestroy() {}
    @Override
    public int getCount() {
        return numItems;
    }
    @Override
    public RemoteViews getLoadingView() {
        return null;
    }
    @Override
    public int getViewTypeCount() {
        return 1;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public boolean hasStableIds() {
        return true;
    }

}

