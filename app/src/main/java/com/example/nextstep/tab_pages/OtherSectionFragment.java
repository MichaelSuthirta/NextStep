package com.example.nextstep.tab_pages;

import android.content.Intent;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nextstep.AddSectionActivity;
import com.example.nextstep.AddSectionEntryActivity;
import com.example.nextstep.EditSectionEntryActivity;
import com.example.nextstep.R;
import com.example.nextstep.data_access.ProfileSectionDAO;
import com.example.nextstep.data_access.SQLiteConnector;
import com.example.nextstep.models.ProfileSection;
import com.example.nextstep.models.ProfileSectionEntry;
import com.example.nextstep.models.User;
import com.example.nextstep.tools.ProfileSectionRVAdapter;

import java.util.ArrayList;

public class OtherSectionFragment extends Fragment {

    public OtherSectionFragment() {}

    private TextView btnAddSection;
    private TextView noSection;
    private RecyclerView rvSections;

    private ProfileSectionDAO db;
    private ProfileSectionRVAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_other_section, container, false);

        btnAddSection = view.findViewById(R.id.btnAddSection);
        noSection = view.findViewById(R.id.noSection);
        rvSections = view.findViewById(R.id.rvSections);

        db = new ProfileSectionDAO(SQLiteConnector.getInstance(this.getContext()));

        adapter = new ProfileSectionRVAdapter(new java.util.ArrayList<>(), new ProfileSectionRVAdapter.OnSectionActionListener() {
            @Override
            public void onAddEntry(ProfileSection section) {
                Intent i = new Intent(getContext(), AddSectionEntryActivity.class);
                i.putExtra(AddSectionEntryActivity.EXTRA_SECTION_ID, section.getId());
                i.putExtra(AddSectionEntryActivity.EXTRA_SECTION_NAME, section.getName());
                startActivity(i);
            }

            @Override
            public void onEditEntry(ProfileSectionEntry entry) {
                Intent i = new Intent(getContext(), EditSectionEntryActivity.class);
                i.putExtra(EditSectionEntryActivity.EXTRA_ENTRY_ID, entry.getId());
                startActivity(i);
            }
        });

        rvSections.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSections.setAdapter(adapter);

        btnAddSection.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), AddSectionActivity.class);
            startActivity(i);
        });

        loadSections();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSections();
    }

    private void loadSections() {
        if (getContext() == null || User.getActiveUser() == null || db == null) return;

        int userId = Integer.parseInt(User.getActiveUser().getId());
        ArrayList<ProfileSection> sections = db.getUserSections(userId);

        java.util.ArrayList<ProfileSectionRVAdapter.SectionBlock> blocks = new java.util.ArrayList<>();
        for (ProfileSection s : sections) {
            blocks.add(new ProfileSectionRVAdapter.SectionBlock(s, db.getSectionEntries(s.getId())));
        }

        adapter.setData(blocks);
        boolean empty = blocks.isEmpty();
        noSection.setVisibility(empty ? View.VISIBLE : View.GONE);
        rvSections.setVisibility(empty ? View.GONE : View.VISIBLE);
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