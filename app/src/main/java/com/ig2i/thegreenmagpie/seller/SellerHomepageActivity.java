package com.ig2i.thegreenmagpie.seller;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ig2i.thegreenmagpie.HistoryActivity;
import com.ig2i.thegreenmagpie.R;
import com.ig2i.thegreenmagpie.Seller;
import com.ig2i.thegreenmagpie.Transaction;

public class SellerHomepageActivity extends Activity {
    private Button transactionBtn;
    private Button historyBtn;
    private TextView settingsText;
    private ImageView settingsImg;
    private ImageView returnView;
    private TextView returnTxt;
    private Seller seller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_homepage);

        this.returnView = (ImageView) findViewById(R.id.returnView);
        this.returnTxt = (TextView) findViewById(R.id.textView7);
        this.transactionBtn = (Button) findViewById(R.id.button11);
        this.historyBtn = (Button) findViewById(R.id.button12);
        this.settingsText = (TextView) findViewById(R.id.settingsTextSellerHome);
        this.settingsImg = (ImageView) findViewById(R.id.settingsImgSellerHome);

        this.returnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });
        this.returnTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });

        this.transactionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SellerTransactionInitiationActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        this.historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), HistoryActivity.class);
                intent.putExtra("email", "seller");
                startActivityForResult(intent, 2);
            }
        });

        this.settingsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        this.settingsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
            // Retour de Make a transaction
        }
        else if(requestCode == 2){
            // Retour de History
        }
    }
}
