package com.ig2i.thegreenmagpie.buyer;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ig2i.thegreenmagpie.ComplexPreferences;
import com.ig2i.thegreenmagpie.ObjectPreference;
import com.ig2i.thegreenmagpie.R;
import com.ig2i.thegreenmagpie.User;

public class BuyerHomepageActivity extends Activity {
    private TextView balanceAmount;
    private Button manageBalanceButton;
    private ImageView logoutButton;
    private ObjectPreference objectPreference;

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

        initViewElements();

        objectPreference = (ObjectPreference) this.getApplication();
        ComplexPreferences complexPreferences = objectPreference.getComplexPreference();

        User currentUser = complexPreferences.getObject("user", User.class);
        balanceAmount.setText("$"+String.valueOf(currentUser.getBalance()));
    }
}
