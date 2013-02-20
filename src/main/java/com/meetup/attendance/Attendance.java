package com.meetup.attendance;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import java.net.Authenticator;

public class Attendance extends Activity {
    private TextView hello;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!PreferenceUtility.getInstance().isLoggedIn()) {
            startActivity(new Intent(this, Auth.class).putExtra("return_to", getIntent()));
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
        hello = ((TextView) findViewById(R.id.hello));
    }
}