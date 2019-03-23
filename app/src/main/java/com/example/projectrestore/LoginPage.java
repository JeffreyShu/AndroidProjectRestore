package com.example.projectrestore;

import android.app.ProgressDialog;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginPage extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private Button buttonRegister;

    private ProgressDialog progressDialog;
    private FirebaseAuth authentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        authentication = FirebaseAuth.getInstance();
        if (authentication.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(LoginPage.this, MainPage.class));
        }

        progressDialog = new ProgressDialog(LoginPage.this);

        editTextEmail = (EditText)findViewById(R.id.edtUser);
        editTextPassword = (EditText)findViewById(R.id.edtPwd);
        buttonLogin = (Button)findViewById(R.id.btnLogin);
        buttonRegister = (Button)findViewById(R.id.btnRegister);

        buttonRegisterFunction();
        buttonLoginFunction();
    }

    private void buttonRegisterFunction() {
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.equals("")) {
            Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.equals("")) {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        authentication.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginPage.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginPage.this, "Registration successful", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        } else {
                            System.out.println("Error Error: " + task.getException());
                            Toast.makeText(LoginPage.this, "Authentication failed. Please try again", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }

    private void buttonLoginFunction() {
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser();
            }
        });
    }

    private void signInUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.equals("")) {
            Toast.makeText(LoginPage.this, "Please enter an email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.equals("")) {
            Toast.makeText(LoginPage.this, "Please enter a password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Signing in...");
        progressDialog.show();

        authentication.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            finish();
                            startActivity(new Intent(LoginPage.this, MainPage.class));
                            progressDialog.dismiss();
                        } else {
                            Toast.makeText(LoginPage.this, "Incorrect email or password.", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }

}
