package com.example.nextstep.tools;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nextstep.R;
import com.example.nextstep.models.Certificate;

import java.util.ArrayList;

public class CertificateRVAdapter extends RecyclerView.Adapter<CertificateRVAdapter.ItemViewHolder> {

    public interface OnCertificateActionListener {
        void onEditClicked(Certificate certificate);
        void onItemClicked(Certificate certificate);
    }

    private ArrayList<Certificate> certList;
    private boolean editMode = false;
    private final OnCertificateActionListener listener;

    public CertificateRVAdapter(ArrayList<Certificate> certList, OnCertificateActionListener listener) {
        this.certList = certList;
        this.listener = listener;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
        notifyDataSetChanged();
    }

    public void setData(ArrayList<Certificate> certList) {
        this.certList = certList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_certificate, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        if (certList == null || position < 0 || position >= certList.size()) return;

        Certificate certificate = certList.get(position);

        holder.certTitle.setText(certificate.getTitle());
        holder.certPublisher.setText(certificate.getPublisher());
        if(certificate.getExpireDate().equalsIgnoreCase("No expiration")){
            holder.certDate.setText(String.format("Issued %s - %s", certificate.getPublishDate(), certificate.getExpireDate()));
        }
        else {
            holder.certDate.setText(String.format("Issued %s - Expired %s", certificate.getPublishDate(), certificate.getExpireDate()));
        }

        holder.editIcon.setVisibility(editMode ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClicked(certificate);
        });

        holder.editIcon.setOnClickListener(v -> {
            if (listener != null) listener.onEditClicked(certificate);
        });
    }

    @Override
    public int getItemCount() {
        return certList == null ? 0 : certList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView certImage;
        ImageView editIcon;
        TextView certTitle, certPublisher, certDate;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            certImage = itemView.findViewById(R.id.imgView);
            certTitle = itemView.findViewById(R.id.titleView);
            certPublisher = itemView.findViewById(R.id.description);
            certDate = itemView.findViewById(R.id.smallDesc);
            editIcon = itemView.findViewById(R.id.editIcon);
        }
    }
}
