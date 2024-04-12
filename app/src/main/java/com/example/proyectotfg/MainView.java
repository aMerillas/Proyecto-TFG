package com.example.proyectotfg;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainView extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainview);

        Button myFavSpotsButton = findViewById(R.id.myFavSpotsButtonMainView);
        Button myNextSpotsButton = findViewById(R.id.myNextSpotsButtonMainView);
        Button spotsListButton = findViewById(R.id.spotsListButtonMainView);

        myFavSpotsButton.setOnClickListener(view -> Navigator.openActivity(MainView.this, MyFavSpots.class));
        myNextSpotsButton.setOnClickListener(view -> Navigator.openActivity(MainView.this, MyNextSpots.class));
        spotsListButton.setOnClickListener(view -> Navigator.openActivity(MainView.this, SpotsList.class));
    }

}