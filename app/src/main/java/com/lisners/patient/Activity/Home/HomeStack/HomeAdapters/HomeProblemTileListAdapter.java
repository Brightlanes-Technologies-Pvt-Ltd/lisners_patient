package com.lisners.patient.Activity.Home.HomeStack.HomeAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lisners.patient.ApiModal.HomeProblemTileModal;
import com.lisners.patient.ApiModal.SpacializationMedel;
import com.lisners.patient.R;
import com.lisners.patient.utils.UtilsFunctions;

import java.util.ArrayList;

public class HomeProblemTileListAdapter extends RecyclerView.Adapter<HomeProblemTileListAdapter.MyViewHolder> {
    Context context;
    private LayoutInflater inflater;
    ArrayList<SpacializationMedel> tileList;
    OnClickListener listener ;

    public interface OnClickListener {
       public void  onClick(SpacializationMedel spacializationMedel, int pos);
    }

    public HomeProblemTileListAdapter(Context context,   OnClickListener listener) {
        this.context = context;
        if (context != null) {
            inflater = LayoutInflater.from(context);
            tileList = new ArrayList<>();
            this.listener = listener;
        }
    }

    public void updateList( ArrayList<SpacializationMedel> list){
       this.tileList.addAll(list);
       notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_home_problem_tiles, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        SpacializationMedel homeProblemTileModal = tileList.get(position);

        UtilsFunctions.SetLOGO(context,homeProblemTileModal.getImage(),holder.ivTile);
        holder.tvTileName.setText(UtilsFunctions.splitCamelCase(homeProblemTileModal.getTitle()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null)
                listener.onClick(homeProblemTileModal,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tileList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivTile;
        TextView tvTileName;

        public MyViewHolder(View itemView) {
            super(itemView);
            ivTile = itemView.findViewById(R.id.iv_problem);
            tvTileName = itemView.findViewById(R.id.tvTileName);
        }
    }
}
