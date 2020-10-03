package com.example.encryptionapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private EncryptionApplication app;
    private boolean fromIntent;
    //activity request codes
    private static final int CODE_GET_FILE = 0;
    private static final int CODE_GET_CAMERA_PICTURE = 1;

    //permission codes
    private static final int CODE_READ_STORAGE_PERMISSION = 0;
    private static final int CODE_WRITE_STORAGE_PERMISSION = 0;
    private static final int CODE_CAMERA_PERMISSION = 1;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        System.out.println("wow it worked *******************");

        super.onActivityResult(requestCode, resultCode, data);
    }

    //asks a file browser for a file to encrypt
    public void encryptFile(View view){
        //ask for permissions
        if (permissionGranted(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //System.out.println("working!");
            fromIntent = true;
            Intent getFile = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            try {
                startActivityForResult(getFile, CODE_GET_FILE);
            } catch (ActivityNotFoundException e){
            }
        }
        else {
            System.out.println("no permission :(");
            //permissionRequester.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            String[] temp = {Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, temp, CODE_READ_STORAGE_PERMISSION);
        }
    }

    //asks a camera app for a picture to encrypt
    public void encryptFromCamera(View view){
        //ask for permissions
        if (permissionGranted(getApplicationContext(), Manifest.permission.CAMERA)) {
            //System.out.println("working!");
            //camera intent
            fromIntent = true;
            Intent useCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                startActivityForResult(useCameraIntent, CODE_GET_CAMERA_PICTURE);
            } catch (ActivityNotFoundException e){
            }
        }
        else {
            System.out.println("no permission :(");
            //permissionRequester.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            String[] temp = {Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(this, temp, CODE_CAMERA_PERMISSION);
        }
    }

    //asks a file browser for a file to decrypt
    public void decryptFile(View view){

    }

    private void goToEncrypt(File file){
        System.out.println("going to encrypt now");
    }

    public boolean permissionGranted(Context context, String permission){
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (app.getIsLocked() && !fromIntent) {
            Intent locker = new Intent(this, BiometricLockActivity.class);
            locker.putExtra("fromListener", true);
            startActivity(locker);
        }
        else {
            fromIntent = false;
        }
    }

}