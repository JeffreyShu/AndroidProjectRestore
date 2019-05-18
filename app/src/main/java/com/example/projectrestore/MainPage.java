package com.example.projectrestore;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.IDNA;
import android.net.Uri;
import android.nfc.Tag;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MainPage extends AppCompatActivity {
    private Button buttonSignOut;
    private Button buttonProfile;
    private Button buttonUpload;

    private FirebaseAuth authentication;
    private DatabaseReference dataRef;
    private DatabaseReference dataRefImages;

    private String firebaseUserId;

    private ArrayList<String> imageNames = new ArrayList<>();
    private ArrayList<String> imageUrls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        authentication = FirebaseAuth.getInstance();

        buttonSignOut = findViewById(R.id.buttonSignOut);
        buttonProfile = findViewById(R.id.buttonProfile);
        buttonUpload = findViewById(R.id.buttonUpload);

        FirebaseUser user = authentication.getCurrentUser();
        dataRef = FirebaseDatabase.getInstance().getReference();

        firebaseUserId = authentication.getUid();

        buttonSignOutFunction();
        buttonProfileFunction();
        buttonUploadFunction();

        dataRefImages = dataRef.child(firebaseUserId);
        dataRefImages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnap : dataSnapshot.getChildren()) {
                    // Glide.with(getApplicationContext()).load(url).into(imageView);
                    for (DataSnapshot postpostSnap : postSnap.getChildren()) {
                        /*
                        String url = postpostSnap.getValue(String.class);
                        Glide.with(getApplicationContext()).load(url).into(imageView);
                         */
                        imageNames.add(postpostSnap.getKey().toString());
                        imageUrls.add(postpostSnap.getValue().toString());
                    }
                }
                initRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.my_recycler_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, imageNames, imageUrls);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
