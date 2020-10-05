package com.bishopwang.photoencrypter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ShareActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getAction().equals(Intent.ACTION_SEND)){
            Uri uri = getIntent().getParcelableExtra(Intent.EXTRA_STREAM);
            Intent toMain = new Intent(this, MainActivity.class);
            toMain.putExtra("skipPick", true);
            toMain.setData(uri);

            System.out.println("somehow we made it here");
            startActivity(toMain);
        }

    }
}
