package com.example.nextstep.tools;

import android.content.Intent;
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
    private static ArrayList<Experience> expList;
    private static  ExpRVAdapter.onExpEditClickListener listener;

    public ExpRVAdapter(ArrayList<Experience> expList, onExpEditClickListener listener) {
        this.expList = expList;
        this.listener = listener;
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
//        holder.editBtn.setOnClickListener(
//                v -> {
//                    Intent moveToEditPage = new Intent()
//                }
//        );
    }

    @Override
    public int getItemCount() {
        if(expList == null){
            return -1;
        }
        return expList.size();
    }

    public interface onExpEditClickListener{
        void onBtnClick(Experience exp);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        TextView expTitle, expDuration, expLocation;
        ImageView editBtn;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            expTitle = itemView.findViewById(R.id.titleView);
            expDuration = itemView.findViewById(R.id.description);
            expLocation = itemView.findViewById(R.id.smallDesc);
            editBtn = itemView.findViewById(R.id.editExperienceItemBtn);

            editBtn.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Experience exp = expList.get(getAdapterPosition());
                            listener.onBtnClick(exp);
                        }
                    }
            );
        }
    }
}
