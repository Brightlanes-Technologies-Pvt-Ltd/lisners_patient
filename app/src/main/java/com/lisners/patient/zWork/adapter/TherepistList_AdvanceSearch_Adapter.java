package com.lisners.patient.zWork.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.androidnetworking.error.ANError;
import com.lisners.patient.ApiModal.User;
import com.lisners.patient.R;
import com.lisners.patient.databinding.ItemProgressBinding;
import com.lisners.patient.databinding.ItemReviewListBinding;
import com.lisners.patient.utils.PostApiHandler;
import com.lisners.patient.utils.URLs;
import com.lisners.patient.utils.UtilsFunctions;
import com.lisners.patient.zWork.adapter.viewholder.FooterViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TherepistList_AdvanceSearch_Adapter extends Adapter<ViewHolder> {


    private List<User> resultList;
    private Context context;

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    public boolean isFooterShow = false;

    OnClickListener listener;
    boolean isRemove = false;


    public interface OnClickListener {
        public void onClick(User user, int pos);

        public void onFavourite(User user, int pos);
    }

    public TherepistList_AdvanceSearch_Adapter(List<User> resultList, Context context, boolean isRemove, OnClickListener onClickListener) {
        this.resultList = resultList;
        this.context = context;
        this.listener = onClickListener;
        this.isRemove = isRemove;

    }

    public TherepistList_AdvanceSearch_Adapter(List<User> resultList, Context context, OnClickListener onClickListener) {
        this.resultList = resultList;
        this.context = context;
        this.listener = onClickListener;
        this.isRemove = false;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {

            ItemProgressBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_progress, parent, false);
            return new FooterViewHolder(binding);
        } else if (viewType == TYPE_ITEM) {
            ItemReviewListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_review_list, parent, false);
            return new GenericViewHolder(binding);
        }
        return null;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerHolder = (FooterViewHolder) holder;
            ((FooterViewHolder) holder).bind(isFooterShow);


        } else if (holder instanceof GenericViewHolder) {
            ((GenericViewHolder) holder).bind(resultList.get(position));
        }


    }


    @Override
    public int getItemViewType(int position) {
        return (position == resultList.size()) ? TYPE_FOOTER : TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        // System.out.println("I am call");
        return resultList.size() + 1;
    }


    /*
      Helpers
      _________________________________________________________________________________________________
       */
    void add(User r) {
        resultList.add(r);
        notifyItemInserted(resultList.size() - 1);
    }

    public void addAll(List<User> moveResults) {
        for (User result : moveResults) {
            add(result);
        }
    }


    public void updateList(List<User> list) {
        this.resultList.addAll(list);
        notifyDataSetChanged();
    }

    public void updateFirstList(List<User> list) {
        this.resultList.clear();
        this.resultList.addAll(list);
        notifyDataSetChanged();
    }

    public Integer getListSize() {
        return this.resultList.size();
    }


    class GenericViewHolder extends ViewHolder {

        ItemReviewListBinding binding;

        GenericViewHolder(ItemReviewListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onClick(resultList.get(getAdapterPosition()), getAdapterPosition());
                }
            });


            binding.ibFavourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    User user = resultList.get(getAdapterPosition());
                    if (user.getIs_saved() == 1)
                        deleteFav(user, getAdapterPosition());
                    else
                        saveFav(user, getAdapterPosition());
                    binding.pbLoader.setVisibility(View.VISIBLE);
                    binding.ibFavourite.setVisibility(View.GONE);
                }
            });
        }

        void bind(User user) {
            binding.pbLoader.setVisibility(View.GONE);
            binding.ibFavourite.setVisibility(View.VISIBLE);
            if (user.getIs_saved() == 1)
                binding.ibFavourite.setImageResource(R.drawable.ic_svg_heart_red);
            else
                binding.ibFavourite.setImageResource(R.drawable.ic_svg_heart);


            binding.tvUserName.setText(UtilsFunctions.splitCamelCase(user.getName()));
            binding.tvTitle.setText(UtilsFunctions.getSpecializeString(user.getSpecialization()));

            if (user.getCounselor_profile().getProfession() != null) {
                binding.tvProfession.setText(user.getCounselor_profile().getProfession().getTitle());
            }

            binding.tvAddress.setText(user.getCity());



            if (user.getCounselor_profile() != null) {
                binding.rvVideo.setVisibility(View.VISIBLE);
                binding.tvRatting.setText(user.getCounselor_profile().getVoice_call() != null ? Double.parseDouble(user.getCounselor_profile().getVoice_call()) + "₹ / session" : "");
                binding.tvRattingVideo.setText(user.getCounselor_profile().getVideo_call() != null ? Double.parseDouble(user.getCounselor_profile().getVideo_call()) + "₹ / session" : "");
            } else {
                binding.rvVideo.setVisibility(View.INVISIBLE);
                binding.tvRatting.setText("");
                binding.tvRattingVideo.setText("");
            }

            if (user.getAvg_rating() != null)
                binding.tvAvgRatting.setText(UtilsFunctions.showRatting(user.getAvg_rating()));
            if (user.getProfile_image() != null ) {
                UtilsFunctions.SetLOGO(context, user.getProfile_image(), binding.ivProfile);
                binding.tvShortName.setVisibility(View.GONE);

            } else {
                binding.tvShortName.setText(UtilsFunctions.getFistLastChar(user.getName()));
                binding.tvShortName.setVisibility(View.VISIBLE);
                UtilsFunctions.SetLOGO(context, "", binding.ivProfile);
            }


            if (user.getIs_online() == 1) {
                binding.txtOnlineStatus.setText("Online");
                binding.txtOnlineStatus.setTextColor(ContextCompat.getColor(context, R.color.green));
            } else {
                binding.txtOnlineStatus.setText("Offline");
                binding.txtOnlineStatus.setTextColor(ContextCompat.getColor(context, R.color.red));
            }

            binding.txtOnlineStatus.setVisibility(View.VISIBLE);


        }


    }


    public void saveFav(User user, int pos) {
        Map<String, String> params = new HashMap<>();
        params.put("counselor_id", user.getId() + "");
        PostApiHandler postApiHandler = new PostApiHandler(context, URLs.GET__FAV_SAVE, params, new PostApiHandler.OnClickListener() {
            @Override
            public void onResponse(JSONObject jsonObject) throws JSONException {
                if (jsonObject.has("status")) {
                    user.set_Save("1");
                    resultList.set(pos, user);
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
                        resultList.remove(pos);
                    } else {
                        user.set_Save("0");
                        resultList.set(pos, user);
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

}
