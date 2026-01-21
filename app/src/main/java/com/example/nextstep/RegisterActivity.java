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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.example.nextstep.data_access.SQLiteConnector;
import com.example.nextstep.data_access.UserDAO;
import com.example.nextstep.data_access.UserProfileDAO;
import com.example.nextstep.models.User;
import com.example.nextstep.tools.SessionManager;

import androidx.core.content.ContextCompat;

import org.json.JSONObject;

import java.util.Arrays;

public class RegisterActivity extends AppCompatActivity {

    Button btnRegister;
    EditText etUsername, etEmail, etPassword;
    ImageButton btnGoogle, btnFacebook;
    TextView tvLoginHere;


    UserDAO userDAO;
    UserProfileDAO userProfileDAO;

    CredentialManager credentialManager;
    FirebaseAuth firebaseAuth;

    CallbackManager fbCallbackManager;

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
                ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.link)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            tvLoginHere.setText(ss);
        } catch (Exception ignored) {}

        tvLoginHere.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        btnGoogle.setOnClickListener(v -> startGoogleSignIn(false));
        btnFacebook.setOnClickListener(v -> startFacebookLogin());

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
                            SessionManager.save(this, user);
                        }

                        Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show();
                        Intent moveToProfile = new Intent(this, ProfilePage.class);
                        moveToProfile.putExtra("username", user != null ? user.getUsername() : u);
                        moveToProfile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(moveToProfile);
                        finish();
                    });
        });
    }

    private void startGoogleSignIn(boolean filterByAuthorizedAccounts) {
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
                ContextCompat.getMainExecutor(this),
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
                SessionManager.save(this, user);
            }

            Intent moveToProfile = new Intent(this, ProfilePage.class);
            moveToProfile.putExtra("username", user != null ? user.getUsername() : (name != null ? name : email));
            moveToProfile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(moveToProfile);
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (fbCallbackManager != null) {
            fbCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private static String firstNonEmpty(String... values) {
        if (values == null) return null;
        for (String v : values) {
            if (v != null && !v.trim().isEmpty()) return v;
        }
        return null;
    }

    private void initFacebookLogin() {
        fbCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(fbCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken token = loginResult.getAccessToken();
                fetchFacebookProfile(token, (name, email) -> firebaseAuthWithFacebook(token, name, email));
            }

            @Override
            public void onCancel() {
                Toast.makeText(RegisterActivity.this, "Facebook login dibatalkan", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(RegisterActivity.this, "Facebook login error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startFacebookLogin() {
        LoginManager.getInstance().logInWithReadPermissions(
                this,
                Arrays.asList("email", "public_profile")
        );
    }

    private interface FacebookProfileCallback {
        void onResult(String name, String email);
    }

    private void fetchFacebookProfile(AccessToken token, RegisterActivity.FacebookProfileCallback cb) {
        GraphRequest request = GraphRequest.newMeRequest(token, (JSONObject object, GraphResponse response) -> {
            String email = null;
            String name = null;
            try {
                if (object != null) {
                    email = object.optString("email", null);
                    name = object.optString("name", null);
                }
            } catch (Exception ignored) {}
            cb.onResult(name, email);
        });

        Bundle params = new Bundle();
        params.putString("fields", "id,name,email");
        request.setParameters(params);
        request.executeAsync();
    }

    private void firebaseAuthWithFacebook(AccessToken token, String graphName, String graphEmail) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (!task.isSuccessful()) {
                String msg = task.getException() != null ? task.getException().getMessage() : "Firebase Auth gagal";
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                return;
            }

            FirebaseUser fu = firebaseAuth.getCurrentUser();
            String email = firstNonEmpty(graphEmail, fu != null ? fu.getEmail() : null);
            String name = firstNonEmpty(graphName, fu != null ? fu.getDisplayName() : null);

            if (email == null || email.trim().isEmpty()) {
                String uid = fu != null ? fu.getUid() : "unknown";
                email = "fb_" + uid + "@facebook.local";
            }

            User user = userDAO.upsertByEmail(name, "", email, "");
            if (user != null) {
                User.setActiveUser(user);
                userProfileDAO.ensureProfile(user.getId());
                SessionManager.save(this, user);
            }

            Intent moveToProfile = new Intent(this, ProfilePage.class);
            moveToProfile.putExtra("username", user != null ? user.getUsername() : email);
            moveToProfile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(moveToProfile);
            finish();
        });
    }
}
