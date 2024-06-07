package com.example.proyectotfg;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private TextInputEditText userName;
    private ImageView userPic;
    private TextView nameUserView;
    private Button uploadPhoto, saveChanges, cancelChanges;
    private static final int PICK_IMAGE = 100;
    private Uri selectedImageUri;
    private StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        nameUserView = findViewById(R.id.nameUser);
        userPic = findViewById(R.id.profilePick);
        userName = findViewById(R.id.name);
        uploadPhoto = findViewById(R.id.uploadPhoto);
        saveChanges = findViewById(R.id.saveChanges);
        cancelChanges = findViewById(R.id.cancelChanges);

        // Verifica si el usuario actual existe
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
                                // Obtiene el nombre del documento y establece el texto en el TextInputEditText
                                String nombreUsuario = document.getString("name");
                                nameUserView.setText(nombreUsuario);
                                userName.setText(nombreUsuario);

                                // Obtiene la URL de la foto de perfil y la carga usando Glide
                                String photoUrl = document.getString("profilePic");
                                if (photoUrl != null && !photoUrl.isEmpty()) {
                                    Glide.with(this)
                                            .load(photoUrl)
                                            .circleCrop()
                                            .into(userPic);
                                } else {
                                    // Si no hay foto de perfil, muestra la imagen predeterminada
                                    Glide.with(this)
                                            .load(R.drawable.bosque)
                                            .circleCrop()
                                            .into(userPic);
                                }
                            } else {
                                // El documento no existe
                            }
                        } else {
                            // Error al obtener el documento
                        }
                    });
        }

        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = userName.getText().toString().trim();
                if (selectedImageUri != null) {
                    if(newName.equals("")){
                        Toast.makeText(EditProfile.this, R.string.insertUsername, Toast.LENGTH_SHORT).show();
                    }else{
                        // Subir la imagen al Storage
                        uploadImage(selectedImageUri, newName);
                    }
                } else {
                    // No se seleccionó una nueva imagen, solo actualizar el nombre
                    if (!newName.equals("")) {
                        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                        if (currentUser != null) {
                            updateUserNameAndPhoto(currentUser.getUid(), newName, null);
                        }
                    } else {
                        Toast.makeText(EditProfile.this, R.string.insertUsername, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        cancelChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMain();
            }
        });

    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            selectedImageUri = data.getData();
            userPic.setImageURI(selectedImageUri);
        }
    }

    private void uploadImage(Uri imageUri, final String nuevoNombre) {
        // Define la ruta en el Storage donde se almacenará la imagen
        final StorageReference imageRef = storageReference.child("images/" + firebaseAuth.getCurrentUser().getUid() + ".jpg");

        // Sube la imagen al Storage
        UploadTask uploadTask = imageRef.putFile(imageUri);

        // Maneja los eventos de éxito y fracaso de la carga
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Obtiene la URL de la imagen subida
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUri) {
                        // Actualiza el nombre y la URL de la imagen en Firestore
                        updateUserNameAndPhoto(firebaseAuth.getCurrentUser().getUid(), nuevoNombre, downloadUri.toString());
                        goToMain();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfile.this, "Error "+R.string.subirPhoto, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserNameAndPhoto(String userId, String nuevoNombre, String photoUrl) {
        DocumentReference userRef = firebaseFirestore.collection("user").document(userId);
        // Crea un mapa con los datos a actualizar
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", nuevoNombre);

        // Agrega la URL de la imagen solo si se proporciona
        if (photoUrl != null) {
            updates.put("profilePic", photoUrl);
        }

        // Actualiza los datos en Firestore
        userRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditProfile.this, R.string.editProfile, Toast.LENGTH_SHORT).show();
                    goToMain();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProfile.this, "Error", Toast.LENGTH_SHORT).show();
                });
    }

    private void goToMain() {
        Intent toMain = new Intent(EditProfile.this, MainView.class);
        toMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        toMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(toMain);
    }

}