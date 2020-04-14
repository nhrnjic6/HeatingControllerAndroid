package com.nhrnjic.heatingcontroller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ReloadActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retry);

        Button reloadBtn = findViewById(R.id.try_again_btn);
        reloadBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ReloadActivity.this, LoadingActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
    }
}
