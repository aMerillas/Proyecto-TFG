package com.example.proyectotfg;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginview);

        Button loginButton = findViewById(R.id.loginButtonLoginView);
        Button registerButton = findViewById(R.id.registerButtonLoginView);

        loginButton.setOnClickListener(view -> Navigator.openActivity(Login.this, MainView.class));
        registerButton.setOnClickListener(view -> Navigator.openActivity(Login.this, Register.class));
    }

}