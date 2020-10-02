package com.example.encryptionapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

public class BiometricActivity extends AppCompatActivity{

    //requirements for the biometric scanner
    private Executor executor;
    private BiometricPrompt bioPrompt;
    private BiometricPrompt.PromptInfo promptSkeleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biometric);

        executor = ContextCompat.getMainExecutor(this);
        bioPrompt = new BiometricPrompt(BiometricActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
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
                .setTitle("test")
                .setSubtitle("subtitle")
                .setDescription("description")
                .setNegativeButtonText("exit app")
                .build();

        System.out.println("we made it here!");
        bioPrompt.authenticate(promptSkeleton);


    }



}
