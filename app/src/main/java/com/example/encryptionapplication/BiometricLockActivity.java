package com.example.encryptionapplication;

import android.os.Bundle;
import android.view.View;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biometric);

        executor = ContextCompat.getMainExecutor(this);
        bioPrompt = new BiometricPrompt(BiometricLockActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                //make a toast message?
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
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



}
