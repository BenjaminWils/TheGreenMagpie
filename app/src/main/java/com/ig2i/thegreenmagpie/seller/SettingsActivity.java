package com.ig2i.thegreenmagpie.seller;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ig2i.thegreenmagpie.R;

public class SettingsActivity extends Activity {
    EditText currentPwdEditText;
    EditText newPwdEditText;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        currentPwdEditText = (EditText) findViewById(R.id.currentPwdSettings);
        newPwdEditText = (EditText) findViewById(R.id.newPwdSettings);
        saveButton = (Button) findViewById(R.id.saveSettingsBtn);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SetSettings(new SetSettings.AsyncResponse() {
                    @Override
                    public void SetSettingsIsFinished(String output) {
                        if(("success").equals(output)){
                            finish();
                        }
                        else if(("wrongPwd").equals(output)){
                            Toast.makeText(getBaseContext(), "Wrong password!", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getBaseContext(), "Settings saving failed!", Toast.LENGTH_LONG).show();
                        }
                    }
                }).execute(currentPwdEditText.getText().toString(), newPwdEditText.getText().toString());
            }
        });
    }
}
