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

public class CertificateRVAdapter extends RecyclerView.Adapter<CertificateRVAdapter.ItemViewHolder>{
    ArrayList<Certificate> certList;

    public CertificateRVAdapter(ArrayList<Certificate> certList) {
        this.certList = certList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_data_view, parent, false);

        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Certificate certificate = certList.get(position);

        holder.certTitle.setText(certificate.getTitle());
        holder.certPublisher.setText(certificate.getPublisher());
        holder.certDate.setText(String.format("Issued %s - Expired %s", certificate.getPublishDate(), certificate.getExpireDate()));
    }

    @Override
    public int getItemCount() {
        if(certList == null){
            return -1;
        }
        return certList.size();
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        ImageView certImage;
        TextView certTitle, certPublisher, certDate;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            certImage = itemView.findViewById(R.id.imgView);
            certTitle = itemView.findViewById(R.id.titleView);
            certPublisher = itemView.findViewById(R.id.description);
            certDate = itemView.findViewById(R.id.smallDesc);
        }
    }
}
