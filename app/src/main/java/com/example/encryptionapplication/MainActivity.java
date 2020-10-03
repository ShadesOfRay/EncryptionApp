package com.example.encryptionapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private EncryptionApplication app;
    /*
    private ActivityResultLauncher<String> permissionRequester = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            goToEncrypt();
        }
        else {
            //ask for a redo lol
        }
    });*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app = (EncryptionApplication) getApplication();

        //go to ask for the finger print
        Intent check_bio = new Intent(this, BiometricLockActivity.class);
        startActivity(check_bio);
    }



    //asks a file browser for a file to encrypt


    public void encryptFile(View view){
        //ask for permissions
        if (permissionGranted(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            System.out.println("working!");
            goToEncrypt(null);
        }
        else {
            System.out.println("no permission :(");
            //permissionRequester.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            String[] temp = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, temp ,0);
        }
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

    private void goToEncrypt(File file){
        System.out.println("going to encrypt now");
    }

    //asks a camera app for a picture to encrypt
    public void encryptFromCamera(View view){
        //ask for permissions
        if (permissionGranted(getApplicationContext(), Manifest.permission.CAMERA)) {
            System.out.println("working!");
            goToEncrypt(null);
        }
        else {
            System.out.println("no permission :(");
            //permissionRequester.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            String[] temp = {Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(this, temp ,0);
        }
    }

    //asks a file browser for a file to decrypt
    public void decryptFile(View view){

    }

    public boolean permissionGranted(Context context, String permission){
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

}