package com.ig2i.thegreenmagpie;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TransactionDetailsActivity extends Activity {
    TextView dateTextView;
    TextView amountTextView;
    TextView typeTextView;
    TextView fromTextView;
    TextView toTextView;
    TextView returnTextView;
    ImageView returnImg;

    private void initViewElements(){
        dateTextView = (TextView) findViewById(R.id.transactionDetailsDateValue);
        amountTextView = (TextView) findViewById(R.id.transactionDetailsAmountValue);
        typeTextView = (TextView) findViewById(R.id.transactionDetailsTypeValue);
        fromTextView = (TextView) findViewById(R.id.transactionDetailsFromValue);
        toTextView = (TextView) findViewById(R.id.transactionDetailsToValue);
        returnTextView = (TextView) findViewById(R.id.returnTextTransactionDetails);
        returnImg = (ImageView) findViewById(R.id.returnImgTransactionDetails);

        returnTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        returnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void fillTextView(Transaction transaction){
        dateTextView.setText(transaction.getDate());
        amountTextView.setText(String.valueOf(transaction.getAmount()));
        typeTextView.setText(transaction.getType().toString());
        fromTextView.setText(transaction.getSellerEmail());
        toTextView.setText(transaction.getClientEmail());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);

        initViewElements();

        Transaction transaction = (Transaction) getIntent().getSerializableExtra("transaction");

        fillTextView(transaction);
    }
}
