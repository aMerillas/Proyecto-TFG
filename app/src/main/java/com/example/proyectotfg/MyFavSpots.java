package com.example.proyectotfg;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectotfg.recyclerView.cardPlace;
import com.example.proyectotfg.recyclerView.cardPlaceAdapter;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyFavSpots extends AppCompatActivity {

     private List<cardPlace> prueba = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myfavspotslist);

        Button myNextSpotsButton = findViewById(R.id.myNextSpotsButtonFavView);
        Button visitedSpotsButton = findViewById(R.id.visitedSpotsButtonFavView);
        RecyclerView mRecyclerView = findViewById(R.id.recyclerView);

        visitedSpotsButton.setOnClickListener(view -> Navigator.openActivity(MyFavSpots.this, VisitedSpots.class));
        myNextSpotsButton.setOnClickListener(view -> Navigator.openActivity(MyFavSpots.this, MyNextSpots.class));

        //Recycler
        mRecyclerView.setLayoutManager(mRecyclerView.getLayoutManager());
        cardPlaceAdapter mCardPlaceAdapter = new cardPlaceAdapter(prueba);
        mRecyclerView.setAdapter(mCardPlaceAdapter);

        mCardPlaceAdapter.setOnItemClickListener(new cardPlaceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                cardPlace clickedProduct = prueba.get(position);
                // Implementa la lógica para abrir el nuevo Activity aquí
                Toast.makeText(MyFavSpots.this, "Hola", Toast.LENGTH_SHORT).show();
            }

            /*@Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                // Obtiene el producto en la posición clicada
                cardPlace clickedProduct = prueba.get(position);
                // Implementa la lógica para abrir el nuevo Activity aquí
                Toast.makeText(MyFavSpots.this, "Hola", Toast.LENGTH_SHORT).show();
            }*/
        });

        for (int i = 1; i <= 30; i++) {
            String name = "Producto " + i;
            String imgurl = String.valueOf(R.color.barf_green);

            cardPlace card = new cardPlace(name, imgurl);
            prueba.add(card);
        }

    }

}