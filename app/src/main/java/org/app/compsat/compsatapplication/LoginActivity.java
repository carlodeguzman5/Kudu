package org.app.compsat.compsatapplication;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class LoginActivity extends Activity {
    private EditText usernameField, passwordField;
    private Button login;
    private String username, password;
    private ImageView image, logo;
    private CheckBox cb;
    private SharedPreferences prefs;
    private LocalBroadcastManager mLocalBroadcastManager;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("org.app.compsat.compsatapplication.close")){
                Intent intent2 = new Intent(context, MainActivity.class);
                startActivity(intent2);
                finish();
            }
            else if(intent.getAction().equals("org.app.compsat.compsatapplication.loginfailed")){
                Toast.makeText(context, "Cannot connect to server", Toast.LENGTH_LONG).show();
            }
            else if(intent.getAction().equals("org.app.compsat.compsatapplication.wrongcredentials")){
                Toast.makeText(context, "Invalid Username & Password", Toast.LENGTH_LONG).show();
            }
            else if(intent.getAction().equals("org.app.compsat.compsatapplication.server")){
                Toast.makeText(context, "Internal Server Error", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefs = this.getSharedPreferences("org.app.compsat.compsatapplication",
                MODE_PRIVATE);

        if(prefs.getString("loggedIn","").equals("true")){
            //new LoginService(this).execute(prefs.getString("username",""), prefs.getString("password",""));
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        usernameField = (EditText) findViewById(R.id.loginUser);
        passwordField = (EditText) findViewById(R.id.loginpass);
        login = (Button) findViewById(R.id.button);
        cb = (CheckBox) findViewById(R.id.remember);

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/FuturaLT.ttf");
        usernameField.setTypeface(tf);
        passwordField.setTypeface(tf);
        login.setTypeface(tf);
        cb.setTypeface(tf);

        String userSave = prefs.getString("user", "");
        String passSave = prefs.getString("pass", "");



        usernameField.setText(userSave);
        passwordField.setText(passSave);

        cb.setChecked(prefs.getBoolean("cbIsChecked",false));

        image = (ImageView) findViewById(R.id.image);
        image.setImageResource(R.drawable.mainlogo);


        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("org.app.compsat.compsatapplication.close");
        mIntentFilter.addAction("org.app.compsat.compsatapplication.wrongcredentials");
        mIntentFilter.addAction("org.app.compsat.compsatapplication.loginfailed");
        mIntentFilter.addAction("org.app.compsat.compsatapplication.server");
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, mIntentFilter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void login(View view) {
        username = usernameField.getText().toString();
        password = passwordField.getText().toString();

        if (cb.isChecked()) {
            SharedPreferences prefs = this.getSharedPreferences(
                    "org.app.compsat.compsatapplication", MODE_PRIVATE);
            prefs.edit().putString("user", username)
                    .apply();
            prefs.edit().putString("pass", password)
                    .apply();
            prefs.edit().putBoolean("cbIsChecked", true).apply();
        } else {
            SharedPreferences prefs = this.getSharedPreferences(
                    "org.app.compsat.compsatapplication", MODE_PRIVATE);
            prefs.edit().clear().apply();
        }

        new LoginService(this).execute(username, password);
    }

    public void register(View view){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

}
