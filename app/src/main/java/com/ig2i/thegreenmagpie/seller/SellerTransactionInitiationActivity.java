package com.ig2i.thegreenmagpie.seller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.nfc.NfcAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ig2i.thegreenmagpie.Operation;
import com.ig2i.thegreenmagpie.R;
import com.ig2i.thegreenmagpie.Transaction;

import java.util.ArrayList;

public class SellerTransactionInitiationActivity extends Activity implements Button.OnClickListener{
    private Button goButton;
    private EditText amountEditTxt;
    private TextView NFCTxt;
    private ImageView NFCIcon;
    private ImageView returnView;
    private TextView returnTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_transaction_initiation);

        this.goButton = (Button) findViewById(R.id.button13);
        this.amountEditTxt = (EditText) findViewById(R.id.editText3);
        this.NFCTxt = (TextView) findViewById(R.id.textView14);
        this.NFCIcon = (ImageView) findViewById(R.id.imageView2);
        this.returnView = (ImageView) findViewById(R.id.returnView);
        this.returnTxt = (TextView) findViewById(R.id.textView7);

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

        NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            this.NFCTxt.setText(R.string.nfc_unpresent);
            this.NFCIcon.setImageResource(R.drawable.x_icon_bg);
            this.goButton.setEnabled(false);
        }

        if (!mNfcAdapter.isEnabled()) {
            this.NFCTxt.setText(R.string.nfc_disabled);
            this.NFCIcon.setImageResource(R.drawable.x_icon_bg);
            this.goButton.setEnabled(false);
        } else {
            this.NFCTxt.setText(R.string.nfc_enabled);
            this.NFCIcon.setImageResource(R.drawable.v_icon_bg);
            this.goButton.setEnabled(true);
        }

        this.goButton.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
            ArrayList<Transaction> transactions = (ArrayList<Transaction>) data.getSerializableExtra("Transactions");
            if (transactions.size() > 0) {
                Transaction transac = transactions.get(0);
                this.amountEditTxt.setText(String.valueOf(transac.getAmount()));
            }
        }
    }

    @Override
    public void onClick(View v) {
        double montant = Double.parseDouble(amountEditTxt.getText().toString());
        if (montant != 0) {
            Intent intent = new Intent(this, SellerWaitingTransactionActivity.class);
            Transaction transac = new Transaction(Operation.SPENDING, montant);
            intent.putExtra("Transaction", transac);
            startActivityForResult(intent, 1);
        }
    }
}
