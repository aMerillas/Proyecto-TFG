package com.example.proyectotfg;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectotfg.recyclerView.SpotsAdapterFB;
import com.example.proyectotfg.recyclerView.SpotsFB;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class MainView extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    private SpotsAdapterFB mSpotsAdapterFB;
    private RecyclerView mRecyclerView;
    private ImageView imageView;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainview);
        imageView = findViewById(R.id.not_found);
        // Instanciamos el Firebase
        firebaseFirestore = FirebaseFirestore.getInstance();
        // Inicializa el RecyclerView
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Query query = firebaseFirestore.collection("spots");
        FirestoreRecyclerOptions<SpotsFB> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<SpotsFB>().setQuery(query, SpotsFB.class).build();
        mSpotsAdapterFB = new SpotsAdapterFB(firestoreRecyclerOptions);
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

        //Nav
        BottomNavigationView mybottomNavView = findViewById(R.id.bottom_navigation);
        mybottomNavView.getMenu().getItem(1).setChecked(true);
        mybottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (!item.isChecked()) {
                    if (item.getItemId() == R.id.spots_Next) {
                        item.setChecked(true);
                        nextSpots();
                    } else if (item.getItemId() == R.id.spots_List) {
                        item.setChecked(true);
                        listSpots();
                    } else if (item.getItemId() == R.id.spots_Favs) {
                        item.setChecked(true);
                        favSpots();
                    }
                    return false;
                }
                return false;
            }
        });

    }

    private void nextSpots() {
        // Obtener la referencia del usuario actualmente autenticado
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            // Obtener una referencia al documento del usuario
            DocumentReference userRef = firebaseFirestore.collection("user").document(userId);
            // Obtener el array de IDs de spots del documento del usuario
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    imageView.setVisibility(View.INVISIBLE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    if (documentSnapshot.exists()) {
                        // Obtener el array de IDs de spots del documento del usuario
                        List<Integer> spotIDs = (List<Integer>) documentSnapshot.get("nextSpots");
                        if (spotIDs != null && !spotIDs.isEmpty()) { // Verificar si el array no está vacío
                            // Filtrar los spots basados en los IDs obtenidos
                            Query query = firebaseFirestore.collection("spots").whereIn("id", spotIDs);
                            FirestoreRecyclerOptions<SpotsFB> firestoreRecyclerOptions =
                                    new FirestoreRecyclerOptions.Builder<SpotsFB>().setQuery(query, SpotsFB.class).build();
                            mSpotsAdapterFB.updateOptions(firestoreRecyclerOptions);
                            mRecyclerView.setAdapter(mSpotsAdapterFB); // Establece el adaptador en el RecyclerView
                        } else {
                            // El array de IDs de spots está vacío
                            mRecyclerView.setVisibility(View.INVISIBLE);
                            imageView.setVisibility(View.VISIBLE);
                            Toast.makeText(MainView.this, "No hay spots guardados", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    private void listSpots() {
        Query query = firebaseFirestore.collection("spots");
        FirestoreRecyclerOptions<SpotsFB> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<SpotsFB>().setQuery(query, SpotsFB.class).build();
        mSpotsAdapterFB.updateOptions(firestoreRecyclerOptions);
        mRecyclerView.setAdapter(mSpotsAdapterFB);
        imageView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void favSpots() {
        // Obtener la referencia del usuario actualmente autenticado
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            // Obtener una referencia al documento del usuario
            DocumentReference userRef = firebaseFirestore.collection("user").document(userId);
            // Obtener el array de IDs de spots del documento del usuario
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    imageView.setVisibility(View.INVISIBLE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    if (documentSnapshot.exists()) {
                        // Obtener el array de IDs de spots del documento del usuario
                        List<Integer> spotIDs = (List<Integer>) documentSnapshot.get("favSpots");
                        if (spotIDs != null && !spotIDs.isEmpty()) { // Verificar si el array no está vacío
                            // Filtrar los spots basados en los IDs obtenidos
                            Query query = firebaseFirestore.collection("spots").whereIn("id", spotIDs);
                            FirestoreRecyclerOptions<SpotsFB> firestoreRecyclerOptions =
                                    new FirestoreRecyclerOptions.Builder<SpotsFB>().setQuery(query, SpotsFB.class).build();
                            mSpotsAdapterFB.updateOptions(firestoreRecyclerOptions);
                            mRecyclerView.setAdapter(mSpotsAdapterFB); // Establece el adaptador en el RecyclerView
                        } else {
                            // El array de IDs de spots está vacío
                            mRecyclerView.setVisibility(View.INVISIBLE);
                            imageView.setVisibility(View.VISIBLE);
                            Toast.makeText(MainView.this, "No hay spots favoritos", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSpotsAdapterFB.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSpotsAdapterFB.stopListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSpotsAdapterFB.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSpotsAdapterFB.startListening();
    }

}