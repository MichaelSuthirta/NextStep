package com.example.nextstep.tab_pages;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nextstep.data_access.UserProfileDAO;
import com.example.nextstep.models.UserProfile;

import com.example.nextstep.AddExperienceActivity;
import com.example.nextstep.EditExperienceActivity;
import com.example.nextstep.ProfilePage;
import com.example.nextstep.R;
import com.example.nextstep.data_access.ExperienceDAO;
import com.example.nextstep.data_access.SQLiteConnector;
import com.example.nextstep.models.Experience;
import com.example.nextstep.models.User;
import com.example.nextstep.tools.ExpRVAdapter;

import java.util.ArrayList;

public class ProfileFragment extends Fragment implements ExpRVAdapter.onExpEditClickListener{
    public ProfileFragment(){};

    TextView abtMe, noExp;
    TextView skillsText;

    ImageView abtMeEdit, addExp, expEdit, skillsAdd;

    RecyclerView rvExperiences;
    ExpRVAdapter adapter;

    ExperienceDAO db;
    UserProfileDAO profileDAO;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        abtMeEdit = view.findViewById(R.id.abtMeEditBtn);
        addExp = view.findViewById(R.id.addExp);
        expEdit = view.findViewById(R.id.expEditBtn);

        abtMe = view.findViewById(R.id.aboutMeDesc);
        skillsText = view.findViewById(R.id.skillsText);
        skillsAdd = view.findViewById(R.id.skillsAddBtn);

        noExp = view.findViewById(R.id.noExp);

        db = new ExperienceDAO(SQLiteConnector.getInstance(this.getContext()));
        profileDAO = new UserProfileDAO(SQLiteConnector.getInstance(this.getContext()));

        rvExperiences = view.findViewById(R.id.rvExperience);

        ArrayList<Experience> expList = db.getUserExps(User.getActiveUser().getId());

        if(expList.isEmpty()){
            rvExperiences.setVisibility(View.GONE);
            noExp.setVisibility(View.VISIBLE);
        }
        else{
            rvExperiences.setVisibility(View.VISIBLE);
            noExp.setVisibility(View.GONE);
        }
        adapter = new ExpRVAdapter(expList, this);
        rvExperiences.setAdapter(adapter);
        rvExperiences.setLayoutManager(new LinearLayoutManager(this.getContext()));

        // Load About Me + Skills from single profile source
        bindProfile();

        abtMeEdit.setOnClickListener(v -> showEditAboutMeDialog());

        skillsAdd.setOnClickListener(v -> showEditSkillsDialog());

        addExp.setOnClickListener(
                v -> {
                    Intent addExp = new Intent(this.getContext(), AddExperienceActivity.class);
                    startActivity(addExp);
                }
        );

        expEdit.setOnClickListener(
                v -> {
                    Toast.makeText(this.getContext(), "Edit Experience clicked", Toast.LENGTH_LONG).show();
                }
        );

        return view;
    }

    private void bindProfile() {
        if (getContext() == null || User.getActiveUser() == null) return;

        UserProfile p = profileDAO.getProfile(User.getActiveUser().getId());
        String about = p.getDescription();
        if (about == null || about.trim().isEmpty()) {
            abtMe.setText("");
        } else {
            abtMe.setText(about);
        }

        if (p.getSkills() == null || p.getSkills().isEmpty()) {
            skillsText.setText("Add skills here");
        } else {
            skillsText.setText(joinWithBullet(p.getSkills()));
        }
    }

    private void showEditAboutMeDialog() {
        if (getContext() == null || User.getActiveUser() == null) return;

        final android.widget.EditText input = new android.widget.EditText(getContext());
        input.setText(abtMe.getText().toString());
        input.setMinLines(3);
        input.setPadding(24, 20, 24, 20);

        new AlertDialog.Builder(getContext())
                .setTitle("Edit About Me")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String text = input.getText().toString().trim();
                    profileDAO.updateAboutMe(User.getActiveUser().getId(), text);
                    bindProfile();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditSkillsDialog() {
        if (getContext() == null || User.getActiveUser() == null) return;

        UserProfile p = profileDAO.getProfile(User.getActiveUser().getId());
        final android.widget.EditText input = new android.widget.EditText(getContext());
        input.setHint("e.g. Software Engineer, Critical Thinking, Problem Solver");
        input.setText(UserProfileDAO.listToCsv(p.getSkills()));
        input.setPadding(24, 20, 24, 20);

        new AlertDialog.Builder(getContext())
                .setTitle("Edit My Skills")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String csv = input.getText().toString();
                    profileDAO.updateSkills(User.getActiveUser().getId(), UserProfileDAO.csvToList(csv));
                    bindProfile();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private String joinWithBullet(ArrayList<String> items) {
        StringBuilder sb = new StringBuilder();
        for (String s : items) {
            if (s == null) continue;
            String t = s.trim();
            if (t.isEmpty()) continue;
            if (sb.length() > 0) sb.append("  \u2022  ");
            sb.append(t);
        }
        return sb.toString();
    }

    @Override
    public void onResume() {
        super.onResume();
        db = new ExperienceDAO(SQLiteConnector.getInstance(this.getContext()));

        ArrayList<Experience> expList = db.getUserExps(User.getActiveUser().getId());

        if(expList.isEmpty()){
            rvExperiences.setVisibility(View.GONE);
            noExp.setVisibility(View.VISIBLE);
        }
        else{
            rvExperiences.setVisibility(View.VISIBLE);
            noExp.setVisibility(View.GONE);
        }
        adapter = new ExpRVAdapter(expList, this);
        rvExperiences.setAdapter(adapter);
        rvExperiences.setLayoutManager(new LinearLayoutManager(this.getContext()));

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onBtnClick(Experience exp) {
        Intent goToEdit = new Intent(this.getContext(), EditExperienceActivity.class);
        goToEdit.putExtra("experience", exp);
        startActivity(goToEdit);

        Log.v("DEBUG", exp.getPostId() + ", " + exp.getCompanyName());
    }
}