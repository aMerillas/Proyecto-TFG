package com.example.proyectotfg;

        import android.os.Bundle;
        import android.widget.Button;

        import androidx.appcompat.app.AppCompatActivity;




public class Register extends AppCompatActivity {



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registerview);

        Button registerButton = findViewById(R.id.registerButtonRegisterView);

        registerButton.setOnClickListener(view -> Navigator.openActivity(Register.this, Login.class));





    }

}