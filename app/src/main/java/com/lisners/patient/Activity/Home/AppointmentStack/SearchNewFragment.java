package com.lisners.patient.Activity.Home.AppointmentStack;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lisners.patient.Activity.Home.HomeActivity;
import com.lisners.patient.Activity.Home.HomeStack.AdvanceSearchFragment;
import com.lisners.patient.ApiModal.User;
import com.lisners.patient.GlobalData;
import com.lisners.patient.R;
import com.lisners.patient.databinding.FragmentSearchNewBinding;
import com.lisners.patient.utils.DProgressbar;
import com.lisners.patient.zWork.adapter.TherepistListAdapter;
import com.lisners.patient.zWork.adapter.TherepistList_AdvanceSearch_Adapter;
import com.lisners.patient.zWork.base.BaseFragment;
import com.lisners.patient.zWork.base.BasePojo;
import com.lisners.patient.zWork.commens.EndlessRecyclerViewScrollListener;
import com.lisners.patient.zWork.restApi.pojo.searchCounselor.SearchCounselorPojo;
import com.lisners.patient.zWork.restApi.repo.AppDataRepo;
import com.lisners.patient.zWork.restApi.viewmodel.ContentsViewModel;
import com.lisners.patient.zWork.utils.Utils;
import com.lisners.patient.zWork.utils.ViewModelUtils;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class SearchNewFragment extends BaseFragment<FragmentSearchNewBinding> implements View.OnClickListener {
    ContentsViewModel contentsVM;
    TherepistList_AdvanceSearch_Adapter therapistListAdapter;

    EndlessRecyclerViewScrollListener scrollListener;
    DProgressbar dProgressbar;
    ProgressBar pb_loader;


    List<String> professionID;
    List<String> specializationID;
    List<String> languageID;
    String location;
    String gender;

    public static SearchNewFragment newInstance(List<String> professionID, List<String> languageID, List<String> specializationID, String gender, String location) {

        Bundle args = new Bundle();
        args.putStringArrayList("language_ids", (ArrayList<String>) languageID);
        args.putStringArrayList("specialization_ids", (ArrayList<String>) specializationID);
        args.putStringArrayList("profession_ids", (ArrayList<String>) professionID);
        args.putString("gender", gender);
        args.putString("location", location);

        SearchNewFragment fragment = new SearchNewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_search_new;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Bundle bundle = getArguments();
        assert bundle != null;
        professionID = bundle.getStringArrayList("profession_ids");
        specializationID = bundle.getStringArrayList("specialization_ids");
        languageID = bundle.getStringArrayList("language_ids");
        location = bundle.getString("location");
        gender = bundle.getString("gender");


        dProgressbar = new DProgressbar(getActivity());
        contentsVM = ViewModelUtils.getViewModel(ContentsViewModel.class, this);

        showMainLayout();
        initView();


    }

    private void initView() {
        getFragmentBinding().btnHomeHeaderLeft.setOnClickListener(this);


        //top
        Utils.setNestedScrollingEnabledFalse(getFragmentBinding().rvSearch);
        getFragmentBinding().rvSearch.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));

        scrollListener = new EndlessRecyclerViewScrollListener((LinearLayoutManager) getFragmentBinding().rvSearch.getLayoutManager()) {

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

        getFragmentBinding().rvSearch.addOnScrollListener(scrollListener);


        therapistListAdapter = new TherepistList_AdvanceSearch_Adapter(new ArrayList<>(), getContext(), new TherepistList_AdvanceSearch_Adapter.OnClickListener() {
            @Override
            public void onClick(User user, int pos) {
                GlobalData.bookAppointmentTherapist = user;
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new AppointmentProfileFragment());
                transaction.addToBackStack("SearchActivity");
                transaction.commit();
            }

            @Override
            public void onFavourite(User user, int pos) {
                pb_loader.setVisibility(View.GONE);
                //  del  pb_loader.setVisibility(View.VISIBLE);eteFav(user,pos);
            }
        });

        getFragmentBinding().rvSearch.setAdapter(therapistListAdapter);
        therapistListAdapter.updateFirstList(new ArrayList<>());


        getFragmentBinding().etHeaderSearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //page = 1;
                    getFragmentBinding().tvShowCount.setText("");

                    therapistListAdapter.isFooterShow = false;
                    therapistListAdapter.notifyItemChanged(therapistListAdapter.getListSize());
                    scrollListener.resetState();


                    therapistListAdapter.updateFirstList(new ArrayList<>());
                    return true;
                }
                return false;
            }
        });


        getFragmentBinding().etHeaderSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                getFragmentBinding().tvShowCount.setText("0" + " Therapist Found");

                therapistListAdapter.isFooterShow = false;
                therapistListAdapter.notifyItemChanged(therapistListAdapter.getListSize());

                scrollListener.resetState();
                therapistListAdapter.updateFirstList(new ArrayList<>());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        getFragmentBinding().searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // page = 1;
                getFragmentBinding().tvShowCount.setText("");

                therapistListAdapter.isFooterShow = false;
                therapistListAdapter.notifyItemChanged(therapistListAdapter.getListSize());

                scrollListener.resetState();
                therapistListAdapter.updateFirstList(new ArrayList<>());
                getData();

            }
        });

        getFragmentBinding().btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilter();
            }
        });


        getData();

    }


    private void getData() {
        String st_search = getFragmentBinding().etHeaderSearch.getText().toString();

        hideError();
        showProgress();


        LiveData<BasePojo<SearchCounselorPojo>> liveData;
        if (GlobalData.advanceParams != null) {
            liveData = AppDataRepo.advanceSearchCounselor(1, st_search, professionID, specializationID, languageID, gender, location);
        } else {
            liveData = AppDataRepo.searchCounselor(1, st_search, "");
        }


        liveData.observe(getViewLifecycleOwner(), response -> {
            hideProgress();
            if (response.getStatus() && response.getData() != null
                    && response.getData().getData() != null && response.getData().getData().size() > 0) {
                showMainLayout();


                if (response.getData().getTotal() <= 0) {
                    getFragmentBinding().tvShowCount.setText("No" + " Therapist Found");
                    getFragmentBinding().rvSearch.setVisibility(View.GONE);
                    getFragmentBinding().tvNoResult.setVisibility(View.VISIBLE);
                } else {
                    getFragmentBinding().tvShowCount.setText(response.getData().getTotal() + " Therapist Found");
                    getFragmentBinding().rvSearch.setVisibility(View.VISIBLE);
                    getFragmentBinding().tvNoResult.setVisibility(View.GONE);
                }



                /*therapistListAdapter = new AppointmentsAdapter(response.getData().getData(), getActivity(), on);
                getFragmentBinding().rvSearch.setAdapter(therapistListAdapter);*/

                therapistListAdapter.updateFirstList(response.getData().getData());

                therapistListAdapter.isFooterShow = true;
                therapistListAdapter.notifyItemChanged(therapistListAdapter.getListSize());
            } else {
                getFragmentBinding().tvShowCount.setText("No" + " Therapist Found");
                getFragmentBinding().rvSearch.setVisibility(View.VISIBLE);
                getFragmentBinding().tvNoResult.setVisibility(View.GONE);
            }

        });
    }

    private void getNextData(int page) {
        String st_search = getFragmentBinding().etHeaderSearch.getText().toString();
        LiveData<BasePojo<SearchCounselorPojo>> liveData;
        if (GlobalData.advanceParams != null) {
            liveData = AppDataRepo.advanceSearchCounselor(page, st_search, professionID, specializationID, languageID, gender, location);
        } else {
            liveData = AppDataRepo.searchCounselor(page, st_search, "");
        }


        liveData.observe(getViewLifecycleOwner(), response -> {

            if (response.getStatus() && response.getData() != null
                    && response.getData().getData() != null && response.getData().getData().size() > 0) {
                therapistListAdapter.addAll(response.getData().getData());
            } else {
                therapistListAdapter.isFooterShow = false;
                therapistListAdapter.notifyItemChanged(therapistListAdapter.getListSize());
            }

        });

    }


    private void openFilter() {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new AdvanceSearchFragment());
        transaction.addToBackStack("SearchActivity");
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_home_header_left:
                ((HomeActivity) getActivity()).openDrawer();
                break;
        }
    }


}


