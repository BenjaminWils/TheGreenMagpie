package com.ig2i.thegreenmagpie.buyer;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ig2i.thegreenmagpie.ComplexPreferences;
import com.ig2i.thegreenmagpie.NFCAction;
import com.ig2i.thegreenmagpie.NdefReaderTask;
import com.ig2i.thegreenmagpie.ObjectPreference;
import com.ig2i.thegreenmagpie.R;
import com.ig2i.thegreenmagpie.User;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class BuyerHomepageActivity extends Activity {
    public static final String TYPE_MIME = "application/com.ig2i.thegreenmagpie";
    public static final String TAG = "NFCLog";

    private TextView balanceAmount;
    private Button manageBalanceButton;
    private ImageView logoutButton;
    private TextView logoutTextView;
    private ObjectPreference objectPreference;
    private ComplexPreferences complexPreferences;
    private User currentUser;

    private NfcAdapter mNfcAdapter;

    private void logout(){
        currentUser.setAutoConnect(false);
        complexPreferences.putObject("user", currentUser);
        complexPreferences.commit();
        Intent intent = new Intent(getBaseContext(), BuyerConnectionActivity.class);
        startActivity(intent);
        finish();
    }

    private void initViewElements(){
        balanceAmount = (TextView) findViewById(R.id.textView8);
        manageBalanceButton = (Button) findViewById(R.id.button8);
        logoutButton = (ImageView) findViewById(R.id.returnView);
        logoutTextView = (TextView) findViewById(R.id.textView7);

        manageBalanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), BalanceManagementActivity.class);
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        logoutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_homepage);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
        }

        if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC is disabled.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, R.string.explanation, Toast.LENGTH_LONG).show();
        }

        initViewElements();

        objectPreference = (ObjectPreference) this.getApplication();
        complexPreferences = objectPreference.getComplexPreference();

        currentUser = complexPreferences.getObject("user", User.class);
        balanceAmount.setText("$" + String.valueOf(currentUser.getBalance()));
    }

    @Override
    protected void onResume(){
        super.onResume();
        objectPreference = (ObjectPreference) this.getApplication();
        complexPreferences = objectPreference.getComplexPreference();
        currentUser = complexPreferences.getObject("user", User.class);
        balanceAmount.setText("$"+String.valueOf(currentUser.getBalance()));
        setupForegroundDispatch(this, mNfcAdapter);
    }

    @Override
    protected void onPause() {
        stopForegroundDispatch(this, mNfcAdapter);
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
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
                startTransaction(nfcMsg);
            }
        }
    }

    String getTextData(byte[] payload) throws UnsupportedEncodingException {
        String texteCode = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
        int langageCodeTaille = payload[0] & 0063;
        return new String(payload, langageCodeTaille + 1, payload.length - langageCodeTaille - 1, texteCode);
    }

    public void startTransaction(String nfcMessage) {
        Intent intent = new Intent(this, TransactionDetectionActivity.class);
        intent.putExtra("nfcMsg", nfcMessage);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            double montant = data.getDoubleExtra("montant", 0.00);

            // TODO : Mettre à jour le solde en local
        }
    }

    /**
     * @param activity The corresponding {@link BuyerHomepageActivity} requesting the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public void setupForegroundDispatch(BuyerHomepageActivity activity, NfcAdapter adapter) {
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
    public void stopForegroundDispatch(BuyerHomepageActivity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }
}
