package com.example.nextstep.tools;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nextstep.R;
import com.example.nextstep.models.ProfileSection;
import com.example.nextstep.models.ProfileSectionEntry;

import java.util.ArrayList;

public class ProfileSectionRVAdapter extends RecyclerView.Adapter<ProfileSectionRVAdapter.SectionHolder> {

    public interface OnSectionActionListener {
        void onAddEntry(ProfileSection section);
        void onEditEntry(ProfileSectionEntry entry);
    }

    public static class SectionBlock {
        public final ProfileSection section;
        public final ArrayList<ProfileSectionEntry> entries;
        public boolean editMode = false;

        public SectionBlock(ProfileSection section, ArrayList<ProfileSectionEntry> entries) {
            this.section = section;
            this.entries = entries;
        }
    }

    private ArrayList<SectionBlock> data;
    private final OnSectionActionListener listener;

    public ProfileSectionRVAdapter(ArrayList<SectionBlock> data, OnSectionActionListener listener) {
        this.data = data;
        this.listener = listener;
    }

    public void setData(ArrayList<SectionBlock> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SectionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile_section, parent, false);
        return new SectionHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SectionHolder holder, int position) {
        SectionBlock block = data.get(position);

        holder.title.setText(block.section.getName());

        // Inner list
        ProfileSectionEntryRVAdapter entryAdapter = new ProfileSectionEntryRVAdapter(block.entries, new ProfileSectionEntryRVAdapter.OnEntryActionListener() {
            @Override
            public void onEditClicked(ProfileSectionEntry entry) {
                if (listener != null) listener.onEditEntry(entry);
            }

            @Override
            public void onItemClicked(ProfileSectionEntry entry) {
                // Match mock: tap item also opens edit when in edit mode.
                if (block.editMode && listener != null) listener.onEditEntry(entry);
            }
        });
        entryAdapter.setEditMode(block.editMode);

        holder.entriesRv.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.entriesRv.setAdapter(entryAdapter);
        holder.entriesRv.setNestedScrollingEnabled(false);

        holder.addBtn.setVisibility(block.editMode ? View.GONE : View.VISIBLE);
        holder.editToggle.setImageResource(block.editMode ? R.drawable.ic_check : R.drawable.pencil_icon);

        holder.addBtn.setOnClickListener(v -> {
            if (listener != null) listener.onAddEntry(block.section);
        });

        holder.editToggle.setOnClickListener(v -> {
            block.editMode = !block.editMode;
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    static class SectionHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView addBtn;
        ImageView editToggle;
        RecyclerView entriesRv;

        SectionHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.sectionTitle);
            addBtn = itemView.findViewById(R.id.sectionAddBtn);
            editToggle = itemView.findViewById(R.id.sectionEditToggle);
            entriesRv = itemView.findViewById(R.id.rvSectionEntries);
        }
    }
}
