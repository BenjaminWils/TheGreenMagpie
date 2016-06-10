package com.ig2i.thegreenmagpie.seller;

import android.os.AsyncTask;
import android.util.Log;

import com.ig2i.thegreenmagpie.ServerInfo;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by qlammens on 10/06/16.
 */
public class SetSettings extends AsyncTask<String, String, String> {
    public interface AsyncResponse {
        void SetSettingsIsFinished(String output);
    }

    public AsyncResponse delegate = null;

    public SetSettings(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... params) {
        String requestResponse = "";
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("currentPassword", params[0])
                .add("newPassword", params[0])
                .build();
        Request request = new Request.Builder()
                .url(ServerInfo.SetSettingsURL)
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            assert response != null;
            requestResponse = response.body().string();
            Log.d("settings rep", requestResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return requestResponse;
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.SetSettingsIsFinished(result);
    }
}
