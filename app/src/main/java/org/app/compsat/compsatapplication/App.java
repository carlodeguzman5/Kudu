package org.app.compsat.compsatapplication;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.PushService;

import java.util.ArrayList;

/**
 * Created by carlo on 9/27/2015.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //Parse.enableLocalDatastore(this);
        Parse.initialize(this, "THz1fv3xfay1bnVKgWUUfyIL6e0pcjfFX9aisSSL", "qzkzqY1H6duTFrsRI2c1nlgXittF33CsPg0r525X");
        //ParsePush.subscribeInBackground("CompSAtApplication");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

}
