package com.ig2i.thegreenmagpie.seller;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ig2i.thegreenmagpie.R;
import com.ig2i.thegreenmagpie.buyer.BuyerConnectionActivity;
import com.paypal.android.sdk.payments.PaymentActivity;

public class SellerConnectionActivity extends Activity {
    private Button buyerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_connection);

        buyerButton = (Button) findViewById(R.id.button7);

        buyerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), BuyerConnectionActivity.class);
                startActivity(intent);
            }
        });
    }
}
