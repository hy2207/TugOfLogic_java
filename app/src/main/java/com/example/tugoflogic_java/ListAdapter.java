package com.example.tugoflogic_java;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {
    private ArrayList<DB_Rip> arrayList;
    private Context context;

    public ListAdapter(ArrayList<DB_Rip> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvRip;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvName = itemView.findViewById(R.id.listName);
            this.tvRip = itemView.findViewById(R.id.listRip);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION){
                        if (mListener != null){
                            mListener.onItemClick(v, pos);
                        }
                    }
                }
            });
        }
    }
    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_rip, parent, false);
        ListViewHolder holder = new ListViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        holder.tvName.setText(arrayList.get(position).playerName);
        holder.tvRip.setText(arrayList.get(position).rip);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    //click the listener
    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }
    private ListAdapter.OnItemClickListener mListener = null;
    public void setOnItemClickListener(ListAdapter.OnItemClickListener listener){
        this.mListener = listener;
    }


}
