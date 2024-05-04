package com.example.proyectotfg;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectotfg.recyclerView.SpotsAdapterFB;
import com.example.proyectotfg.recyclerView.SpotsFB;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MainView extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    private SpotsAdapterFB mSpotsAdapterFB;
    private RecyclerView mRecyclerView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainview);
        // Instanciamos el Firebase
        firebaseFirestore = FirebaseFirestore.getInstance();
        // Inicializa el RecyclerView
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Query query = firebaseFirestore.collection("spots");
        FirestoreRecyclerOptions<SpotsFB> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<SpotsFB>().setQuery(query, SpotsFB.class).build();
        mSpotsAdapterFB = new SpotsAdapterFB(firestoreRecyclerOptions);
        mSpotsAdapterFB.notifyDataSetChanged();
        mSpotsAdapterFB.startListening();
        mSpotsAdapterFB.setOnItemClickListener(new SpotsAdapterFB.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                // Obtén el modelo de producto correspondiente al documento
                SpotsFB clickedProduct = documentSnapshot.toObject(SpotsFB.class);
                // Implementa la lógica para abrir el nuevo Activity aquí
                Intent intent = new Intent(MainView.this, SpotDetails.class);
                intent.putExtra("id", clickedProduct.getId());
                // Puedes usar Intent para iniciar un nuevo Activity, pasando la información necesaria
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mSpotsAdapterFB);

        Button myFavSpotsButton = findViewById(R.id.myFavSpotsButtonMainView);
        Button myNextSpotsButton = findViewById(R.id.myNextSpotsButtonMainView);
        Button spotsListButton = findViewById(R.id.spotsListButtonMainView);

        myFavSpotsButton.setOnClickListener(view -> Navigator.openActivity(MainView.this, MyFavSpots.class));
        myNextSpotsButton.setOnClickListener(view -> Navigator.openActivity(MainView.this, MyNextSpots.class));
        spotsListButton.setOnClickListener(view -> Navigator.openActivity(MainView.this, SpotsList.class));
    }

}