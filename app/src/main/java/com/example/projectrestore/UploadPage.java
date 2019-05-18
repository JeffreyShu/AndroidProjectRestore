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

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
    private Button buttonBack;
    private EditText editTextEnterClassName;
    private EditText editTextEnterFileName;
    private ImageView imageViewPicture;

    private Uri uriImage;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private FirebaseAuth auth;

    private DatabaseReference dataRef;

    private String userId;

    private String imageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_page);

        buttonChooseFile = findViewById(R.id.buttonChooseFile);
        buttonUpload = findViewById(R.id.buttonUpload);
        buttonBack = findViewById(R.id.buttonBack);
        editTextEnterClassName = findViewById(R.id.editTextEnterClassName);
        editTextEnterFileName = findViewById(R.id.editTextEnterFileName);
        imageViewPicture = findViewById(R.id.imageViewPicture);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        auth = FirebaseAuth.getInstance();

        dataRef = FirebaseDatabase.getInstance().getReference();

        userId = auth.getUid();

        buttonChooseFileFunctionality();
        buttonUploadFunctionality();
        buttonbackFunctionality();
    }

    private void buttonbackFunctionality() {
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(UploadPage.this, MainPage.class));
            }
        });
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
        final String classname = editTextEnterClassName.getText().toString();
        final String filename = editTextEnterFileName.getText().toString();
        if (classname.equals("") && filename.equals("")) {
            Toast.makeText(UploadPage.this, "Please enter the class name that image will be placed under and the name of the image.", Toast.LENGTH_LONG).show();
        } else if (classname.equals("")) {
            Toast.makeText(UploadPage.this, "Please enter the class name that image will be placed under.", Toast.LENGTH_LONG).show();
        } else if (filename.equals("")) {
             Toast.makeText(UploadPage.this, "Please enter the name of the file.", Toast.LENGTH_LONG).show();
        } else {
            StorageReference ref_original = storageReference.child(userId + "/" + classname + "/" + filename);
            ref_original.putFile(uriImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(UploadPage.this, "Upload successful.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UploadPage.this, "Upload failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            final StorageReference ref_restored = storageReference.child(userId + "/" + classname + "_r" + "/" + filename);
            UploadTask uploadTask = ref_restored.putFile(uriImage);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return ref_restored.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        dataRef.child(userId).child(classname + "_r").child(filename).setValue(downloadUri.toString());
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
        }
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
