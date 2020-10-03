package com.example.encryptionapplication;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

public class EncryptionApplication extends Application {
    private boolean isLocked;


    @Override
    public void onCreate(){
        super.onCreate();

        //screen off checker
        ScreenLockReceiver receiver = new ScreenLockReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        //intentFilter.addAction("test");
        registerReceiver(receiver, intentFilter);

        //check if the user moves off the app
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new AppLifecycleObserver());
    }

    public boolean getIsLocked(){
        return isLocked;
    }

    public void setUnlocked(){
        isLocked = false;
    }

    private class AppLifecycleObserver implements LifecycleEventObserver {
        @Override
        public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
            if (event == Lifecycle.Event.ON_STOP){
                isLocked = true;
                System.out.println("APP PUT IN BACKGROUND *********************");
            }
        }
    }

    private class ScreenLockReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            isLocked = true;
            System.out.println("SCREEN LOCKED *****************");
        }
    }

}
