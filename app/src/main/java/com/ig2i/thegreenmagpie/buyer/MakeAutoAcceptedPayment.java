package com.ig2i.thegreenmagpie.buyer;

import android.os.AsyncTask;
import android.util.Log;

import com.ig2i.thegreenmagpie.PaypalInfo;
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

/*
curl 'https://api.paypal.com/v1/payments/payment' \
    -H "Content-Type: application/json" \
    -H "PayPal-Client-Metadata-Id: c2edbd6e97b14ff2b19ddb8eec9d264c" \
    -H "Authorization: Bearer WfXdnxmyJtdF4q59ofxuQuAAk6eEV-Njm6puht3Nk3w" \
    -d '{
           "intent":"authorize",
           "payer":{
              "payment_method":"paypal"
           },
           "transactions":[
              {
                 "amount":{
                    "currency":"USD",
                    "total":"1.88"
                 },
                 "description":"future of sauces"
              }
           ]
        }'
 */

public class MakeAutoAcceptedPayment extends AsyncTask<String, String, String> {
    public interface AsyncResponse {
        void AutoAcceptedPaymentIsFinished(String output);
    }

    public AsyncResponse delegate = null;

    public MakeAutoAcceptedPayment(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... params) {
        String requestResponse = "";
        String accessToken = params[0];
        String paymentAmount = params[1];
        String clientMetadataId = params[2];
        String data = "{\"intent\": \"sale\", " +
                    "\"payer\": {\"payment_method\": \"paypal\"}, " +
                    "\"transactions\": [{" +
                        "\"amount\": {\"currency\": \"USD\", \"total\": \""+paymentAmount+"\"}," +
                        "\"description\": \"The Green Magpie\"" +
                    "}]}";
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, data);
        Request request = new Request.Builder()
                .url("https://api.sandbox.paypal.com/v1/payments/payment")
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("PayPal-Client-Metadata-Id", clientMetadataId)
                .addHeader("Authorization", "Bearer " + accessToken)
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
        Log.d("response", requestResponse);
        return requestResponse;
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.AutoAcceptedPaymentIsFinished(result);
    }
}
