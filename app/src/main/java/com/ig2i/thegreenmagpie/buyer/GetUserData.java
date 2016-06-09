package com.ig2i.thegreenmagpie.buyer;

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
 * Created by qlammens on 05/06/16.
 */
public class GetUserData extends AsyncTask<String, String, String> {
    public interface AsyncResponse {
        void getUserDataIsFinished(String output);
    }

    public AsyncResponse delegate = null;

    public GetUserData(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... params) {
        String requestResponse = "";
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("email", params[0])
                .build();
        Request request = new Request.Builder()
                .url(ServerInfo.GetUserURL)
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return requestResponse;
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.getUserDataIsFinished(result);
    }
}
