package com.example.projectrestore;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText user;
    private EditText password;
    private Button login;
    private TextView numberTries;
    private int numberOfTries = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = (EditText)findViewById(R.id.edtUser);
        password = (EditText)findViewById(R.id.edtPwd);
        login = (Button)findViewById(R.id.btnLogin);
        numberTries = (TextView)findViewById(R.id.numberTries);

        loginFunction();
    }

    private void loginFunction() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginValidation(user.getText().toString(), password.getText().toString());
            }
        });
    }

    private void loginValidation(String user, String password) {
        if (user.equals("root") && password.equals("qwerty")) {
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            startActivity(intent);
        } else {
            numberOfTries--;
            if (numberOfTries == 0) {
                login.setEnabled(false);
                numberTries.setText("Login has been disabled.");
            } else {
                numberTries.setText("Number of tries remaining: " + Integer.toString(numberOfTries));
            }
        }
    }

}
