package com.example.proyectotfg;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyectotfg.recyclerView.SpotsAdapterFB;
import com.example.proyectotfg.recyclerView.SpotsFB;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.List;
import android.Manifest;

public class MainView extends AppCompatActivity implements OnMapReadyCallback {

    FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private SpotsAdapterFB mSpotsAdapterFB;
    private RecyclerView mRecyclerView;
    private ImageView imageView, profilePic, editProfile, covermap;
    private ConstraintLayout profile;
    private Button lOut, dAccount;
    private TextView uName, uEmail;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private boolean isFirstLocationUpdate = true;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainview);
        imageView = findViewById(R.id.not_found);
        profile = findViewById(R.id.profileView);
        // Instanciamos el Firebase
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
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
        mybottomNavView.getMenu().getItem(2).setChecked(true);
        mybottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (!item.isChecked()) {
                    if (item.getItemId() == R.id.spots_Map) {
                        item.setChecked(true);
                        mapSpot();
                    } else if (item.getItemId() == R.id.spots_Next) {
                        item.setChecked(true);
                        nextSpots();
                    } else if (item.getItemId() == R.id.spots_List) {
                        item.setChecked(true);
                        listSpots();
                    } else if (item.getItemId() == R.id.spots_Favs) {
                        item.setChecked(true);
                        favSpots();
                    } else if (item.getItemId() == R.id.profile) {
                        item.setChecked(true);
                        profileView();
                    }
                    return false;
                }
                return false;
            }
        });

        //Profile
        lOut = findViewById(R.id.logOut);
        dAccount = findViewById(R.id.deleteAccount);
        lOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutConfirmationDialog();
            }
        });
        dAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteAccountDialog();
            }
        });
        FirebaseUser user = firebaseAuth.getCurrentUser();
        uName = findViewById(R.id.profileName);
        String uid = user.getUid();
        DocumentReference userReference = firebaseFirestore.collection("user").document(uid);
        bucarUsuario(userReference);
        uEmail = findViewById(R.id.profileEmail);
        uEmail.setText(user.getEmail().toString());
        editProfile = findViewById(R.id.profileEdit);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainView.this, EditProfile.class);
                startActivity(intent);
            }
        });

        //Map
        covermap = findViewById(R.id.coverMap);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

    }

    private void mapSpot() {
        profile.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        covermap.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        // Cargar el estilo del mapa desde el archivo JSON
        try {
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));
            if (!success) {
                // Si no se puede aplicar el estilo, mostrar un mensaje de error
                // o realizar una acción alternativa
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Obtener los datos de Firebase Firestore y agregar marcadores al mapa
        loadMarkersFromFirestore();

        // Inicializar locationManager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Verificar permisos de ubicación
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Solicitar permisos si no están concedidos
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }
        configureLocationUpdates();
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Obtener la etiqueta (ID del spot) del marcador
                String spotId = (String) marker.getTag();
                // Abrir el Activity con el spot correspondiente
                abrirActivityConSpot(spotId);
                return false;
            }
        });
    }

    private void abrirActivityConSpot(String spotId) {
        Intent intent = new Intent(MainView.this, SpotDetails.class);
        int id = Integer.parseInt(spotId);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    private void configureLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if (isFirstLocationUpdate) {
                    LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15)); // Zoom nivel 15
                    isFirstLocationUpdate = false;
                }
            }
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    // Manejar la respuesta de la solicitud de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permisos concedidos, configurar la ubicación
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                configureLocationUpdates();
            } else {
                LatLng cnt = new LatLng(40.4165, -3.70256);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(cnt));
            }
        }
    }

    private void loadMarkersFromFirestore() {
        firebaseFirestore.collection("spots").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        // Obtener los datos del documento
                        String latitudeStr = document.getString("latitude");
                        String longitudeStr = document.getString("longitude");
                        String title = document.getString("title");
                        String spotId = document.getId(); // Obtener el ID del spot
                        // Verificar que los valores no sean nulos antes de agregar el marcador
                        if (latitudeStr != null && longitudeStr != null && title != null) {
                            try {
                                double latitude = Double.parseDouble(latitudeStr);
                                double longitude = Double.parseDouble(longitudeStr);
                                LatLng location = new LatLng(latitude, longitude);
                                Marker marker = mMap.addMarker(new MarkerOptions()
                                        .position(location)
                                        .title(title)
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_gps)));
                                // Establecer el ID del spot como la etiqueta del marcador
                                marker.setTag(spotId); // Usar marker en lugar de markerOptions
                            } catch (NumberFormatException e) {
                                // Manejar el caso cuando las cadenas no se pueden convertir a números
                                Log.e("LoadMarkers", "No se pudo convertir la cadena de latitud o longitud a un número en el documento: " + document.getId(), e);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Manejar errores al cargar los datos de Firestore
                    Toast.makeText(MainView.this, "Error al cargar los puntos de Firebase", Toast.LENGTH_SHORT).show();
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
                    profile.setVisibility(View.INVISIBLE);
                    imageView.setVisibility(View.INVISIBLE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    covermap.setVisibility(View.VISIBLE);
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
        profile.setVisibility(View.INVISIBLE);
        covermap.setVisibility(View.VISIBLE);
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
                    profile.setVisibility(View.INVISIBLE);
                    imageView.setVisibility(View.INVISIBLE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    covermap.setVisibility(View.VISIBLE);
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

    public void profileView() {
        imageView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        profile.setVisibility(View.VISIBLE);
        covermap.setVisibility(View.VISIBLE);
        profilePic = findViewById(R.id.profilePic);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // Obtiene el ID del usuario actual
            String userId = user.getUid();
            // Accede al documento del usuario en Firestore
            firebaseFirestore.collection("user")
                    .document(userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Obtiene la URL de la foto de perfil y la carga usando Glide
                                String photoUrl = document.getString("profilePic");
                                if (photoUrl != null && !photoUrl.isEmpty()) {
                                    Glide.with(this)
                                            .load(photoUrl)
                                            .circleCrop()
                                            .into(profilePic);
                                } else {
                                    // Si no hay foto de perfil, muestra la imagen predeterminada
                                    Glide.with(this)
                                            .load(R.drawable.bosque)
                                            .circleCrop()
                                            .into(profilePic);
                                }
                            } else {
                                // El documento no existe
                            }
                        } else {
                            // Error al obtener el documento
                        }
                    });
        }
    }

    private void showLogoutConfirmationDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(R.string.logOutConfirm);
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                firebaseAuth.signOut();
                goToLogin();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDeleteAccountDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(R.string.deleteAccount);
        builder.setMessage(R.string.deleteAccountStr);

        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Usuario ha confirmado la eliminación, proceder con la eliminación de la cuenta
                deleteAccount();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Usuario ha cancelado la eliminación, cerrar el diálogo
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteAccount() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            // Eliminar la cuenta del usuario actual en Firestore
            deleteFirestoreUserData(userId);
            // Eliminar la cuenta del usuario actual en Firebase Authentication
            currentUser.delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // La cuenta ha sido eliminada exitosamente
                            Toast.makeText(this, R.string.deleteAccount, Toast.LENGTH_SHORT).show();
                            goToLogin(); // Redirige a la pantalla de inicio de sesión u otra actividad según sea necesario
                        } else {
                            // Error al eliminar la cuenta
                            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        currentUser.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //Exito
            }
        });
    }

    private void deleteFirestoreUserData(String userId) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        // Acceder al documento del usuario en Firestore y eliminarlo
        DocumentReference userRef = firebaseFirestore.collection("user").document(userId);
        userRef.delete()
                .addOnSuccessListener(aVoid -> {
                    //Exito
                })
                .addOnFailureListener(e -> {
                    // Error al eliminar el documento en Firestore
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                });
    }

    private void bucarUsuario(DocumentReference userReference) {
        userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Obtener los datos del usuario
                        String username = document.getString("name");
                        uName.setText(username);
                    } else {
                        // El documento del usuario no existe en Firestore
                        // Puedes manejar esto según tus necesidades
                    }
                } else {
                    // Manejar errores de lectura de Firestore si es necesario
                    Exception exception = task.getException();
                }
            }
        });
    }

    private void goToLogin() {
        Intent toLogin = new Intent(this, Login.class);
        toLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        toLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(toLogin);
    }

}