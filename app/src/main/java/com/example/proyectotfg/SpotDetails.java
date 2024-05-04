package com.example.proyectotfg;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class SpotDetails extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    String title, latitude, longitude;
    int idSpot;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spotdetailsview);
        // Inicializa FirebaseFirestore
        firebaseFirestore = FirebaseFirestore.getInstance();
        // Recupera la información del Intent
        Intent intent = getIntent();
        idSpot = intent.getIntExtra("id", 1);
        spotData(idSpot);
        //Accion botones
        Button addNextSpots = findViewById(R.id.addNextSpots);
        ImageButton favSpot = findViewById(R.id.addFavSpots);
        Button seeMapsButton = findViewById(R.id.seeMaps);
        addNextSpots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //Funcionalidad maps
        seeMapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crea una Uri con las coordenadas de latitud y longitud
                Uri myUri = Uri.parse("geo:0,0?q=" + latitude +"," + longitude + "(" + title + ") ");
                // Crea un Intent con la acción de ver la ubicación en Google Maps
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, myUri);
                startActivity(mapIntent);
                // Verifica si hay una aplicación que pueda manejar el Intent
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    // Si hay una aplicación, abre Google Maps

                }
            }
        });
    }

    private void spotData(int idSpot) {
        firebaseFirestore.collection("spots").
                whereEqualTo("id", idSpot)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // El documento existe, ahora puedes obtener más datos
                            String image = document.getString("image1");
                            title = document.getString("title");
                            String subtitle = document.getString("subtitle");
                            String details = document.getString("details");
                            latitude = document.getString("latitude");
                            longitude = document.getString("longitude");
                            Long difficulty = document.getLong("difficulty");
                            String type = document.getString("type");
                            showData(image, title, subtitle, details, latitude, longitude, difficulty, type);
                        }
                    } else {
                        Toast.makeText(this, "Error getting data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showData(String image, String title, String subtitle, String details, String latitude, String longitude, Long difficulty, String type) {
        ImageView imgSpot = findViewById(R.id.imgSpot);
        TextView titleSpot = findViewById(R.id.titleSpot);
        TextView subtitleSpot = findViewById(R.id.subtitleSpot);
        TextView detailSpot = findViewById(R.id.detailSpot);
        TextView typeSpot = findViewById(R.id.typeSpot);
        ProgressBar difficultyProgressBar = findViewById(R.id.progressBar);
        //Instanciar
        Glide.with(this)
                .load(image)
                .centerCrop()
                .into(imgSpot);
        titleSpot.setText(title);
        subtitleSpot.setText(subtitle);
        detailSpot.setText(details);
        typeSpot.setText(type.toUpperCase());
        difficultyProgressBar.setProgress(Math.toIntExact(difficulty));

    }

}