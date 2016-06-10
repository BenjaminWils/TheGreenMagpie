package com.ig2i.thegreenmagpie.buyer;

import android.os.AsyncTask;
import android.util.Log;

import com.ig2i.thegreenmagpie.ServerInfo;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by qlammens on 02/06/16.
 */
public class UpdateBalance extends AsyncTask<String, String, String> {
    public interface AsyncResponse {
        void UpdateBalanceIsFinished(String output);
    }

    public AsyncResponse delegate = null;

    public UpdateBalance(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... params) {
        String requestResponse = "";
        String email = params[0];
        String paymentAmount = params[1];
        Log.d("update", email);
        Log.d("update", paymentAmount);
        //String data = "{\"email\": \""+email+"\", \"amount\":\""+paymentAmount+"\"}";
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("email", email)
                .add("amount", paymentAmount)
                .build();
        Request request = new Request.Builder()
                .url(ServerInfo.UpdateBalanceURL)
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
            Log.d("updateBalRep", requestResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return requestResponse;
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.UpdateBalanceIsFinished(result);
    }
}
