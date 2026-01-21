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

import com.example.nextstep.data_access.SQLiteConnector;
import com.example.nextstep.data_access.UserDAO;
import com.example.nextstep.data_access.UserProfileDAO;
import com.example.nextstep.models.User;
import com.example.nextstep.tools.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Button btnNext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        // ===================== AUTO-LOGIN / ROUTING =====================
        // If user already logged-in (Firebase or local session), skip onboarding
        // and go straight to ProfilePage.
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser fu = firebaseAuth.getCurrentUser();

        UserDAO userDAO = new UserDAO(SQLiteConnector.getInstance(this));
        UserProfileDAO userProfileDAO = new UserProfileDAO(SQLiteConnector.getInstance(this));

        // 1) Prefer Firebase persisted session (Google/Facebook/EmailPass via Firebase).
        if (fu != null && fu.getEmail() != null && !fu.getEmail().trim().isEmpty()) {
            String email = fu.getEmail();
            String name = fu.getDisplayName();

            User user = userDAO.upsertByEmail(name, "", email, "");
            if (user != null) {
                User.setActiveUser(user);
                userProfileDAO.ensureProfile(user.getId());
                SessionManager.save(this, user);
            }

            Intent i = new Intent(this, ProfilePage.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            if (user != null) i.putExtra("username", user.getUsername());
            startActivity(i);
            finish();
            return;
        }

        // 2) Fallback to local session (for users who login with SQLite username/password).
        if (SessionManager.hasSession(this)) {
            String email = SessionManager.getEmail(this);
            String username = SessionManager.getUsername(this);

            User user = null;
            if (email != null && !email.trim().isEmpty()) user = userDAO.getUserByEmail(email);
            if (user == null && username != null && !username.trim().isEmpty()) user = userDAO.getUserByUsername(username);

            if (user != null) {
                User.setActiveUser(user);
                userProfileDAO.ensureProfile(user.getId());

                Intent i = new Intent(this, ProfilePage.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.putExtra("username", user.getUsername());
                startActivity(i);
                finish();
                return;
            } else {
                // Session data exists but user row is missing. Clear it.
                SessionManager.clear(this);
            }
        }
        // ===================== END AUTO-LOGIN / ROUTING =====================

        setContentView(R.layout.activity_main);

        //App transision
        splashScreen.setOnExitAnimationListener(splashScreenView -> {
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
                    // delete splash after animation
                    splashScreenView.remove();
                }
            });

            fadeOut.start();
        });


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