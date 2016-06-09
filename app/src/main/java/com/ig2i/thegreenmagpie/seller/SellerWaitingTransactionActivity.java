package com.ig2i.thegreenmagpie.seller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
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
import android.os.Parcelable;
import android.support.annotation.BoolRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ig2i.thegreenmagpie.MainActivity;
import com.ig2i.thegreenmagpie.NFCAction;
import com.ig2i.thegreenmagpie.NdefReaderTask;
import com.ig2i.thegreenmagpie.Operation;
import com.ig2i.thegreenmagpie.R;
import com.ig2i.thegreenmagpie.Transaction;
import com.ig2i.thegreenmagpie.buyer.BuyerHomepageActivity;
import com.ig2i.thegreenmagpie.buyer.TransactionDetectionActivity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Locale;

public class SellerWaitingTransactionActivity extends Activity implements View.OnClickListener, NfcAdapter.CreateNdefMessageCallback,NfcAdapter.OnNdefPushCompleteCallback{
    public static final String TAG = "NFCLog";

    private NfcAdapter mNfcAdapter;
    private Transaction transaction;
    private ArrayList<Transaction> transactions;
    private TextView NFCInfoDisplay;
    private ImageView cancelButton;
    private TextView cancelText;

    public String reponse;

    private int attente = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_waiting_transaction);

        this.cancelButton = (ImageView) findViewById(R.id.returnView);
        this.cancelText = (TextView) findViewById(R.id.textView7);

        this.cancelButton.setOnClickListener(this);
        this.cancelText.setOnClickListener(this);

        this.attente = 0;

        this.NFCInfoDisplay = (TextView) findViewById(R.id.textView15);

        this.transaction = (Transaction) getIntent().getSerializableExtra("Transaction");
        this.transactions = new ArrayList<>();

        // Défini un message à envoyer par NFC
        this.mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        this.mNfcAdapter.setNdefPushMessageCallback(this, this);
        this.mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupForegroundDispatch(this, mNfcAdapter);
    }

    @Override
    protected void onPause() {
        stopForegroundDispatch(this, mNfcAdapter);
        super.onPause();
    }

    @Override
    public void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        // When an NFC tag is being written, call the write tag function when an intent is
        // received that says the tag is within range of the device and ready to be written to
        if (attente == 1) {
            // Réception de la confirmation
            ArrayList<String> msgs = new ArrayList<>();
            String action = intent.getAction();
            if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
                Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                NdefMessage[] messages;
                if (rawMsgs != null) {
                    messages = new NdefMessage[rawMsgs.length];
                    for (int i = 0; i < rawMsgs.length; i++) {
                        messages[i] = (NdefMessage) rawMsgs[i];
                    }

                    for (NdefMessage msg : messages) {
                        for (int j = 0; j < msg.getRecords().length; j++) {
                            NdefRecord record = msg.getRecords()[j];
                            byte[] id = record.getId();
                            short tnf = record.getTnf();
                            byte[] type = record.getType();
                            try {
                                msgs.add(getTextData(record.getPayload()));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    String nfcMsg = "";
                    for (String msg : msgs) {
                        nfcMsg += msg;
                    }
                    traitementConfirmation(nfcMsg);
                }
            }
        }
    }

    /**
     * @param activity The corresponding {@link BuyerHomepageActivity} requesting the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public void setupForegroundDispatch(SellerWaitingTransactionActivity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);

        adapter.enableForegroundDispatch(activity, pendingIntent, new IntentFilter[] {tagDetected}, null);
    }

    /**
     * @param activity The corresponding {@link AppCompatActivity} requesting to stop the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public void stopForegroundDispatch(SellerWaitingTransactionActivity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    String getTextData(byte[] payload) throws UnsupportedEncodingException {
        String texteCode = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
        int langageCodeTaille = payload[0] & 0063;
        return new String(payload, langageCodeTaille + 1, payload.length - langageCodeTaille - 1, texteCode);
    }

    public void traitementConfirmation(String result) {
        if (result != null) {
            try {
                result = result.split(";")[1].split(":")[1];
                if (Boolean.parseBoolean(result)) {
                    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setTitle("Transaction");
                    alertDialog.setMessage("Acceptée !");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    transactions.add(transaction);
                                    dialog.dismiss();
                                    Intent returnIntent = new Intent();
                                    returnIntent.putExtra("Transactions", transactions);
                                    setResult(RESULT_OK,returnIntent);
                                    finish();
                                }
                            });
                    alertDialog.show();
                }
                else {
                    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setTitle("Transaction");
                    alertDialog.setMessage("Refusée !");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent returnIntent = new Intent();
                                    returnIntent.putExtra("Transactions", transactions);
                                    setResult(RESULT_OK,returnIntent);
                                    finish();
                                }
                            });
                    alertDialog.show();
                }
                attente = 0;
            }
            catch (Exception e) {
                e.printStackTrace();
                attente = 0;
            }
        }
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

    @Override
    public void onClick(View v) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("Transactions", transactions);
        setResult(RESULT_OK,returnIntent);
        finish();
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {
        mHandler.obtainMessage(1).sendToTarget();
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        NdefRecord record = creerRecord("seller;type:" + this.transaction.getType().toString() + ";montant:" + String.valueOf(this.transaction.getAmount()));
        NdefMessage message = creerMessage(record);
        return message;
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    attente = 1;
                    break;
            }
        }
    };
}
