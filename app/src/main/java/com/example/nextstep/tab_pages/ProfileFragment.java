package com.example.nextstep.tab_pages;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nextstep.ProfilePage;
import com.example.nextstep.R;

public class ProfileFragment extends Fragment {
    public ProfileFragment(){};

    TextView abtMe;

    ImageView abtMeEdit, addExp, expEdit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        abtMeEdit = view.findViewById(R.id.abtMeEditBtn);
        addExp = view.findViewById(R.id.addExp);
        expEdit = view.findViewById(R.id.expEditBtn);

        abtMeEdit.setOnClickListener(
                v -> {
                    Toast.makeText(this.getContext(), "About me edit clicked", Toast.LENGTH_LONG).show();
                }
        );

        addExp.setOnClickListener(
                v -> {
                    Toast.makeText(this.getContext(), "Add experience clicked", Toast.LENGTH_LONG).show();
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