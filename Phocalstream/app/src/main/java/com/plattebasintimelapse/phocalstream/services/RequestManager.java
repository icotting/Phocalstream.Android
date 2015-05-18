package com.plattebasintimelapse.phocalstream.services;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ZachChristensen on 5/15/15.
 */
public class RequestManager {

    public RequestManager() { }

    protected static String[] Get_Connection(String urlString) {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlString);

            urlConnection = (HttpURLConnection) url.openConnection();

            BufferedReader reader;
            if (urlConnection.getResponseCode() == 200) {
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
            }

            return new String[] {String.valueOf(urlConnection.getResponseCode()), readStream(reader)};
        } catch (IOException ex) {
            return new String[] {"-1", "Error."};
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private static String readStream(BufferedReader reader) throws IOException{
        StringBuilder sb = new StringBuilder();

        String line = "";
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();

        return sb.toString();
    }

    public static Bitmap downloadBitmap(String urlString) {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();

            Bitmap bitmap = null;
            if (urlConnection.getResponseCode() == 200) {
                bitmap = BitmapFactory.decodeStream(urlConnection.getInputStream());
                return bitmap;
            }
        } catch (Exception e) {
            // You Could provide a more explicit error message for IOException
            Log.e("RequestManager", "Something went wrong while" +
                    " retrieving bitmap from " + urlString + e.toString());
        }

        return null;
    }
}
