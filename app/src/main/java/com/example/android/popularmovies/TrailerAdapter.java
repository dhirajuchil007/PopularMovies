package com.example.android.popularmovies;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.MyViewHolder> {
    List<TrailerObject> trailerList;
    private final TrailerAdapterOnClickHandler mClickHandler;
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView trailer_name;
        public MyViewHolder(View itemView) {
            super(itemView);
            trailer_name= itemView.findViewById(R.id.trailer_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position=getAdapterPosition();
            mClickHandler.onClick(trailerList.get(position).url);


        }
    }
    public  interface  TrailerAdapterOnClickHandler{
        void onClick(String url);
    }
    public TrailerAdapter(List<TrailerObject> list,TrailerAdapterOnClickHandler clickHandler) {
        this.trailerList=list;
        mClickHandler=clickHandler;
    }
    @NonNull
    @Override
    public TrailerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_item,parent,false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TrailerObject obj=trailerList.get(position);
        holder.trailer_name.setText(obj.name);



    }

    @Override
    public int getItemCount() {
        return trailerList.size();
    }
}
