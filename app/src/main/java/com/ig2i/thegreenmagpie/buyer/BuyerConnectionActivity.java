package com.ig2i.thegreenmagpie.buyer;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ig2i.thegreenmagpie.R;
import com.ig2i.thegreenmagpie.seller.SellerConnectionActivity;

public class BuyerConnectionActivity extends Activity {
    private Button sellerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_connection);

        sellerButton = (Button) findViewById(R.id.button7);

        sellerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SellerConnectionActivity.class);
                startActivity(intent);
            }
        });
    }
}
