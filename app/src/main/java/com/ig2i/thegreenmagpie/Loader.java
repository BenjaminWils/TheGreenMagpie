package com.ig2i.thegreenmagpie;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by qlammens on 06/06/16.
 */
public class Loader {
    private static ProgressDialog progress;

    public static void start(Context context){
        progress = new ProgressDialog(context);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.show();
    }

    public static void end(){
        if(progress != null) progress.dismiss();
    }
}
