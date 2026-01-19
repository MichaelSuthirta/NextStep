package com.example.nextstep;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.credentials.exceptions.NoCredentialException;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import com.example.nextstep.data_access.SQLiteConnector;
import com.example.nextstep.data_access.UserDAO;
import com.example.nextstep.data_access.UserProfileDAO;
import com.example.nextstep.models.User;

public class RegisterActivity extends AppCompatActivity {

    private Button btnRegister;
    private EditText etUsername, etEmail, etPassword;
    private ImageButton btnGoogle, btnFacebook, btnLinkedin;
    private TextView tvLoginHere;


    private UserDAO userDAO;
    private UserProfileDAO userProfileDAO;

    private CredentialManager credentialManager;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnRegister = findViewById(R.id.btnRegister);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnGoogle = findViewById(R.id.btnGoogle);
        btnFacebook = findViewById(R.id.btnFacebook);
        btnLinkedin = findViewById(R.id.btnLinkedin);
        tvLoginHere = findViewById(R.id.tvLoginHere);

        userDAO = new UserDAO(SQLiteConnector.getInstance(this));
        userProfileDAO = new UserProfileDAO(SQLiteConnector.getInstance(this));

        credentialManager = CredentialManager.create(this);
        firebaseAuth = FirebaseAuth.getInstance();

        // Make the "Login here" part look like a link
        try {
            String full = "Already have an account? Login here";
            SpannableString ss = new SpannableString(full);
            int start = full.indexOf("Login here");
            if (start >= 0) {
                int end = start + "Login here".length();
                ss.setSpan(new ForegroundColorSpan(getColor(R.color.link)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            tvLoginHere.setText(ss);
        } catch (Exception ignored) {}

        tvLoginHere.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        btnGoogle.setOnClickListener(v -> startGoogleSignIn(false));
        btnFacebook.setOnClickListener(v -> Toast.makeText(this, "Facebook Login", Toast.LENGTH_SHORT).show());
        btnLinkedin.setOnClickListener(v -> Toast.makeText(this, "LinkedIn Login", Toast.LENGTH_SHORT).show());

        btnRegister.setOnClickListener(v -> {
            String u = etUsername.getText().toString().trim();
            String em = etEmail.getText().toString().trim();

            String p1 = etPassword.getText().toString().trim();

            if (u.isEmpty() || em.isEmpty() || p1.isEmpty()) {
                Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            // Buat akun di Firebase (email+password), lalu sinkron ke SQLite.
            firebaseAuth.createUserWithEmailAndPassword(em, p1)
                    .addOnCompleteListener(this, task -> {
                        if (!task.isSuccessful()) {
                            String msg = task.getException() != null ? task.getException().getMessage() : "Register gagal";
                            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                            return;
                        }

                        User user = userDAO.upsertByEmail(u, "", em, p1);
                        if (user != null) {
                            User.setActiveUser(user);
                            userProfileDAO.ensureProfile(user.getId());
                        }

                        Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show();
                        Intent moveToProfile = new Intent(this, ProfilePage.class);
                        moveToProfile.putExtra("username", user != null ? user.getUsername() : u);
                        startActivity(moveToProfile);
                        finish();
                    });
        });
    }

    private void startGoogleSignIn(boolean filterByAuthorizedAccounts) {
        // Sama seperti di Login: kalau filter=true dan user baru pertama kali,
        // sering muncul "No credentials found".
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setServerClientId(getString(R.string.default_web_client_id))
                .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        credentialManager.getCredentialAsync(
                this,
                request,
                null,
                getMainExecutor(),
                new CredentialManagerCallback(filterByAuthorizedAccounts)
        );
    }

    private class CredentialManagerCallback implements androidx.credentials.CredentialManagerCallback<GetCredentialResponse, GetCredentialException> {

        private final boolean usedFilterByAuthorizedAccounts;

        private CredentialManagerCallback(boolean usedFilterByAuthorizedAccounts) {
            this.usedFilterByAuthorizedAccounts = usedFilterByAuthorizedAccounts;
        }

        @Override
        public void onResult(GetCredentialResponse result) {
            Credential credential = result.getCredential();

            if (credential instanceof CustomCredential) {
                CustomCredential customCredential = (CustomCredential) credential;
                if (GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(customCredential.getType())) {
                    GoogleIdTokenCredential googleCred = GoogleIdTokenCredential.createFrom(customCredential.getData());
                    firebaseAuthWithGoogle(googleCred.getIdToken());
                    return;
                }
            }
            Toast.makeText(RegisterActivity.this, "Credential tidak dikenali", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(GetCredentialException e) {
            if (e instanceof NoCredentialException && usedFilterByAuthorizedAccounts) {
                startGoogleSignIn(false);
                return;
            }
            Toast.makeText(RegisterActivity.this, "Google Sign-In gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(this, "Firebase Auth gagal", Toast.LENGTH_SHORT).show();
                return;
            }

            String email = firebaseAuth.getCurrentUser() != null ? firebaseAuth.getCurrentUser().getEmail() : null;
            String name = firebaseAuth.getCurrentUser() != null ? firebaseAuth.getCurrentUser().getDisplayName() : null;

            if (email == null || email.trim().isEmpty()) {
                Toast.makeText(this, "Google login berhasil tapi email tidak terbaca", Toast.LENGTH_SHORT).show();
                return;
            }

            // Sinkron Firebase user -> SQLite
            User user = userDAO.upsertByEmail(name, "", email, "");
            if (user != null) {
                User.setActiveUser(user);
                userProfileDAO.ensureProfile(user.getId());
            }

            Intent moveToProfile = new Intent(this, ProfilePage.class);
            moveToProfile.putExtra("username", user != null ? user.getUsername() : (name != null ? name : email));
            startActivity(moveToProfile);
            finish();
        });
    }
}
