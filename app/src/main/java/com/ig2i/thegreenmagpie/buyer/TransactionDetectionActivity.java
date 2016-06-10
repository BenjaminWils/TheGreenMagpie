package com.ig2i.thegreenmagpie.buyer;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ig2i.thegreenmagpie.ComplexPreferences;
import com.ig2i.thegreenmagpie.Loader;
import com.ig2i.thegreenmagpie.ObjectPreference;
import com.ig2i.thegreenmagpie.Operation;
import com.ig2i.thegreenmagpie.R;
import com.ig2i.thegreenmagpie.Transaction;
import com.ig2i.thegreenmagpie.User;
import com.ig2i.thegreenmagpie.seller.SaveTransaction;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

public class TransactionDetectionActivity extends Activity implements NfcAdapter.CreateNdefMessageCallback,NfcAdapter.OnNdefPushCompleteCallback{
    public static final String TAG = "NFCLog";
    public static final String TYPE_MIME = "application/com.ig2i.thegreenmagpie";

    private NfcAdapter mNfcAdapter;
    private TextView amountView;
    private String nfcMessage = "";
    private double montant;
    private int action;

    public Button accept;
    public Button decline;

    private ObjectPreference objectPreference;
    private ComplexPreferences complexPreferences;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detection);

        Bundle extras = getIntent().getExtras();
        String nfcMsg = extras.getString("nfcMsg");

        montant = Double.parseDouble(nfcMsg.split(";")[2].split(":")[1]);

        objectPreference = (ObjectPreference) this.getApplication();
        complexPreferences = objectPreference.getComplexPreference();

        currentUser = complexPreferences.getObject("user", User.class);

        this.mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        this.amountView = (TextView) findViewById(R.id.textView11);

        this.amountView.setText(String.valueOf(montant));

        accept = (Button) findViewById(R.id.button9);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accept.setEnabled(false);
                decline.setEnabled(false);
                Loader.start(getBaseContext());
                try {
                    saveTransaction(Float.parseFloat(String.valueOf(montant)));
                }
                catch (Exception e) {
                    e.printStackTrace();
                    action = RESULT_CANCELED;
                }
            }
        });

        decline = (Button) findViewById(R.id.button10);
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accept.setEnabled(false);
                decline.setEnabled(false);
                nfcMessage = "buyer;confirmation:false";
                action = RESULT_CANCELED;
                setNfcPush();
            }
        });

        if (currentUser.getBalance() < montant) {
            accept.setEnabled(false);
            Toast.makeText(this, "Cannot be accepted: the transaction amount exceeds your balance", Toast.LENGTH_LONG).show();
        }
    }

    public void setNfcPush() {
        this.mNfcAdapter.setNdefPushMessageCallback(this, this);
        this.mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
        Loader.setMessage("Waiting for NFC transaction");
        Loader.start(this);
    }

    NdefRecord creerRecord(String message)
    {
        byte[] langBytes = Locale.ENGLISH.getLanguage().getBytes(Charset.forName("US-ASCII"));
        byte[] textBytes = message.getBytes(Charset.forName("UTF-8"));
        char status = (char) (langBytes.length);
        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
    }

    NdefMessage creerMessage(NdefRecord record)
    {
        NdefRecord[] records = new NdefRecord[1];
        records[0] = record;
        NdefMessage message = new NdefMessage(records);
        return message;
    }

    private void saveTransaction(final float amount){
        new SaveTransaction(new SaveTransaction.AsyncResponse() {
            @Override
            public void SaveTransactionIsFinished(String output) {
                try {
                    float newBalance = Float.valueOf(output);
                    nfcMessage = "buyer;confirmation:true";
                    action = RESULT_OK;
                    accept.setEnabled(false);
                }
                catch (Exception e) {
                    nfcMessage = "buyer;confirmation:false";
                    action = RESULT_CANCELED;
                }

                setNfcPush();
                Loader.end();
                if (action == RESULT_CANCELED) {
                    Toast.makeText(getBaseContext(), "The transaction encountered an error", Toast.LENGTH_SHORT).show();
                }
            }
        }).execute(currentUser.getEmail(), String.valueOf(amount));
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        NdefRecord record = creerRecord(this.nfcMessage);
        NdefMessage message = creerMessage(record);
        return message;
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {
        mHandler.obtainMessage(1).sendToTarget();
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Loader.end();
                    Loader.setMessage("Wait while loading...");
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("montant", montant);
                    setResult(action,returnIntent);
                    finish();
                    break;
            }
        }
    };
}
