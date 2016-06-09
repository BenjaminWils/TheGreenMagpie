package com.ig2i.thegreenmagpie.buyer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.ig2i.thegreenmagpie.ComplexPreferences;
import com.ig2i.thegreenmagpie.Loader;
import com.ig2i.thegreenmagpie.ObjectPreference;
import com.ig2i.thegreenmagpie.R;
import com.ig2i.thegreenmagpie.User;
import com.ig2i.thegreenmagpie.seller.SellerConnectionActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class BuyerConnectionActivity extends Activity{
    private Button connectButton;
    private Button sellerButton;
    private EditText emailEditText;
    private User currentUser;
    private ObjectPreference objectPreference;
    private CheckBox autoConnectCheckbox;

    private void CreateUserInstanceAndConnect(String email){
        currentUser = new User(email);
        objectPreference = (ObjectPreference) this.getApplication();
        Loader.start(this);
        new GetUserData(new GetUserData.AsyncResponse() {
            @Override
            public void getUserDataIsFinished(String output) {
                try {
                    JSONObject data = new JSONObject(output);
                    currentUser.setBalance(Float.parseFloat(data.getString("balance")));
                    currentUser.setToken(data.getString("refresh_token"));
                    currentUser.setAutoConnect(autoConnectCheckbox.isChecked());
                    ComplexPreferences complexPreferences = objectPreference.getComplexPreference();
                    if(complexPreferences != null) {
                        complexPreferences.putObject("user", currentUser);
                        complexPreferences.commit();
                    } else {
                        android.util.Log.e("error preferences", "Preference is null");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Loader.end();
                Intent intent = new Intent(getBaseContext(), BuyerHomepageActivity.class);
                startActivity(intent);
            }
        }).execute(email);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_connection);

        connectButton = (Button) findViewById(R.id.button6);
        sellerButton = (Button) findViewById(R.id.button7);
        emailEditText = (EditText) findViewById(R.id.editText2);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateUserInstanceAndConnect(emailEditText.getText().toString());
            }
        });

        autoConnectCheckbox = (CheckBox) findViewById(R.id.checkBox);

        sellerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SellerConnectionActivity.class);
                startActivity(intent);
            }
        });

        objectPreference = (ObjectPreference) this.getApplication();
        ComplexPreferences complexPreferences = objectPreference.getComplexPreference();
        currentUser = complexPreferences.getObject("user", User.class);
        if(currentUser.getAutoConnect()){
            Intent intent = new Intent(getBaseContext(), BuyerHomepageActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        objectPreference = (ObjectPreference) this.getApplication();
        ComplexPreferences complexPreferences = objectPreference.getComplexPreference();
        currentUser = complexPreferences.getObject("user", User.class);
    }
}
