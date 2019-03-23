package com.example.projectrestore;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class UploadPage extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private Button buttonChooseFile;
    private Button buttonUpload;
    private EditText editTextEnterFileName;
    private ImageView imageViewPicture;

    private Uri uriImage;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private FirebaseAuth auth;

    private DatabaseReference dataRef;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_page);

        buttonChooseFile = findViewById(R.id.buttonChooseFile);
        buttonUpload = findViewById(R.id.buttonUpload);
        editTextEnterFileName = findViewById(R.id.editTextEnterFileName);
        imageViewPicture = findViewById(R.id.imageViewPicture);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        auth = FirebaseAuth.getInstance();

        dataRef = FirebaseDatabase.getInstance().getReference();

        userId = auth.getUid();

        buttonChooseFileFunctionality();
        buttonUploadFunctionality();
    }

    private void buttonUploadFunctionality() {
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Upload();
            }
        });

    }

    public void Upload() {
        String imageName = UUID.randomUUID().toString();
        StorageReference ref = storageReference.child("images/"+ userId + "/" + imageName);
        ref.putFile(uriImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(UploadPage.this, "Upload successful.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UploadPage.this, "Upload failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ref = storageReference.child("restored_images/"+ userId + "/" + imageName);
        ref.putFile(uriImage);
        String downloadUrl = storageReference.child("restored_images/"+ userId + "/" + imageName).getDownloadUrl().toString();
        dataRef.child(userId).child("restored_images_url").child(imageName).setValue(downloadUrl);
    }

    private void buttonChooseFileFunctionality() {
        buttonChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileChooser();
            }
        });
    }

    private void FileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriImage = data.getData();
            imageViewPicture.setImageURI(uriImage);
        }
    }
}
