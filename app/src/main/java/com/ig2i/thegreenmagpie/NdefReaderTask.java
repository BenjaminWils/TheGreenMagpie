package com.ig2i.thegreenmagpie;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.ig2i.thegreenmagpie.buyer.BuyerHomepageActivity;
import com.ig2i.thegreenmagpie.seller.SellerWaitingTransactionActivity;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Created by 10081923 on 18/05/2016.
 */
public class NdefReaderTask extends AsyncTask<Object, Void, String> {
    private NFCAction action;
    private Class activityToStart;
    private BuyerHomepageActivity sourceActivity;
    private SellerWaitingTransactionActivity sourceActivity2;

    @Override
    protected String doInBackground(Object... params) {
        Tag tag = (Tag) params[0];
        this.action = (NFCAction) params[1];

        switch (action) {
            case StartActivity:
                this.activityToStart = (Class) params[2];
                this.sourceActivity = (BuyerHomepageActivity) params[3];
                break;
            case ReceptionMsg:
                this.sourceActivity2 = (SellerWaitingTransactionActivity) params[2];
                break;
        }

        Ndef ndef = Ndef.get(tag);
        if (ndef == null) {
            // NDEF is not supported by this Tag.
            return null;
        }

        NdefMessage ndefMessage = ndef.getCachedNdefMessage();

        NdefRecord[] records = ndefMessage.getRecords();
        for (NdefRecord ndefRecord : records) {
            if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                try {
                    return readText(ndefRecord);
                } catch (UnsupportedEncodingException e) {
                    Log.e(sourceActivity.TAG, "Unsupported Encoding", e);
                }
            }
        }

        return null;
    }

    private String readText(NdefRecord record) throws UnsupportedEncodingException {
        byte[] payload = record.getPayload();

        String textEncoding;
        if ((payload[0] & 128) == 0) {
            textEncoding = "UTF-8";
        }
        else {
            textEncoding = "UTF-16";
        }

        int languageCodeLength = payload[0] & 0063;

        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            switch (action) {
                case StartActivity:
                    Intent intent = new Intent(sourceActivity, activityToStart);
                    intent.putExtra("nfcMsg", result);
                    sourceActivity.startActivity(intent);
                    break;
                case ReceptionMsg:
                    sourceActivity2.traitementConfirmation(result);
                    break;
            }
        }
    }
}
