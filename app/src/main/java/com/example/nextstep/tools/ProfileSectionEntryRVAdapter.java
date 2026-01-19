package com.example.nextstep.tools;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nextstep.R;
import com.example.nextstep.models.ProfileSectionEntry;

import java.util.ArrayList;

public class ProfileSectionEntryRVAdapter extends RecyclerView.Adapter<ProfileSectionEntryRVAdapter.Holder> {

    public interface OnEntryActionListener {
        void onEditClicked(ProfileSectionEntry entry);
        void onItemClicked(ProfileSectionEntry entry);
    }

    private ArrayList<ProfileSectionEntry> data;
    private final OnEntryActionListener listener;
    private boolean editMode = false;

    public ProfileSectionEntryRVAdapter(ArrayList<ProfileSectionEntry> data, OnEntryActionListener listener) {
        this.data = data;
        this.listener = listener;
    }

    public void setData(ArrayList<ProfileSectionEntry> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile_section_entry, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        ProfileSectionEntry e = data.get(position);

        holder.title.setText(safe(e.getRole()));
        holder.desc.setText(safe(e.getCompanyName()));

        String end = e.isCurrent() ? "Present" : safe(e.getEndDate());
        holder.small.setText(String.format("%s - %s", safe(e.getStartDate()), end));

        holder.editIcon.setVisibility(editMode ? View.VISIBLE : View.GONE);
        holder.editIcon.setOnClickListener(v -> {
            if (listener != null) listener.onEditClicked(e);
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClicked(e);
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView title, desc, small;
        ImageView editIcon;

        Holder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleView);
            desc = itemView.findViewById(R.id.description);
            small = itemView.findViewById(R.id.smallDesc);
            editIcon = itemView.findViewById(R.id.editIcon);
        }
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
