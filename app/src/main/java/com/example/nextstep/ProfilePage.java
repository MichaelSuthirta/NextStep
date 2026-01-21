package com.example.nextstep;


import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;


import com.example.nextstep.data_access.SQLiteConnector;
import com.example.nextstep.data_access.UserDAO;
import com.example.nextstep.data_access.UserProfileDAO;
import com.example.nextstep.models.User;
import com.example.nextstep.tools.SessionManager;
import com.example.nextstep.tools.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.facebook.login.LoginManager;

public class ProfilePage extends AppCompatActivity {
    //Main profile components
    TextView tvName, tvRole;
    ImageView profilePic, banner;
    TextView btnLogout;

    //Tabs
    TabLayout tabLayout;
    ViewPager2 profileTabs;
    ViewPagerAdapter adapter;

    UserDAO userDAO;
    UserProfileDAO userProfileDAO;

    ActivityResultLauncher<String[]> pickProfileImage;
    ActivityResultLauncher<String[]> pickBannerImage;

    ActivityResultLauncher<Intent> editProfileLauncher;

    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        tvName = findViewById(R.id.profileName);
        tvRole = findViewById(R.id.profileRole);

        profilePic = findViewById(R.id.profilePic);
        banner = findViewById(R.id.banner);
        ImageView editBtn = findViewById(R.id.profileEditBtn);
        btnLogout = findViewById(R.id.btnLogout);

        userDAO = new UserDAO(SQLiteConnector.getInstance(this));
        userProfileDAO = new UserProfileDAO(SQLiteConnector.getInstance(this));

        tabLayout = findViewById(R.id.tabLayout);
        profileTabs = findViewById(R.id.profileTab);

        adapter = new ViewPagerAdapter(this);

        profileTabs.setAdapter(adapter);
        new TabLayoutMediator(
                tabLayout,
                profileTabs,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setIcon(adapter.getIcon(position));
                    }
                }
        ).attach();

        // ====== IMAGE PICKER SETUP ======
        sp = getSharedPreferences("profile_prefs", MODE_PRIVATE);

        pickProfileImage = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null) {
                        takePersistablePermission(uri);
                        profilePic.setImageURI(uri);
                        saveUri("profile_uri", uri);
                    }
                }
        );

        pickBannerImage = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null) {
                        takePersistablePermission(uri);
                        banner.setImageURI(uri);
                        saveUri("banner_uri", uri);
                    }
                }
        );

        profilePic.setOnClickListener(v -> pickProfileImage.launch(new String[]{"image/*"}));
        banner.setOnClickListener(v -> pickBannerImage.launch(new String[]{"image/*"}));

        loadSavedUris();
        // ====== END IMAGE PICKER SETUP ======

        // Edit profile launcher
        editProfileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Refresh header after save
                    refreshHeader();
                }
        );

        editBtn.setOnClickListener(v -> {
            Intent i = new Intent(this, EditProfileActivity.class);
            editProfileLauncher.launch(i);
        });

        // Logout
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                new AlertDialog.Builder(ProfilePage.this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setNegativeButton("Cancel", (d, which) -> d.dismiss())
                        .setPositiveButton("Logout", (d, which) -> doLogout())
                        .show();
            });
        }


        User activeUser = User.getActiveUser();
        if (activeUser == null) {
            String key = (this.getIntent()).getStringExtra("username");
            if (key == null || key.trim().isEmpty()) {
                // No active session passed. Send back to Login.
                Intent i = new Intent(this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
                return;
            }
            activeUser = userDAO.getUserByUsername(key);
            if (activeUser == null) {
                // Some flows pass email (Google / Firebase) as the key.
                activeUser = userDAO.getUserByEmail(key);
            }
            User.setActiveUser(activeUser);
        }
        refreshHeader();
    }

    private void doLogout() {
        User.setActiveUser(null);

        SessionManager.clear(this);

        try {
            getSharedPreferences("profile_prefs", MODE_PRIVATE).edit().clear().apply();
        } catch (Exception ignored) {}

        try {
            FirebaseAuth.getInstance().signOut();
        } catch (Exception ignored) {}

        try {
            LoginManager.getInstance().logOut();
        } catch (Exception ignored) {}

        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshHeader();
    }

    private void refreshHeader() {
        User activeUser = User.getActiveUser();
        if (activeUser == null) return;
        tvName.setText(activeUser.getUsername());

        String role = userProfileDAO.getProfile(activeUser.getId()).getRole();
        if (role == null || role.trim().isEmpty()) {
            tvRole.setText("Insert your roles here, e.g. Application Developer | Data Scientist");
        } else {
            tvRole.setText(role);
        }
    }

    private void saveUri(String key, Uri uri) {
        sp.edit().putString(key, uri.toString()).apply();
    }

    private void loadSavedUris() {
        String profileStr = sp.getString("profile_uri", null);
        if (profileStr != null) {
            profilePic.setImageURI(Uri.parse(profileStr));
        }

        String bannerStr = sp.getString("banner_uri", null);
        if (bannerStr != null) {
            banner.setImageURI(Uri.parse(bannerStr));
        }
    }


    private void takePersistablePermission(Uri uri) {
        final int flags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
        try {
            getContentResolver().takePersistableUriPermission(uri, flags);
        } catch (SecurityException ignored) {

        }
    }

}