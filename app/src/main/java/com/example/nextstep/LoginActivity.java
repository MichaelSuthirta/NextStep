package com.example.nextstep;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.credentials.exceptions.NoCredentialException;

import com.example.nextstep.data_access.SQLiteConnector;
import com.example.nextstep.data_access.UserDAO;
import com.example.nextstep.data_access.UserProfileDAO;
import com.example.nextstep.models.User;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin;
    ImageButton btnGoogle, btnFacebook, btnLinkedin;
    TextView tvRegister;

    UserDAO userDAO;
    UserProfileDAO userProfileDAO;

    CredentialManager credentialManager;
    FirebaseAuth firebaseAuth;

     CallbackManager fbCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNING_CERTIFICATES
            );
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                for (Signature signature : info.signingInfo.getApkContentsSigners()) {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.NO_WRAP));
                }
            }
        } catch (Exception e) {
            Log.e("KeyHash", "error", e);
        }


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

        // Link style
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
            if (user == null) user = userDAO.getUserByEmail(u);
            if (user == null) {
                Toast.makeText(this, "User tidak ditemukan", Toast.LENGTH_LONG).show();
                return;
            }

            String storedPass = user.getPassword() == null ? "" : user.getPassword();
            if (!storedPass.equals(p)) {
                Toast.makeText(this, "Password salah", Toast.LENGTH_LONG).show();
                return;
            }

            User.setActiveUser(user);
            userProfileDAO.ensureProfile(user.getId());

            Intent moveToProfile = new Intent(this, ProfilePage.class);
            moveToProfile.putExtra("username", user.getUsername());
            startActivity(moveToProfile);
        });

        // Google
        btnGoogle.setOnClickListener(v -> startGoogleSignIn(false));

        // Facebook
        initFacebookLogin();
        btnFacebook.setOnClickListener(v -> startFacebookLogin());

        btnLinkedin.setOnClickListener(v -> Toast.makeText(this, "LinkedIn Login", Toast.LENGTH_SHORT).show());

        tvRegister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    // -------------------- FACEBOOK --------------------

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
                Toast.makeText(LoginActivity.this, "Facebook login dibatalkan", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, "Facebook login error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startFacebookLogin() {
        // Minta email + public profile
        LoginManager.getInstance().logInWithReadPermissions(
                this,
                Arrays.asList("email", "public_profile")
        );
    }

    private interface FacebookProfileCallback {
        void onResult(String name, String email);
    }

    private void fetchFacebookProfile(AccessToken token, FacebookProfileCallback cb) {
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

            // Kalau email tidak tersedia dari FB (sering terjadi), buat email sintetis yang stabil
            if (email == null || email.trim().isEmpty()) {
                String uid = fu != null ? fu.getUid() : "unknown";
                email = "fb_" + uid + "@facebook.local";
            }

            User user = userDAO.upsertByEmail(name, "", email, "");
            if (user != null) {
                User.setActiveUser(user);
                userProfileDAO.ensureProfile(user.getId());
            }

            Intent moveToProfile = new Intent(this, ProfilePage.class);
            moveToProfile.putExtra("username", user != null ? user.getUsername() : email);
            startActivity(moveToProfile);
            finish();
        });
    }

    private static String firstNonEmpty(String... values) {
        if (values == null) return null;
        for (String v : values) {
            if (v != null && !v.trim().isEmpty()) return v;
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (fbCallbackManager != null) {
            fbCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // -------------------- GOOGLE (punya kamu) --------------------

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
                new LoginActivity.CredentialManagerCallback(filterByAuthorizedAccounts)
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
