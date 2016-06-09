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
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
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
import com.ig2i.thegreenmagpie.buyer.TransactionDetectionActivity;

import java.io.IOException;
import java.nio.charset.Charset;

public class SellerWaitingTransactionActivity extends Activity implements View.OnClickListener{
    public static final String TAG = "NFCLog";
    public static final String TYPE_MIME = "application/com.ig2i.thegreenmagpie";

    private NfcAdapter mNfcAdapter;
    private Transaction transaction;
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

        this.NFCInfoDisplay = (TextView) findViewById(R.id.textView15);

        this.transaction = (Transaction) getIntent().getSerializableExtra("Transaction");

        this.mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String nfcMessage = "seller;type:" + this.transaction.getType().toString() + ";montant:" + String.valueOf(this.transaction.getAmount());

        // When an NFC tag comes into range, call the main activity which handles writing the data to the tag

        Intent nfcIntent = new Intent(this.getApplicationContext(), this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        nfcIntent.putExtra("nfcMessage", nfcMessage);
        PendingIntent pi = PendingIntent.getActivity(this.getApplicationContext(), 0, nfcIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);

        this.mNfcAdapter.enableForegroundDispatch(this, pi, new IntentFilter[] {tagDetected}, null);

        Log.e(TAG, "activation foreground dispatch");
    }

    @Override
    protected void onPause() {
        this.mNfcAdapter.disableForegroundDispatch(this);

        super.onPause();
    }

    @Override
    public void onNewIntent(Intent intent) {
        // When an NFC tag is being written, call the write tag function when an intent is
        // received that says the tag is within range of the device and ready to be written to
        if (attente == 0) {
            Log.e(TAG, "detection TAG");
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
                Log.e(TAG, "detection NDEF");
                String type = intent.getType();
                if (TYPE_MIME.equals(type)) {
                    Log.e(TAG, "detection GreenMagpie NDEF");
                    Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                    String nfcMessage = intent.getStringExtra("nfcMessage");
                    if (nfcMessage != null) {
                        this.NFCInfoDisplay.setText(R.string.seller_writing_transaction_text);
                        if (writeTag(this.getApplicationContext(), tag, nfcMessage)) {
                            attente = 1;
                        }
                    }
                } else {
                    Log.d(TAG, "Wrong mime type: " + type);
                }
            }
        }
        else if (attente == 1) {
            // Réception de la confirmation
            Log.e(TAG, "detection TAG pour confirmation");
            String action = intent.getAction();
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
                Log.e(TAG, "detection NDEF pour confirmation");
                String type = intent.getType();
                if (TYPE_MIME.equals(type)) {
                    Log.e(TAG, "detection GreenMagpie NDEF pour confirmation");
                    Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                    new NdefReaderTask().execute(tag, NFCAction.ReceptionMsg, this);
                } else {
                    Log.d(TAG, "Wrong mime type: " + type);
                }
            }
        }
    }

    public boolean writeTag(Context context, Tag tag, String data) {
        // Record to launch Play Store if app is not installed
        NdefRecord appRecord = NdefRecord.createApplicationRecord(context.getPackageName());

        // Record with actual data we care about
        NdefRecord relayRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                new String(TYPE_MIME)
                        .getBytes(Charset.forName("US-ASCII")),
                null, data.getBytes());

        // Complete NDEF message with both records
        NdefMessage message = new NdefMessage(new NdefRecord[] {relayRecord, appRecord});

        try {
            // If the tag is already formatted, just write the message to it
            Ndef ndef = Ndef.get(tag);
            if(ndef != null) {
                ndef.connect();

                // Make sure the tag is writable
                if(!ndef.isWritable()) {
                    Log.e(TAG, "NDEF en lecture");
                    return false;
                }

                // Check if there's enough space on the tag for the message
                int size = message.toByteArray().length;
                if(ndef.getMaxSize() < size) {
                    Log.e(TAG, "Tag trop long");
                    return false;
                }

                try {
                    // Write the data to the tag
                    ndef.writeNdefMessage(message);
                    this.NFCInfoDisplay.setText(R.string.seller_transaction_done_text);
                    Log.e(TAG,"message écrit");
                    return true;
                } catch (TagLostException tle) {
                    Log.e("","");
                    return false;
                } catch (IOException ioe) {
                    Log.e("","");
                    return false;
                } catch (FormatException fe) {
                    Log.e("","");
                    return false;
                }
                // If the tag is not formatted, format it with the message
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if(format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        this.NFCInfoDisplay.setText(R.string.seller_transaction_done_text);
                        Log.e(TAG,"message écrit");
                        return true;
                    } catch (TagLostException tle) {
                        Log.e("","");
                        return false;
                    } catch (IOException ioe) {
                        Log.e("","");
                        return false;
                    } catch (FormatException fe) {
                        Log.e("","");
                        return false;
                    }
                } else {
                    Log.e(TAG,"Formatage du NDEF échoué");
                    return false;
                }
            }
        } catch(Exception e) {
            Log.e("","");
        }

        return false;
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
                                    dialog.dismiss();
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

    @Override
    public void onClick(View v) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("Transaction", transaction);
        setResult(RESULT_OK,returnIntent);
        finish();
    }
}
