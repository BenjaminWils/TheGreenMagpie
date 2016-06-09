package com.ig2i.thegreenmagpie;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by qlammens on 08/06/16.
 */
public class GetHistory extends AsyncTask<String, String, String> {
    public interface AsyncResponse {
        void GetHistoryIsFinished(String output);
    }

    public AsyncResponse delegate = null;

    public GetHistory(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... params) {
        String requestResponse = "";
        String email = params[0];
        String limit = params[1];
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("email", email)
                .add("limit", limit)
                .build();
        Request request = new Request.Builder()
                .url("http://" + ServerInfo.IpAddress + "/ig2i/android/greenMagpie/getHistory.php")
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
        delegate.GetHistoryIsFinished(result);
    }
}
