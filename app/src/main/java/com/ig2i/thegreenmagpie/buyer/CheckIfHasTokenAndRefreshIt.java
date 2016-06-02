package com.ig2i.thegreenmagpie.buyer;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.ig2i.thegreenmagpie.PaypalInfo;
import com.ig2i.thegreenmagpie.ServerInfo;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;

import java.io.IOException;
import java.math.BigDecimal;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by qlammens on 01/06/16.
 */
public class CheckIfHasTokenAndRefreshIt extends AsyncTask<String, String, String> {
    public interface AsyncResponse {
        void CheckIfHasTokenIsFinish(String output);
    }

    public AsyncResponse delegate = null;

    public CheckIfHasTokenAndRefreshIt(AsyncResponse delegate){
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
                .url("http://" + ServerInfo.IpAddress + "/ig2i/android/greenMagpie/checkIfHasTokenAndRefreshIt.php")
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
            Log.d("responseServer", requestResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return requestResponse;
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.CheckIfHasTokenIsFinish(result);
    }
}
