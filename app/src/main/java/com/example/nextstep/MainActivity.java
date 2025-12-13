package com.example.nextstep;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private Button btnNext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ====== ANIMASI KELUAR SPLASH (TRANSISI MASUK APP) ======
        splashScreen.setOnExitAnimationListener(splashScreenView -> {
            // Contoh: fade out logo + background
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(
                    splashScreenView.getView(),
                    View.ALPHA,
                    1f,
                    0f
            );
            fadeOut.setInterpolator(new AccelerateInterpolator());
            fadeOut.setDuration(300L); // 300ms

            fadeOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    // Hapus view splash setelah animasi selesai
                    splashScreenView.remove();
                }
            });

            fadeOut.start();
        });
        // =======================================================

        btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Splash2Activity.class);
                startActivity(intent);
//                finish();
            }
        });
    }
}