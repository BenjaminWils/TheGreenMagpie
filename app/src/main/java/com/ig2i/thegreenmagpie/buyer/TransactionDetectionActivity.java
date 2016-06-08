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
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ig2i.thegreenmagpie.R;

import java.io.IOException;
import java.nio.charset.Charset;

public class TransactionDetectionActivity extends Activity {
    public static final String TAG = "NFCLog";
    public static final String TYPE_MIME = "application/com.ig2i.thegreenmagpie";

    private NfcAdapter mNfcAdapter;
    private String nfcMessage = "";

    public Button accept;
    public Button decline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detection);

        Bundle extras = getIntent().getExtras();
        String nfcMsg = extras.getString("nfcMsg");

        double montant = Double.parseDouble(nfcMsg.split(";")[2].split(":")[1]);

        this.mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Toast.makeText(this, nfcMsg, Toast.LENGTH_LONG).show();

        accept = (Button) findViewById(R.id.button9);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accept.setEnabled(false);
                decline.setEnabled(false);
                nfcMessage = "buyer;confirmation:true";
            }
        });

        decline = (Button) findViewById(R.id.button10);
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accept.setEnabled(false);
                decline.setEnabled(false);
                nfcMessage = "buyer;confirmation:false";
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent nfcIntent = new Intent(this.getApplicationContext(), this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        nfcIntent.putExtra("nfcMessage", this.nfcMessage);
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
        Log.e(TAG, "detection TAG");
        String nfcMessage = intent.getStringExtra("nfcMessage");
        if (nfcMessage != null && !nfcMessage.equals("")) {
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
                Log.e(TAG, "detection NDEF");
                String type = intent.getType();
                if (TYPE_MIME.equals(type)) {
                    Log.e(TAG, "detection GreenMagpie NDEF");
                    Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                    if (writeTag(this.getApplicationContext(), tag, nfcMessage)) {
                        Intent homepageIntent = new Intent(getBaseContext(), BuyerHomepageActivity.class);
                        startActivity(homepageIntent);
                    }
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
}
