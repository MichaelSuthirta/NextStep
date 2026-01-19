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
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.credentials.exceptions.NoCredentialException;
import androidx.credentials.CustomCredential;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;


import androidx.appcompat.app.AppCompatActivity;

import com.example.nextstep.data_access.SQLiteConnector;
import com.example.nextstep.data_access.UserDAO;
import com.example.nextstep.data_access.UserProfileDAO;
import com.example.nextstep.models.User;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin;
    ImageButton btnGoogle, btnFacebook, btnLinkedin;
    TextView tvRegister;

    UserDAO userDAO;
    UserProfileDAO userProfileDAO;

    CredentialManager credentialManager;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        credentialManager = CredentialManager.create(this);
        firebaseAuth = FirebaseAuth.getInstance();


        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnGoogle = findViewById(R.id.btnGoogle);
        btnFacebook = findViewById(R.id.btnFacebook);
        btnLinkedin = findViewById(R.id.btnLinkedin);
        tvRegister = findViewById(R.id.tvRegister);

        userDAO = new UserDAO(SQLiteConnector.getInstance(this));
        userProfileDAO = new UserProfileDAO(SQLiteConnector.getInstance(this));

        // Make the "Register here" part look like a link
        try {
            String full = "New to our app? Register here";
            SpannableString ss = new SpannableString(full);
            int start = full.indexOf("Register here");
            if (start >= 0) {
                int end = start + "Register here".length();
                ss.setSpan(new ForegroundColorSpan(getColor(R.color.link)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            tvRegister.setText(ss);
        } catch (Exception ignored) {}

        btnLogin.setOnClickListener(v -> {
            String u = etUsername.getText().toString().trim();
            String p = etPassword.getText().toString().trim();

            if (u.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "Username & Password wajib diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = userDAO.getUserByUsername(u);
            if(user == null){
                // Many flows pass email as "username" (Google). Try email lookup too.
                user = userDAO.getUserByEmail(u);
            }
            if(user == null){
                Toast.makeText(this, "User tidak ditemukan", Toast.LENGTH_LONG).show();
                return;
            }

            // Validate password for local login
            String storedPass = user.getPassword() == null ? "" : user.getPassword();
            if(!storedPass.equals(p)){
                Toast.makeText(this, "Password salah", Toast.LENGTH_LONG).show();
                return;
            }

            User.setActiveUser(user);
            userProfileDAO.ensureProfile(user.getId());

            Intent moveToProfile = new Intent(this, ProfilePage.class);
            moveToProfile.putExtra("username", user.getUsername());
            startActivity(moveToProfile);
        });

        btnGoogle.setOnClickListener(v -> startGoogleSignIn(false));
        btnFacebook.setOnClickListener(v -> Toast.makeText(this, "Facebook Login", Toast.LENGTH_SHORT).show());
        btnLinkedin.setOnClickListener(v -> Toast.makeText(this, "LinkedIn Login", Toast.LENGTH_SHORT).show());

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));

        });

    }

    private void startGoogleSignIn(boolean filterByAuthorizedAccounts) {
        // IMPORTANT:
        // - filterByAuthorizedAccounts=true hanya akan menampilkan akun yang *pernah* authorize aplikasi ini.
        //   Kalau user baru pertama kali, hasilnya sering "No credentials found".
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

            Toast.makeText(LoginActivity.this, "Credential tidak dikenali", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(GetCredentialException e) {
            // Fallback: kalau cuma karena belum pernah login, tampilkan semua akun
            if (e instanceof NoCredentialException && usedFilterByAuthorizedAccounts) {
                startGoogleSignIn(false);
                return;
            }

            Toast.makeText(LoginActivity.this, "Google Sign-In gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
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
                moveToProfile.putExtra("username", user != null ? user.getUsername() : email);
                startActivity(moveToProfile);
                finish();
            } else {
                Toast.makeText(this, "Firebase Auth gagal", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
