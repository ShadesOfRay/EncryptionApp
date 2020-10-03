package com.example.encryptionapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private EncryptionApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app = (EncryptionApplication) getApplication();

        //go to ask for the finger print
        Intent check_bio = new Intent(this, BiometricLockActivity.class);
        startActivity(check_bio);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (app.getIsLocked()) {
            Intent locker = new Intent(this, BiometricLockActivity.class);
            locker.putExtra("fromListener", true);
            startActivity(locker);
        }
    }
}