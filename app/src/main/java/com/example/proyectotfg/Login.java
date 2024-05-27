package com.example.proyectotfg;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private TextInputLayout email, passwd;
    FirebaseAuth firebaseAuth;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginview);
        firebaseAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email);
        passwd = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.loginButtonLoginView);
        Button registerButton = findViewById(R.id.registerButtonLoginView);

        ImageView mLake = findViewById(R.id.imageback);
        Glide.with(this)
                .load(R.drawable.montana)
                .transition(DrawableTransitionOptions.withCrossFade(3000))
                .centerCrop()
                //.circleCrop()
                .placeholder(new ColorDrawable(this.getResources().getColor(R.color.black)))
                .into(mLake);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uEmail = email.getEditText().getText().toString().trim();
                String uPass = passwd.getEditText().getText().toString().trim();
                setError();
                if(uEmail.isEmpty() || uPass.isEmpty()) {
                    if (uEmail.isEmpty()){
                        passwd.setError(" ");
                    }
                    //cambio provisional
                    if (uPass.isEmpty()){
                        passwd.setError(" ");
                    }
                } else {
                    loginUser(uEmail, uPass);
                }
            }
       });
        //loginButton.setOnClickListener(view -> Navigator.openActivity(Login.this, MainView.class));
        registerButton.setOnClickListener(view -> Navigator.openActivity(Login.this, Register.class));
    }

    private void loginUser(String uEmail, String uPass) {
        firebaseAuth.signInWithEmailAndPassword(uEmail, uPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    goToMain();
                } else {
                    passwd.setError(getResources().getString(R.string.revisar));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast(e.getMessage());
            }
        });
    }

    private void goToMain() {
        Intent toMain = new Intent(Login.this, MainView.class);
        toMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        toMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(toMain);
    }

    //Funcion para mostrar los posibles errores con Toast
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    //Funcion para mostrar los errores a la hora de introducir un usuario
    private void setError() {
        email.setErrorEnabled(false);
        passwd.setErrorEnabled(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            goToMain();
        }
    }

}