package com.ig2i.thegreenmagpie.buyer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ig2i.thegreenmagpie.ComplexPreferences;
import com.ig2i.thegreenmagpie.HistoryActivity;
import com.ig2i.thegreenmagpie.Loader;
import com.ig2i.thegreenmagpie.ObjectPreference;
import com.ig2i.thegreenmagpie.PaypalInfo;
import com.ig2i.thegreenmagpie.R;
import com.ig2i.thegreenmagpie.User;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

public class BalanceManagementActivity extends Activity{

    private User currentUser;
    private ObjectPreference objectPreference;
    private ImageView returnButton;
    private TextView returnTextView;
    private TextView balanceAmountTextView;
    private EditText amountEditText;
    private Button addButton;
    private Button autoAcceptButton;
    private Button repaymentButton;
    private Button historyButton;
    private static PayPalConfiguration paypalConfig;
    private static Intent paypalIntent;

    private void showError(){
        Toast.makeText(this, "Une erreur est survenue", Toast.LENGTH_LONG).show();
    }

    private void makeAutoAcceptedPayment(String accessToken){
        new MakeAutoAcceptedPayment(new MakeAutoAcceptedPayment.AsyncResponse() {
            @Override
            public void AutoAcceptedPaymentIsFinished(String output) {
                Log.d("paypalAcceptedPayment", output);
                updateBalance(getPaymentAmount());
                Loader.end();
            }
        }).execute(accessToken, String.valueOf(getPaymentAmount()), PayPalConfiguration.getClientMetadataId(this));
    }

    private void makePayment(){
        Loader.start(this);
        Log.d("userToken", currentUser.getToken());
        if(currentUser.getToken().length() > 0) {
            new RefreshToken(new RefreshToken.AsyncResponse() {
                @Override
                public void RefreshTokenIsFinish(String output) {
                    Log.d("refreshTokenOutput", output);
                    if(("error").equals(output)){
                        showError();
                    }
                    makeAutoAcceptedPayment(output);
                }
            }).execute(currentUser.getEmail(), currentUser.getToken());
        }
        else{
            startPaypalActivity(getPaymentAmount());
        }
    }

    private void startPaypalActivity(float paymentAmount){
        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(paymentAmount)),
                                                  "USD",
                                                  "The Green Magpie",
                                                  PayPalPayment.PAYMENT_INTENT_SALE
        );
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, PaypalInfo.BALANCE_MANAGEMENT_PAYPAL_REQUEST_CODE);
    }

    private void startPaypalFuturePaymentActivity() {
        Intent intent = new Intent(this, PayPalFuturePaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);

        startActivityForResult(intent, PaypalInfo.BALANCE_MANAGEMENT_FUTURE_PAYMENT_PAYPAL_REQUEST_CODE);
    }

    private boolean amountIsValid(float paymentAmount){
        if (paymentAmount >= 20){
            return true;
        }
        else{
            Toast.makeText(this, "You need to type an amount greater than $20!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private float getPaymentAmount(){
        return amountEditText.getText().toString().length() > 0 ?
                Float.parseFloat(amountEditText.getText().toString()) : 0;
    }

    private void returnToHome(){
        Intent intent = new Intent(getBaseContext(), BuyerHomepageActivity.class);
        startActivity(intent);
    }

    private void initViewElements(){
        returnButton = (ImageView) findViewById(R.id.returnView);
        balanceAmountTextView = (TextView) findViewById(R.id.textView2);
        amountEditText = (EditText) findViewById(R.id.editText);
        addButton = (Button) findViewById(R.id.button2);
        autoAcceptButton = (Button) findViewById(R.id.button3);
        repaymentButton = (Button) findViewById(R.id.button4);
        historyButton = (Button) findViewById(R.id.button5);
        returnButton = (ImageView) findViewById(R.id.returnView);
        returnTextView = (TextView) findViewById(R.id.textView17);

        balanceAmountTextView.setText("$"+String.valueOf(currentUser.getBalance()));

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float paymentAmount = getPaymentAmount();
                if (amountIsValid(paymentAmount)) {
                    makePayment();
                }
            }
        });

        autoAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPaypalFuturePaymentActivity();
            }
        });

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToHome();
            }
        });

        returnTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToHome();
            }
        });

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), HistoryActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initPaypalElements(){
        paypalConfig = new PayPalConfiguration()
                .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
                .clientId(PaypalInfo.PAYPAL_CLIENT_ID)
                .merchantName("The Green Magpie")
                .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
                .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));

        paypalIntent = new Intent(this, PayPalService.class);
        paypalIntent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
    }

    private void sendAuthorizationToServer(PayPalAuthorization auth){
        new FuturePaymentRequest().execute(auth.getAuthorizationCode(), currentUser.getEmail());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_management);

        objectPreference = (ObjectPreference) this.getApplication();
        ComplexPreferences complexPreferences = objectPreference.getComplexPreference();

        currentUser = complexPreferences.getObject("user", User.class);

        initViewElements();
        initPaypalElements();
        startService(paypalIntent);
    }

    @Override
    protected void onResume(){
        super.onResume();
        Loader.end();
        objectPreference = (ObjectPreference) this.getApplication();
        ComplexPreferences complexPreferences = objectPreference.getComplexPreference();
        currentUser = complexPreferences.getObject("user", User.class);
        balanceAmountTextView.setText("$"+String.valueOf(currentUser.getBalance()));
    }

    @Override
    public void onDestroy() {
        stopService(paypalIntent);
        super.onDestroy();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If the result is from paypal
        Log.d("request code", String.valueOf(requestCode));
        if (requestCode == PaypalInfo.BALANCE_MANAGEMENT_PAYPAL_REQUEST_CODE) {

            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == Activity.RESULT_OK) {
                //Getting the payment confirmation
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                //PayPalAuthorization auth = data.getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);

                if (confirm != null) {
                    try {
                        //Getting the payment details
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        Log.i("paymentExample", paymentDetails);
                        updateBalance(getPaymentAmount());
                        //Starting a new activity for the payment details and also putting the payment details with intent
                        /*startActivity(new Intent(this, ConfirmationActivity.class)
                                .putExtra("PaymentDetails", paymentDetails)
                                .putExtra("PaymentAmount", paymentAmount));*/

                    } catch (JSONException e) {
                        Log.e("payment", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
        else if(requestCode == PaypalInfo.BALANCE_MANAGEMENT_FUTURE_PAYMENT_PAYPAL_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth = data
                        .getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    String authorization_code = auth.getAuthorizationCode();
                    sendAuthorizationToServer(auth);
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("FuturePaymentExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("FuturePaymentExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        }
    }

    private void updateBalance(final float amount){
        new UpdateBalance(new UpdateBalance.AsyncResponse() {
            @Override
            public void UpdateBalanceIsFinished(String output) {
                balanceAmountTextView.setText(String.valueOf("$"+output));
                amountEditText.setText("");
                currentUser.setBalance(amount);
                ComplexPreferences complexPreferences = objectPreference.getComplexPreference();
                if(complexPreferences != null) {
                    complexPreferences.putObject("user", currentUser);
                    complexPreferences.commit();
                } else {
                    android.util.Log.e("error preferences", "Preference is null");
                }
                Loader.end();
            }
        }).execute(currentUser.getEmail(), String.valueOf(amount));
    }
}
