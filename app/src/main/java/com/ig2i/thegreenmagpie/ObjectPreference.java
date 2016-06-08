package com.ig2i.thegreenmagpie;

/**
 * Created by qlammens on 06/06/16.
 */
import android.app.Application;

public class ObjectPreference extends Application {
    private static final String TAG = "ObjectPreference";
    private ComplexPreferences complexPreferences = null;

    @Override
    public void onCreate() {
        super.onCreate();
        complexPreferences = ComplexPreferences.getComplexPreferences(getBaseContext(), "abhan", MODE_PRIVATE);
        android.util.Log.i(TAG, "Preference Created.");
    }

    public ComplexPreferences getComplexPreference() {
        if(complexPreferences != null) {
            return complexPreferences;
        }
        return null;
    } }