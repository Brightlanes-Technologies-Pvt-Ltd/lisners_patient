package com.lisners.patient.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.error.ANError;
import com.lisners.patient.ApiModal.BookedAppointment;
import com.lisners.patient.ApiModal.User;
import com.lisners.patient.R;
import com.lisners.patient.utils.PostApiHandler;
import com.lisners.patient.utils.URLs;
import com.lisners.patient.utils.UtilsFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TherapistListAdapter extends RecyclerView.Adapter<TherapistListAdapter.MyViewHolder> {
    Context context;
    private LayoutInflater inflater;
    ArrayList<User> fvtList;
    OnClickListener listener;
    boolean isRemove = false;

    public interface OnClickListener {
        public void onClick(User user, int pos);

        public void onFavourite(User user, int pos);
    }

    public TherapistListAdapter(Context context, boolean isRemove, OnClickListener listener) {
        this.context = context;
        if (context != null) {
            inflater = LayoutInflater.from(context);
            this.fvtList = new ArrayList<>();
            this.listener = listener;
            this.isRemove = isRemove;
        }
    }


    public TherapistListAdapter(Context context, OnClickListener listener) {
        this.context = context;
        if (context != null) {
            inflater = LayoutInflater.from(context);
            this.fvtList = new ArrayList<>();
            this.listener = listener;
            this.isRemove = false;
        }
    }


    public void updateList(ArrayList<User> list) {
        this.fvtList.addAll(list);
        notifyDataSetChanged();
    }

    public void updateFirstList(ArrayList<User> list) {
        this.fvtList.clear();
        this.fvtList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_review_list, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        User user = fvtList.get(position);
        holder.pb_loader.setVisibility(View.GONE);
        holder.btn_fav.setVisibility(View.VISIBLE);
        if (user.getIs_saved() == 1)
            holder.btn_fav.setImageResource(R.drawable.ic_svg_heart_red);
        else
            holder.btn_fav.setImageResource(R.drawable.ic_svg_heart);


        holder.tv_user_name.setText(UtilsFunctions.splitCamelCase(user.getName()));
        //holder.tv_title.setText(user.getTitle());

        holder.tv_title.setText(UtilsFunctions.getSpecializeString(user.getSpecialization()));

        if (user.getCounselor_profile().getProfession() != null) {
            holder.tv_profession.setText(user.getCounselor_profile().getProfession().getTitle());
        }

        holder.tv_address.setText(user.getAddress());

        if (user.getCounselor_profile() != null) {
            holder.rv_video.setVisibility(View.VISIBLE);
           /* holder.tv_ratting.setText(user.getCounselor_profile().getVoice_call() != null ? Double.parseDouble(user.getCounselor_profile().getVoice_call()) * user.getCounselor_profile().getDefault_duration() + "₹ / session" : "");
            holder.tv_ratting_video.setText(user.getCounselor_profile().getVideo_call() != null ? Double.parseDouble(user.getCounselor_profile().getVideo_call()) * user.getCounselor_profile().getDefault_duration() + "₹ / session" : "");
*/
            holder.tv_ratting.setText("₹ " + (user.getCounselor_profile().getVideo_call() != null ? Double.parseDouble(user.getCounselor_profile().getVideo_call())  + "/ session" : ""));
            holder.tv_ratting_video.setText("₹ " + (user.getCounselor_profile().getVoice_call() != null ? Double.parseDouble(user.getCounselor_profile().getVoice_call())  + "/ session" : ""));


        } else {
            holder.rv_video.setVisibility(View.INVISIBLE);
            holder.tv_ratting.setText("");
            holder.tv_ratting_video.setText("");
        }

        if (user.getAvg_rating() != null)
            holder.tv_avg_ratting.setText(UtilsFunctions.showRatting(user.getAvg_rating()));
        if (user.getProfile_image() != null ) {
            UtilsFunctions.SetLOGO(context, user.getProfile_image(), holder.iv_profile);
            holder.tv_short_name.setVisibility(View.GONE);

        } else {
            holder.tv_short_name.setText(UtilsFunctions.getFistLastChar(user.getName()));
            holder.tv_short_name.setVisibility(View.VISIBLE);
            UtilsFunctions.SetLOGO(context, "", holder.iv_profile);
        }

        if (user.getIs_online() == 1) {
            holder.txt_online_status.setText("Online");
            holder.txt_online_status.setTextColor(ContextCompat.getColor(context, R.color.green));
        } else {
            holder.txt_online_status.setText("Offline");
            holder.txt_online_status.setTextColor(ContextCompat.getColor(context, R.color.red));
        }

        holder.txt_online_status.setVisibility(View.VISIBLE);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onClick(user, position);
            }
        });
        holder.btn_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getIs_saved() == 1)
                    deleteFav(user, position);
                else
                    saveFav(user, position);
                holder.pb_loader.setVisibility(View.VISIBLE);
                holder.btn_fav.setVisibility(View.GONE);
            }
        });


    }


    public void saveFav(User user, int pos) {
        Map<String, String> params = new HashMap<>();
        params.put("counselor_id", user.getId() + "");
        PostApiHandler postApiHandler = new PostApiHandler(context, URLs.GET__FAV_SAVE, params, new PostApiHandler.OnClickListener() {
            @Override
            public void onResponse(JSONObject jsonObject) throws JSONException {
                if (jsonObject.has("status")) {
                    user.set_Save("1");
                    fvtList.set(pos, user);
                }
                if (jsonObject.has("message"))
                    Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                notifyDataSetChanged();
            }

            @Override
            public void onError(ANError error) {
                notifyDataSetChanged();

            }
        });
        postApiHandler.execute();
    }


    public void deleteFav(User user, int pos) {
        Map<String, String> params = new HashMap<>();
        params.put("counselor_id", user.getId() + "");
        PostApiHandler postApiHandler = new PostApiHandler(context, URLs.GET_DELETE_FAV, params, new PostApiHandler.OnClickListener() {
            @Override
            public void onResponse(JSONObject jsonObject) throws JSONException {
                if (jsonObject.has("status")) {
                    if (isRemove) {
                        fvtList.remove(pos);
                    } else {
                        user.set_Save("0");
                        fvtList.set(pos, user);
                    }

                }
                if (jsonObject.has("message"))
                    Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
            }

            @Override
            public void onError(ANError error) {
                notifyDataSetChanged();
            }
        });
        postApiHandler.execute();
    }

    @Override
    public int getItemCount() {
        return fvtList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_user_name, tv_title,tv_profession, tv_address, tv_ratting, tv_avg_ratting, tv_short_name, tv_ratting_video;
        ImageButton btn_fav;
        ProgressBar pb_loader;
        ImageView iv_profile;
        RelativeLayout rv_video;
        TextView txt_online_status;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_user_name = itemView.findViewById(R.id.tv_user_name);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_profession = itemView.findViewById(R.id.tv_profession);
            tv_address = itemView.findViewById(R.id.tv_address);
            tv_ratting = itemView.findViewById(R.id.tv_ratting);
            btn_fav = itemView.findViewById(R.id.ib_favourite);
            pb_loader = itemView.findViewById(R.id.pb_loader);
            tv_avg_ratting = itemView.findViewById(R.id.tv_avg_ratting);
            iv_profile = itemView.findViewById(R.id.iv_profile);
            tv_short_name = itemView.findViewById(R.id.tv_short_name);
            rv_video = itemView.findViewById(R.id.rv_video);
            tv_ratting_video = itemView.findViewById(R.id.tv_ratting_video);
            txt_online_status = itemView.findViewById(R.id.txt_online_status);

        }
    }
}
