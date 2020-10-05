package com.bishopwang.photoencrypter;
import android.content.Intent;

import android.hardware.biometrics.BiometricManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;


public class BiometricLockActivity extends AppCompatActivity{

    //requirements for the biometric scanner
    private Executor executor;
    private BiometricPrompt bioPrompt;
    private BiometricPrompt.PromptInfo promptSkeleton;
    private Intent intent;
    private boolean fromLock;

    private EncryptionApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biometric);
        app = (EncryptionApplication) getApplication();

        intent = getIntent();
        fromLock = intent.getBooleanExtra("fromListener", false);

        executor = ContextCompat.getMainExecutor(this);

        androidx.biometric.BiometricManager biometricManager = androidx.biometric.BiometricManager.from(this);
        switch(biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast toast = Toast.makeText(getApplicationContext(),
                        "User does not have a fingerprint registered. You must have a fingerprint registered to use this app",
                        Toast.LENGTH_LONG);
                toast.show();
                break;
        }
        bioPrompt = new BiometricPrompt(BiometricLockActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                //make a toast message?
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                app.setUnlocked();
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                //TODO make the front camera take a photo when it fails lol
            }
        });

        promptSkeleton = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Unlock App")
                //.setSubtitle("subtitle")
                //.setDescription("description")
                .setNegativeButtonText("Cancel")
                .build();

        System.out.println("we made it here!");
    }

    public void askForAuthentication(View view){
        bioPrompt.authenticate(promptSkeleton);
    }

    //return to home on back pressed
    @Override
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(a);

    }

}
