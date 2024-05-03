package com.example.proyectotfg;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyFavSpots extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myfavspotslist);

        Button myNextSpotsButton = findViewById(R.id.myNextSpotsButtonFavView);
        Button visitedSpotsButton = findViewById(R.id.visitedSpotsButtonFavView);
        RecyclerView mRecyclerView = findViewById(R.id.recyclerView);

        visitedSpotsButton.setOnClickListener(view -> Navigator.openActivity(MyFavSpots.this, VisitedSpots.class));
        myNextSpotsButton.setOnClickListener(view -> Navigator.openActivity(MyFavSpots.this, MyNextSpots.class));

    }

}