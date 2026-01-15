package com.example.nextstep.tools;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nextstep.R;
import com.example.nextstep.models.Experience;

import java.util.ArrayList;

public class ExpRVAdapter extends RecyclerView.Adapter<ExpRVAdapter.ItemViewHolder>{
    ArrayList<Experience> expList;

    public ExpRVAdapter(ArrayList<Experience> expList) {
        this.expList = expList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_data_view, parent, false);

        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Experience exp = expList.get(position);

        holder.expTitle.setText(exp.getTitle());
        holder.expDuration.setText(String.format("%s - %s", exp.getStart(), exp.getFinish()));
        holder.expLocation.setText(exp.getLocation());
    }

    @Override
    public int getItemCount() {
        if(expList == null){
            return -1;
        }
        return expList.size();
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        TextView expTitle, expDuration, expLocation;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            expTitle = itemView.findViewById(R.id.titleView);
            expDuration = itemView.findViewById(R.id.description);
            expLocation = itemView.findViewById(R.id.smallDesc);
        }
    }
}
