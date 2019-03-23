package com.example.projectrestore;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainPage extends AppCompatActivity {
    private Button buttonSignOut;
    private TextView textViewWelcome;
    private Button buttonProfile;
    private Button buttonUpload;

    private FirebaseAuth authentication;
    private DatabaseReference dataRef;
    private DatabaseReference dataRefImages;

    private String firebaseUserId;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        authentication = FirebaseAuth.getInstance();

        buttonSignOut = (Button)findViewById(R.id.buttonSignOut);
        textViewWelcome = (TextView)findViewById(R.id.textViewWelcome);
        buttonProfile = (Button)findViewById(R.id.buttonProfile);
        buttonUpload = (Button)findViewById(R.id.buttonUpload);

        FirebaseUser user = authentication.getCurrentUser();
        dataRef = FirebaseDatabase.getInstance().getReference();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        firebaseUserId = authentication.getUid();

        textViewWelcome.setText("Welcome " + user.getEmail());

        buttonSignOutFunction();
        buttonProfileFunction();
        buttonUploadFunction();

        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showNameData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        dataRefImages = dataRef.child(firebaseUserId).child("restored_images_url");
        dataRefImages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnap : dataSnapshot.getChildren()) {
                    String url = postSnap.getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void buttonUploadFunction() {
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(MainPage.this, UploadPage.class));
            }
        });
    }

    private void showNameData(DataSnapshot dataSnapshot) {
        if (dataSnapshot.hasChild(firebaseUserId)) {
            String name = dataSnapshot.child(firebaseUserId).child("user profile info").getValue(UserInformation.class).getName();
            textViewWelcome.setText("Welcome " + name);
        }
    }

    private void buttonSignOutFunction() {
        buttonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    private void signOut() {
        authentication.signOut();
        finish();
        startActivity(new Intent(MainPage.this, LoginPage.class));
    }

    private void buttonProfileFunction() {
        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(MainPage.this, ProfilePage.class));
            }
        });
    }
}
