package com.example.nextstep.tab_pages;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

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

    ImageView abtMeEdit, addExp, expEdit;

    RecyclerView rvExperiences;
    ExpRVAdapter adapter;

    ExperienceDAO db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        abtMeEdit = view.findViewById(R.id.abtMeEditBtn);
        addExp = view.findViewById(R.id.addExp);
        expEdit = view.findViewById(R.id.expEditBtn);

        noExp = view.findViewById(R.id.noExp);

        db = new ExperienceDAO(SQLiteConnector.getInstance(this.getContext()));

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

        abtMeEdit.setOnClickListener(
                v -> {
                    Toast.makeText(this.getContext(), "About me edit clicked", Toast.LENGTH_LONG).show();
                }
        );

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