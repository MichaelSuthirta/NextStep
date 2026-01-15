package com.example.nextstep;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

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

        //Gets the extra data put in the intent moving to this page, then finds the data in database
        User activeUser = userDAO.getUserByUsername((this.getIntent()).getStringExtra("username"));
        User.setActiveUser(activeUser);
        tvName.setText(activeUser.getUsername());
    }
}