package com.lisners.patient.Activity.Home.HomeStack;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lisners.patient.Activity.Home.AppointmentStack.AppointmentProfileFragment;
import com.lisners.patient.Activity.Home.HomeActivity;
import com.lisners.patient.Activity.Home.HomeStack.HomeAdapters.FavouriteListAdapter;
import com.lisners.patient.Adaptors.TherapistListAdapter;
import com.lisners.patient.ApiModal.User;
import com.lisners.patient.ApiModal.UserlistModel;
import com.lisners.patient.GlobalData;
import com.lisners.patient.R;
import com.lisners.patient.utils.DProgressbar;
import com.lisners.patient.utils.GetApiHandler;
import com.lisners.patient.utils.PostApiHandler;
import com.lisners.patient.utils.URLs;
import com.lisners.patient.zWork.adapter.FavTherepistAdapter;
import com.lisners.patient.zWork.adapter.TherepistList_AdvanceSearch_Adapter;
import com.lisners.patient.zWork.base.BasePojo;
import com.lisners.patient.zWork.commens.EndlessRecyclerViewScrollListener;
import com.lisners.patient.zWork.restApi.pojo.searchCounselor.SearchCounselorPojo;
import com.lisners.patient.zWork.restApi.repo.AppDataRepo;
import com.lisners.patient.zWork.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import timber.log.Timber;


public class FavouriteFragment extends Fragment implements View.OnClickListener {

    FavTherepistAdapter favTherepistAdapter;
    EndlessRecyclerViewScrollListener scrollListener;


    RecyclerView rvFavourite;

    ImageButton btn_header_left;
    TextView tvHeader ,tv_no_result;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);
        init(view);
        // Inflate the layout for this fragment
        return view;
    }

    private void init(View view) {

        tvHeader = view.findViewById(R.id.tvHeader);
        btn_header_left = view.findViewById(R.id.btn_header_left);
        btn_header_left.setImageResource(R.drawable.ic_svg_header_menu);
        tv_no_result = view.findViewById(R.id.tv_no_result);
        btn_header_left.setOnClickListener(this);
        rvFavourite = view.findViewById(R.id.rvFavourite);

        tvHeader.setText("Favorite Therapists");


        //top
        Utils.setNestedScrollingEnabledFalse(rvFavourite);
        rvFavourite.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));

        scrollListener = new EndlessRecyclerViewScrollListener((LinearLayoutManager) rvFavourite.getLayoutManager()) {

            public void onLoadMore(int page, int totalItemsCount, RecyclerView recyclerView) {

                Timber.e("Page No %d", page);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getNextData(page);
                    }
                }, 0);

            }

            @Override
            public int getStartPage() {
                return 1;
            }
        };

        rvFavourite.addOnScrollListener(scrollListener);


        favTherepistAdapter = new FavTherepistAdapter(new ArrayList<>(), getContext(), new FavTherepistAdapter.OnClickListener() {
            @Override
            public void onClick(User user, int pos) {
                GlobalData.bookAppointmentTherapist = user ;
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new AppointmentProfileFragment());
                transaction.addToBackStack("HomeProblemFragment");
                transaction.commit();
            }

            @Override
            public void onFavourite(User user, int pos) {
                //pb_loader.setVisibility(View.GONE);
                //  del  pb_loader.setVisibility(View.VISIBLE);eteFav(user,pos);
            }

            @Override
            public void noResult() {
                rvFavourite.setVisibility(View.GONE);
                tv_no_result.setVisibility(View.VISIBLE);
            }
        });

        rvFavourite.setAdapter(favTherepistAdapter);
        favTherepistAdapter.updateFirstList(new ArrayList<>());



        getData();
    }

    private void getData() {



        LiveData<BasePojo<UserlistModel>> liveData;
            liveData = AppDataRepo.getFavoriteCounsellor(1);


        liveData.observe(getViewLifecycleOwner(), response -> {

            if (response.getStatus() && response.getData() != null
                    && response.getData().getData() != null && response.getData().getData().size() > 0) {


                if (response.getData().getTotal() <= 0) {
                    rvFavourite.setVisibility(View.GONE);
                    tv_no_result.setVisibility(View.VISIBLE);

                } else {
                    rvFavourite.setVisibility(View.VISIBLE);
                    tv_no_result.setVisibility(View.GONE);
                }



                /*therapistListAdapter = new AppointmentsAdapter(response.getData().getData(), getActivity(), on);
                getFragmentBinding().rvSearch.setAdapter(therapistListAdapter);*/

                favTherepistAdapter.updateFirstList(response.getData().getData());

                favTherepistAdapter.isFooterShow = true;
                favTherepistAdapter.notifyItemChanged(favTherepistAdapter.getListSize());
            } else {

                rvFavourite.setVisibility(View.GONE);
                tv_no_result.setVisibility(View.VISIBLE);
            }

        });
    }

    private void getNextData(int page) {
        LiveData<BasePojo<UserlistModel>> liveData;
            liveData = AppDataRepo.getFavoriteCounsellor(page);



        liveData.observe(getViewLifecycleOwner(), response -> {

            if (response.getStatus() && response.getData() != null
                    && response.getData().getData() != null && response.getData().getData().size() > 0) {
                favTherepistAdapter.addAll(response.getData().getData());
            } else {
                favTherepistAdapter.isFooterShow = false;
                favTherepistAdapter.notifyItemChanged(favTherepistAdapter.getListSize());
            }

        });

    }



    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_header_left:
                ((HomeActivity) getActivity()).openDrawer();
                break;
        }
    }
}