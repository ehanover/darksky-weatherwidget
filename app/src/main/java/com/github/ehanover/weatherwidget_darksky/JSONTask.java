package com.github.ehanover.weatherwidget_darksky;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class JSONTask extends AsyncTask<String, Void, String> {

    static String LOG = "ASDF";
    private String urlString;

    public JSONTask(String url){
        //Log.d(LOG, "JSONTask constructor");
        this.urlString = url;
    }

    protected String doInBackground(String... text){
        // https://www.tanelikorri.com/tutorial/android/http-request-tutorial/
        try {
            //Log.d(LOG, "JSONTask doInBackground start");
            //URL url = new URL(text[0]);
            Log.d(LOG, "doInBackground() with url: " + urlString);
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            // connection.setRequestProperty("Access-Control-Allow-Origin", "*"); // needed to avoid CORS 405 error?
            // connection.setRequestProperty("crossorigin", "anonymous");
            connection.setDoOutput(false);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e(LOG, "JSONTask response code not OK, got " + connection.getResponseCode());
            }

            InputStream is = connection.getInputStream();
            //String content = convertInputStream(is, "UTF-8");
            String content = convertInputStream(is);
            is.close();

            //Log.d(LOG, "JSONTask doInBackground finish. content: " + content);
            return content;
        } catch (IOException error) {
            Log.e(LOG, "JSONTask IOException error: " + error);
        }

        return "error";
    }

    protected void onPreExecute(){
        //Log.d("ASDF", "JSONTask onPreExecute");
    }

    protected void onPostExecute(String result) {
        //Log.d(LOG, "JSONTask onPostExecute");
    }

    private String convertInputStream(InputStream is) {
        Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

}



/*
    private static class WeatherDataTask extends AsyncTask<String, Void, String> {
        AppWidgetManager awm;
        RemoteViews rvs;
        int wid;

        WeatherDataTask(AppWidgetManager awm, RemoteViews rvs, int wid){
            this.awm = awm;
            this.rvs = rvs;
            this.wid = wid;
        }

        protected String doInBackground(String... text){ // https://www.tanelikorri.com/tutorial/android/http-request-tutorial/
            try {
                URL url = new URL(text[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoOutput(true);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();

                if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(LOG, "response code is not good");
                }

                InputStream is = connection.getInputStream();
                String content = convertInputStream(is, "UTF-8");
                is.close();

                //Log.d(LOG, "doinbackground finished. result: " + content);

                return content;
            } catch (IOException error) {
                Log.e(LOG, "IOException error in WeatherDataTask: " + error);
            }

            return "error";
        }

        protected void onPreExecute(){
            Log.d(LOG, "pre execute asynctask");
        }

        protected void onPostExecute(String result) {
            try {
                //Log.d(LOG, "post execute asynctask. result: " + result);
                JSONArray data = new JSONObject(result).getJSONArray("list");

                for(int i=0; i<5; i++){
                    JSONObject day = data.getJSONObject(i);
                    JSONObject main = day.getJSONObject("main");

                    long dateSeconds = Integer.parseInt(day.getString("dt"));
                    Date date = new Date(dateSeconds*1000);

                    double temp = main.getDouble("temp");
                    int clouds = day.getJSONObject("clouds").getInt("all");
                    String condition = day.getJSONArray("weather").getJSONObject(0).getString("description");

                    //Log.d(LOG, "condition on " + i + ", " + sdfWeekly.format(date) + ": " + condition);

                    //String updateTime = "Last updated " + sdfUpdated.format(new Date());
                    //rvs.setTextViewText(R.id.textView_updatetime, updateTime);




                    //awm.updateAppWidget(wid, rvs);
                }



            } catch(JSONException error) {
                Log.e(LOG, "jsonexception in onpostexecute: " + error);
            }
        }

        private String convertInputStream(InputStream is, String encoding) {
            Scanner scanner = new Scanner(is, encoding).useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }

    }
*/