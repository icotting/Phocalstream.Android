package com.plattebasintimelapse.phocalstream.managers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ZachChristensen on 5/15/15.
 */
public class RequestManager {

    public RequestManager() { }

    public static String[] Get_Connection(String urlString) {
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

    public static String[] uploadImage(String url_string, String filePath, String token){

        HttpURLConnection conn = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";

        try {
            // open a URL connection to the Servlet
            FileInputStream fileInputStream = new FileInputStream(new File(filePath.split(":")[1]) );
            URL url = new URL(url_string);

            // Open a HTTP connection to the URL
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);

            // Use a post method.
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
//            conn.setRequestProperty("Authorization", "Bearer " + token);

            // Image
            DataOutputStream dos = new DataOutputStream( conn.getOutputStream() );
            dos.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"image\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes("Image Upload");

            dos.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"photo\";filename=\"photo.jpg\"" + lineEnd);
            dos.writeBytes("Content-Type: application/octet-stream"+lineEnd+lineEnd);

            // create a buffer of maximum size
            int bytesAvailable = fileInputStream.available();
            int maxBufferSize = 1*1024*1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];

            // read file and write it into form...
            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            // send multipart form data necesssary after <span id="IL_AD12" class="IL_AD">file data</span>...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // close streams
            fileInputStream.close();
            dos.flush();
            dos.close();
        }
        catch (IOException ioe) {
            Log.e("Phocalstream", "error: " + ioe.getMessage(), ioe);
        }

        try {
            return new String[] {String.valueOf( conn.getResponseCode()), "Upload Successful."};
        }
        catch (Exception e) {
            Log.e("Phocalstream", "Exception : " + e.getMessage(), e);
            return new String[] {"-1", "Error"};
        }
    }
}
