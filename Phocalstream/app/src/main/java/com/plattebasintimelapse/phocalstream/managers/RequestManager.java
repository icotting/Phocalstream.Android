package com.plattebasintimelapse.phocalstream.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZachChristensen on 5/15/15.
 */
public class RequestManager {

    private Context context;
    private SharedPreferences settings;
    private static java.net.CookieManager msCookieManager;
    private static String COOKIES_HEADER = "Set-Cookie";
    private static String COOKIE_STORE_KEY = "Cookie-Store-Key";

    public RequestManager(Context context) {
        this.context = context;
        settings = this.context.getSharedPreferences("Phocalstream", 0);
        msCookieManager = new java.net.CookieManager();
    }

    public String[] Login(String urlString) {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlString);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Accept", "application/json");

            BufferedReader reader;
            if (urlConnection.getResponseCode() == 200) {
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
            }

            // get the cookie and save it
            Map<String, List<String>> headerFields = urlConnection.getHeaderFields();
            List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);
            if (cookiesHeader != null) {
                for (String cookie : cookiesHeader) {
                    msCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                }
            }
            saveCookies(TextUtils.join(";", msCookieManager.getCookieStore().getCookies()));

            return new String[]{String.valueOf(urlConnection.getResponseCode()), readStream(reader)};
        } catch (IOException ex) {
            return new String[]{"-1", "Error."};
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    public String[] Get_Connection(String urlString) {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlString);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Accept", "application/json");

            if (hasCookie()) {
                urlConnection.setRequestProperty("Cookie", getCookies());
            }

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

    public String[] Post_Connection(String urlString, HashMap<String, String> values) {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlString);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);

            if (hasCookie()) {
                urlConnection.setRequestProperty("Cookie", getCookies());
            }

            String jsonString = new JSONObject(values).toString();
            byte[] outputInBytes = jsonString.getBytes("UTF-8");

            OutputStream out = urlConnection.getOutputStream();
            out.write( outputInBytes );
            out.close();

//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
  //          writer.write(jsonString);


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

    public String[] uploadImage(String url_string, String filePath){

        HttpURLConnection conn = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";

        try {
            // open a URL connection to the Servlet
            FileInputStream fileInputStream = new FileInputStream(new File(filePath) );
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

            if (hasCookie()) {
                conn.setRequestProperty("Cookie", getCookies());
            }


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

            BufferedReader reader;
            if (conn.getResponseCode() == 200) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            return new String[] {String.valueOf(conn.getResponseCode()), readStream(reader)};
        }
        catch (Exception e) {
            Log.e("Phocalstream", "Exception : " + e.getMessage(), e);
            return new String[] {"-1", "Error"};
        }
    }


    // COOKIE MANAGEMENT

    private Boolean hasCookie() {
        String cookie = settings.getString(this.COOKIE_STORE_KEY, "");
        return !cookie.isEmpty();
    }

    private String getCookies() {
        return settings.getString(this.COOKIE_STORE_KEY, "");
    }

    private void saveCookies(String cookies) {
        settings.edit().putString(this.COOKIE_STORE_KEY, cookies).commit();
    }

}
