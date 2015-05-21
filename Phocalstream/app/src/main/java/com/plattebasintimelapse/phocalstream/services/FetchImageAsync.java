package com.plattebasintimelapse.phocalstream.services;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.plattebasintimelapse.phocalstream.managers.RequestManager;

/**
 * Created by ZachChristensen on 5/18/15.
 */
public class FetchImageAsync extends AsyncTask<Integer, Void, Bitmap> {

    private ImageView image;

    public FetchImageAsync(ImageView image) {
        this.image = image;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        this.image.setImageBitmap(bitmap);
    }

    @Override
    protected Bitmap doInBackground(Integer... params) {
        String url = String.format("http://images.plattebasintimelapse.com/api/photo/low/%d", params[0]);
        return RequestManager.downloadBitmap(url);
    }
}
