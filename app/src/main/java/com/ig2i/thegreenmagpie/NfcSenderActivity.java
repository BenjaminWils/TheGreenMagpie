package com.ig2i.thegreenmagpie;

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
import android.widget.Toast;

import java.io.IOException;
import java.nio.charset.Charset;

public class NfcSenderActivity extends AppCompatActivity {
    private NfcAdapter mNfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_sender);

        String nfcMessage = "";

        // When an NFC tag comes into range, call the main activity which handles writing the data to the tag
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        Intent nfcIntent = new Intent(this.getApplicationContext(), this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        nfcIntent.putExtra("nfcMessage", nfcMessage);
        PendingIntent pi = PendingIntent.getActivity(this.getApplicationContext(), 0, nfcIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);

        nfcAdapter.enableForegroundDispatch(this, pi, new IntentFilter[] {tagDetected}, null);
    }

    @Override
    public void onNewIntent(Intent intent) {
        // When an NFC tag is being written, call the write tag function when an intent is
        // received that says the tag is within range of the device and ready to be written to
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String nfcMessage = intent.getStringExtra("nfcMessage");

        if(nfcMessage != null) {
            writeTag(this.getApplicationContext(), tag, nfcMessage);
        }
    }

    public static boolean writeTag(Context context, Tag tag, String data) {
        // Record to launch Play Store if app is not installed
        NdefRecord appRecord = NdefRecord.createApplicationRecord(context.getPackageName());

        // Record with actual data we care about
        NdefRecord relayRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                new String("application/" + context.getPackageName())
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
                    Log.e("","");
                    return false;
                }

                // Check if there's enough space on the tag for the message
                int size = message.toByteArray().length;
                if(ndef.getMaxSize() < size) {
                    Log.e("","");
                    return false;
                }

                try {
                    // Write the data to the tag
                    ndef.writeNdefMessage(message);

                    Log.e("","");
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

                        Log.e("","");
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
                    Log.e("","");
                    return false;
                }
            }
        } catch(Exception e) {
            Log.e("","");
        }

        return false;
    }
}
