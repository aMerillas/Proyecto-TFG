package com.example.proyectotfg;


import android.content.Context;
import android.content.Intent;


import androidx.appcompat.app.AppCompatActivity;

public class Navigator extends AppCompatActivity {

    public static void openActivity(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }

}