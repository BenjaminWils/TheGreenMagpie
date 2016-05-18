package com.ig2i.thegreenmagpie.buyer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ig2i.thegreenmagpie.PaypalInfo;
import com.ig2i.thegreenmagpie.R;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

public class BalanceManagementActivity extends AppCompatActivity{

    private Button returnButton;
    private TextView balanceAmountTextView;
    private EditText amountEditText;
    private Button addButton;
    private Button autoAcceptButton;
    private Button repaymentButton;
    private Button historyButton;
    private static PayPalConfiguration paypalConfig;
    private static Intent paypalIntent;

    private void startPaypalActivity(){
        float paymentAmount = Float.parseFloat(amountEditText.getText().toString());
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

    private void initViewElements(){
        returnButton = (Button) findViewById(R.id.button);
        balanceAmountTextView = (TextView) findViewById(R.id.textView2);
        amountEditText = (EditText) findViewById(R.id.editText);
        addButton = (Button) findViewById(R.id.button2);
        autoAcceptButton = (Button) findViewById(R.id.button3);
        repaymentButton = (Button) findViewById(R.id.button4);
        historyButton = (Button) findViewById(R.id.button5);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPaypalActivity();
            }
        });
    }

    private void initPaypalElements(){
        paypalConfig = new PayPalConfiguration()
                .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
                .clientId(PaypalInfo.PAYPAL_CLIENT_ID);

        paypalIntent = new Intent(this, PayPalService.class);
        paypalIntent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_management);
        initViewElements();
        initPaypalElements();
        startService(paypalIntent);
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

                //if confirmation is not null
                if (confirm != null) {
                    try {
                        //Getting the payment details
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        Log.i("paymentExample", paymentDetails);

                        //Starting a new activity for the payment details and also putting the payment details with intent
                        /*startActivity(new Intent(this, ConfirmationActivity.class)
                                .putExtra("PaymentDetails", paymentDetails)
                                .putExtra("PaymentAmount", paymentAmount));*/

                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    }
}
