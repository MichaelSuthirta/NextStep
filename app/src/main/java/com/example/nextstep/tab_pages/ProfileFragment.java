package com.example.nextstep.tab_pages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nextstep.AddExperienceActivity;
import com.example.nextstep.ProfilePage;
import com.example.nextstep.R;
import com.example.nextstep.models.Experience;
import com.example.nextstep.tools.ExpRVAdapter;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {
    public ProfileFragment(){};

    TextView abtMe;

    ImageView abtMeEdit, addExp, expEdit;

    RecyclerView rvExperiences;
    ExpRVAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        abtMeEdit = view.findViewById(R.id.abtMeEditBtn);
        addExp = view.findViewById(R.id.addExp);
        expEdit = view.findViewById(R.id.expEditBtn);

        rvExperiences = view.findViewById(R.id.rvExperience);

        ArrayList<Experience> expList = new ArrayList<>();
        expList.add(new Experience("", "", "Test", "a", "b", "tes"));
        expList.add(new Experience("", "", "Test2", "a", "b", "tes"));
        expList.add(new Experience("", "", "Test3", "a", "b", "tes"));

        adapter = new ExpRVAdapter(expList);
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


}