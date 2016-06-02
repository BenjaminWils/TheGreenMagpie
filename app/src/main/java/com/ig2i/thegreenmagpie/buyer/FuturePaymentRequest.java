package com.ig2i.thegreenmagpie.buyer;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.ig2i.thegreenmagpie.ServerInfo;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;

import java.io.IOException;
import java.net.URL;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by qlammens on 18/05/16.
 */
public class FuturePaymentRequest extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... params) {
        String requestResponse = "";
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("authorizationCode", params[0])
                .add("email", params[1])
                .build();
        Request request = new Request.Builder()
                .url("http://" + ServerInfo.IpAddress + "/ig2i/android/greenMagpie/authorizationFuturePayment.php")
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
}

