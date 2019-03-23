package com.example.projectrestore;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfilePage extends AppCompatActivity {
    private EditText editTextUsername;
    private EditText editTextName;
    private EditText editTextAge;
    private Button buttonSave;
    private Button buttonBack;

    private DatabaseReference dataRefUsers;
    private FirebaseAuth auth;

    private String firebaseUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        editTextUsername = (EditText)findViewById(R.id.editTextUsername);
        editTextName = (EditText)findViewById(R.id.editTextName);
        editTextAge = (EditText)findViewById(R.id.editTextAge);
        buttonSave = (Button)findViewById(R.id.buttonSave);
        buttonBack = (Button)findViewById(R.id.buttonBack);

        dataRefUsers = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        firebaseUserId = auth.getUid();

        buttonSaveFunctionality();
        buttonBackFunctionality();

        dataRefUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showUserData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void buttonBackFunctionality() {
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(ProfilePage.this, MainPage.class));
            }
        });
    }

    private void buttonSaveFunctionality() {
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInformation();
            }
        });
    }

    private void saveUserInformation() {
        String username = editTextUsername.getText().toString().trim();
        String name = editTextName.getText().toString().trim();
        String age = editTextAge.getText().toString().trim();

        UserInformation userInfo = new UserInformation(username, name, age);

        dataRefUsers.child(firebaseUserId).child("user profile info").setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ProfilePage.this, "User Information Saved.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showUserData(DataSnapshot dataSnapshot) {
        String username = "";
        String name = "";
        String age = "";
        if (dataSnapshot.hasChild(firebaseUserId)) {
            username = dataSnapshot.child(firebaseUserId).child("user profile info").getValue(UserInformation.class).getUsername();
            name = dataSnapshot.child(firebaseUserId).child("user profile info").getValue(UserInformation.class).getName();
            age = dataSnapshot.child(firebaseUserId).child("user profile info").getValue(UserInformation.class).getAge();
        }
        editTextUsername.setText(username);
        editTextName.setText(name);
        editTextAge.setText(age);
    }
}
