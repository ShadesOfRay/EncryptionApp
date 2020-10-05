package com.example.encryptionapplication;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private EncryptionApplication app;
    private boolean fromIntent;
    //activity request codes
    private static final int CODE_GET_FILE = 0;
    private static final int CODE_GET_CAMERA_PICTURE = 1;
    private static final int CODE_GET_ENCRYPTED_FILE = 2;
    private static final int CODE_ENCODE_FROM_SHARE = 3;

    //permission codes
    private static final int CODE_READ_STORAGE_PERMISSION = 0;
    private static final int CODE_WRITE_STORAGE_PERMISSION = 1;
    private static final int CODE_CAMERA_PERMISSION = 2;


    //shared preferences
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefEditor;
    private final String encryptedList = "ListOfEncryptedApps";
    private String tempPhotoLocation = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app = (EncryptionApplication) getApplication();

        Intent intent = getIntent();

        //used to store the list of photos encrypted by this app
        prefs = getSharedPreferences(encryptedList, Activity.MODE_PRIVATE);
        prefEditor = prefs.edit();

        if (intent.getBooleanExtra("skipPick", false)){
            tempPhotoLocation = null;
            goToEncrypt(intent.getData());
        }
        else {
            //go to ask for the finger print
            Intent check_bio = new Intent(this, BiometricLockActivity.class);
            //if (intent.getData() != null) {
            startActivity(check_bio);
            //}
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        System.out.println("wow it worked *******************");

        if (resultCode != RESULT_OK){
            return;
        }

        switch (requestCode){
            //not gonna separate in case there have to be differences later
            case CODE_GET_FILE:
                tempPhotoLocation = null;
                goToEncrypt(data.getData());
                break;
            case CODE_GET_CAMERA_PICTURE:
                File temp = new File(tempPhotoLocation);
                Uri tempUri = Uri.fromFile(temp);
                goToEncrypt(tempUri);
                break;
            case CODE_GET_ENCRYPTED_FILE:
                goToDecrypt(data.getData());
                break;
            case CODE_ENCODE_FROM_SHARE:
                goToEncrypt(data.getData());
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    //asks a file browser for a file to encrypt
    public void encryptFile(View view){
        //ask for permissions
        if (permissionGranted(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) &&
                permissionGranted(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //System.out.println("working!");
            fromIntent = true;
            //configures the intent to get a file
            Intent getFile = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            getFile.setType("image/*");
            getFile.putExtra(DocumentsContract.EXTRA_INITIAL_URI, getApplicationContext().getFilesDir());
            if (getFile.resolveActivity(getPackageManager()) == null){
                System.out.println("no viable file manager");
                Toast.makeText(getApplicationContext(), "No viable file manager found", Toast.LENGTH_SHORT).show();
            }
            try {
                startActivityForResult(getFile, CODE_GET_FILE);
            } catch (ActivityNotFoundException e){
            }
        }
        else {
            System.out.println("no permission :(");
            //permissionRequester.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            String[] temp = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
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
            File photoFile = null;
            Intent useCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            System.out.println(getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            try {
                photoFile = createTempFile();
                Uri photoURI = FileProvider.getUriForFile(this,"com.example.encryptionapplication.fileprovider", photoFile);

                useCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(useCameraIntent, CODE_GET_CAMERA_PICTURE);
            } catch (Exception e){
                System.out.println("something went wrong bucko");
                e.printStackTrace();
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
        //ask for permissions
        if (permissionGranted(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) &&
                permissionGranted(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //System.out.println("working!");
            fromIntent = true;
            //configures the intent to get a file
            Intent getFile = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            getFile.setType("*/*");
            getFile.putExtra(DocumentsContract.EXTRA_INITIAL_URI, getApplicationContext().getExternalFilesDir(null));
            //check if there is a real file picker
            if (getFile.resolveActivity(getPackageManager()) == null){
                System.out.println("no viable file manager");
                Toast.makeText(getApplicationContext(), "No viable file manager found", Toast.LENGTH_SHORT).show();
            }
            try {
                startActivityForResult(getFile, CODE_GET_ENCRYPTED_FILE);
            } catch (ActivityNotFoundException e){
                System.out.println("something went wrong");
            }
        }
        else {
            System.out.println("no permission :(");
            //permissionRequester.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            String[] temp = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, temp, CODE_READ_STORAGE_PERMISSION);
        }
    }

    //start the activity to encrypt a file
    //parse all the necessary data
    public void goToEncrypt(Uri uri){
        System.out.println("going to encrypt now");

        Cursor cursor = this.getContentResolver().query(uri, null, null, null, null, null);
        String filename = "";
        String newName = "";
        try {
            //this will be false
            if (cursor != null && cursor.moveToFirst()){
                filename = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                System.out.println("file name is " + filename);
                newName = filename + "-encrypted";
                prefEditor.putBoolean(newName, true);
                System.out.println("What was entered into prefs: "+ newName);

            } else {
                newName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "-encrypted";
                //do this later now
                //prefEditor.putBoolean(newName, true);
            }
            //prefEditor.commit();
            encryptImage(newName, uri);

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }

    //start the activity to decrypt a file
    private void goToDecrypt(Uri uri) {
        //check if the file exists in the shared preferences
        Cursor cursor = this.getContentResolver().query(uri, null, null, null, null, null);
        String filename = "";
        String realName = "";
        try {
            //this will be false
            if (cursor != null && cursor.moveToFirst()){
                filename = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                realName = filename.substring(0, filename.length() - 4);
                if (prefs.contains(realName)) {
                    decryptImage(realName, uri);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Not an encrypted file", Toast.LENGTH_SHORT).show();
                }
                System.out.println(uri);

            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }

    private boolean permissionGranted(Context context, String permission){
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public void encryptImage(String filename, Uri uri){
        byte[] output = EncryptionHelper.encryptGivenFile(filename, uri, getContentResolver(), prefEditor);
        try {
            System.out.println("got to writing the output");
            System.out.println(getApplicationContext().getFilesDir()+  "/" + filename + ".png");
            File outPath = getApplicationContext().getExternalFilesDir(null);
            System.out.println(output.length);

            File outputFile = new File(outPath, filename + ".txt");

            FileOutputStream writer = new FileOutputStream(outputFile);
            writer.write(output);
            writer.close();

            Toast.makeText(getApplicationContext(), "Encrypted", Toast.LENGTH_SHORT).show();

            if (tempPhotoLocation != null) {
                File toDelete = new File(tempPhotoLocation);
                toDelete.delete();
            }
            else {
                DocumentsContract.deleteDocument(getApplication().getContentResolver(), uri);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void decryptImage(String filename, Uri uri){
        byte[] output = EncryptionHelper.decryptGivenFile(filename, uri, getContentResolver(), prefs.getString(filename, ""));
        try {
            File outPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File outputFile = new File(outPath, filename + ".png");

            FileOutputStream writer = new FileOutputStream(outputFile);
            writer.write(output);
            writer.close();

            Toast.makeText(getApplicationContext(), "Decrypted!", Toast.LENGTH_SHORT).show();

            DocumentsContract.deleteDocument(getApplication().getContentResolver(), uri);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    private File createTempFile() throws IOException {
        String title = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageLocation = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(title,".jpg", storageLocation);

        tempPhotoLocation = image.getAbsolutePath();
        return image;
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