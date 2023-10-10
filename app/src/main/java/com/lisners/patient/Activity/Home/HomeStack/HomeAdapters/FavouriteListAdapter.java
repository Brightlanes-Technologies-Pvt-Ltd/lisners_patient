package com.lisners.patient.Activity.Home.HomeStack.HomeAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lisners.patient.R;

import java.util.ArrayList;

public class FavouriteListAdapter extends RecyclerView.Adapter<FavouriteListAdapter.MyViewHolder> {
    Context context;
    private LayoutInflater inflater;
    ArrayList<String> fvtList;

    public FavouriteListAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        if (context != null) {
            inflater = LayoutInflater.from(context);
            fvtList = list;
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_review_list, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return fvtList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
