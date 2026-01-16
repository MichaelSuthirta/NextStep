package com.example.nextstep;


import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;


import com.example.nextstep.data_access.SQLiteConnector;
import com.example.nextstep.data_access.UserDAO;
import com.example.nextstep.models.User;
import com.example.nextstep.tools.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ProfilePage extends AppCompatActivity {
    //Main profile components
    TextView tvName, tvRole;
    ImageView profilePic, banner;

    //Tabs
    TabLayout tabLayout;
    ViewPager2 profileTabs;
    ViewPagerAdapter adapter;   

    UserDAO userDAO;

    ActivityResultLauncher<String[]> pickProfileImage;
    ActivityResultLauncher<String[]> pickBannerImage;

    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        tvName = findViewById(R.id.profileName);
        tvRole = findViewById(R.id.profileRole);

        profilePic = findViewById(R.id.profilePic);
        banner = findViewById(R.id.banner);

        userDAO = new UserDAO(SQLiteConnector.getInstance(this));

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

        // Klik untuk pilih gambar
        profilePic.setOnClickListener(v -> pickProfileImage.launch(new String[]{"image/*"}));
        banner.setOnClickListener(v -> pickBannerImage.launch(new String[]{"image/*"}));

        // Load gambar terakhir yang disimpan
        loadSavedUris();
        // ====== END IMAGE PICKER SETUP ======

        //Gets the extra data put in the intent moving to this page, then finds the data in database
        User activeUser = userDAO.getUserByUsername((this.getIntent()).getStringExtra("username"));
        User.setActiveUser(activeUser);
        tvName.setText(activeUser.getUsername());
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

    // biar URI bisa dipakai lagi setelah app ditutup/dibuka
    private void takePersistablePermission(Uri uri) {
        final int flags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
        try {
            getContentResolver().takePersistableUriPermission(uri, flags);
        } catch (SecurityException ignored) {
            // beberapa device/URI bisa gak support persistable, tapi setImageURI tetap jalan saat itu juga
        }
    }

}