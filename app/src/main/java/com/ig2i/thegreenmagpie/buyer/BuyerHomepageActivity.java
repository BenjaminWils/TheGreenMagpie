package com.ig2i.thegreenmagpie.buyer;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
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

public class BuyerHomepageActivity extends Activity {
    public static final String TYPE_MIME = "application/com.ig2i.thegreenmagpie";
    public static final String TAG = "NFCLog";

    private TextView balanceAmount;
    private Button manageBalanceButton;
    private ImageView logoutButton;
    private ObjectPreference objectPreference;
    private User currentUser;

    private NfcAdapter mNfcAdapter;

    private void initViewElements(){
        balanceAmount = (TextView) findViewById(R.id.textView8);
        manageBalanceButton = (Button) findViewById(R.id.button8);
        logoutButton = (ImageView) findViewById(R.id.returnView);

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
                Intent intent = new Intent(getBaseContext(), BuyerConnectionActivity.class);
                startActivity(intent);
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
            Toast.makeText(this, "@string/explanation", Toast.LENGTH_LONG).show();
        }

        initViewElements();

        objectPreference = (ObjectPreference) this.getApplication();
        ComplexPreferences complexPreferences = objectPreference.getComplexPreference();

        currentUser = complexPreferences.getObject("user", User.class);
        balanceAmount.setText("$" + String.valueOf(currentUser.getBalance()));
    }

    @Override
    protected void onResume(){
        super.onResume();
        objectPreference = (ObjectPreference) this.getApplication();
        ComplexPreferences complexPreferences = objectPreference.getComplexPreference();
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
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            if (TYPE_MIME.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag, NFCAction.StartActivity, TransactionDetectionActivity.class, this);

            } else {
                Log.d(TAG, "Wrong mime type: " + type);
            }
        }
    }

    /**
     * @param activity The corresponding {@link BuyerHomepageActivity} requesting the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void setupForegroundDispatch(final BuyerHomepageActivity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);

        adapter.enableForegroundDispatch(activity, pendingIntent, new IntentFilter[] {tagDetected}, techList);
    }

    /**
     * @param activity The corresponding {@link AppCompatActivity} requesting to stop the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void stopForegroundDispatch(final BuyerHomepageActivity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }
}
