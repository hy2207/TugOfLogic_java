package com.example.tugoflogic_java;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RipAdapter extends RecyclerView.Adapter<RipAdapter.RipViewHolder> {

    private ArrayList<DB_Player> arrayList;
    private Context context;

    public RipAdapter(ArrayList<DB_Player> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    public class RipViewHolder extends RecyclerView.ViewHolder{

        TextView tvName;
        TextView tvRip;

        public RipViewHolder(@NonNull View itemView) {
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
    public RipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_rip, parent, false);
        RipViewHolder holder = new RipViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RipViewHolder holder, int position) {
        holder.tvName.setText(arrayList.get(position).name);
        holder.tvRip.setText(arrayList.get(position).playerRip);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    //click the listener
    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }
    private OnItemClickListener mListener = null;
    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }
}
