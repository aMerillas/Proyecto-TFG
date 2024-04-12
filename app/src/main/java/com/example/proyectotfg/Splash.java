package com.example.proyectotfg;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashview);

        transition(Splash.this, Login.class);
    }

    public void transition(Context context, Class<?> cls) {
        Navigator.openActivity(context, cls);
        finish();
    }


}
