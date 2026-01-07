package com.example.nextstep;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Splash2Activity extends AppCompatActivity {

    private Button btnPrev, btnNext2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash2);

        btnPrev  = findViewById(R.id.btnPrev);
        btnNext2 = findViewById(R.id.btnNext);

        // Kembali ke halaman 1
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Splash2Activity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Lanjut ke halaman 3 (kalau ada)
        btnNext2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(Splash2Activity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
