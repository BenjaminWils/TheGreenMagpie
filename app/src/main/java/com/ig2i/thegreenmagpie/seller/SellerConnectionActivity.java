package com.ig2i.thegreenmagpie.seller;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ig2i.thegreenmagpie.R;
import com.ig2i.thegreenmagpie.buyer.BuyerConnectionActivity;
import com.paypal.android.sdk.payments.PaymentActivity;

public class SellerConnectionActivity extends Activity {
    private Button buyerButton;
    private Button goButton;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_connection);

        buyerButton = (Button) findViewById(R.id.clientBtnSellerConnection);
        goButton = (Button) findViewById(R.id.goBtnSellerConnection);
        passwordEditText = (EditText) findViewById(R.id.passwordSellerConnection);

        buyerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), BuyerConnectionActivity.class);
                startActivity(intent);
            }
        });

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ConnectSeller(new ConnectSeller.AsyncResponse() {
                    @Override
                    public void ConnectSellerIsFinished(String output) {
                        Log.d("output", output);
                        if(("true").equals(output)){
                            Intent intent = new Intent(getBaseContext(), SellerHomepageActivity.class);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(SellerConnectionActivity.this, "Connection failed!", Toast.LENGTH_LONG).show();
                        }
                    }
                }).execute(passwordEditText.getText().toString());
            }
        });
    }
}
