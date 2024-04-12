package com.example.proyectotfg;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SpotsList extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spotslistview);

        Button myFavSpotsButton = findViewById(R.id.myFavSpotsButtonSpotsListView);
        Button myNextSpotsButton = findViewById(R.id.myNextSpotsButtonSpotsListView);


        myFavSpotsButton.setOnClickListener(view -> Navigator.openActivity(SpotsList.this, MyFavSpots.class));
        myNextSpotsButton.setOnClickListener(view -> Navigator.openActivity(SpotsList.this, MyNextSpots.class));

    }

}