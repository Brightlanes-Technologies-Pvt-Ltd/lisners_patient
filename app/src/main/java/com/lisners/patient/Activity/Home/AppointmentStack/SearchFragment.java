package com.lisners.patient.Activity.Home.AppointmentStack;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.error.ANError;
import com.google.gson.Gson;
import com.lisners.patient.Activity.Home.HomeActivity;
import com.lisners.patient.Activity.Home.HomeStack.AdvanceSearchFragment;
import com.lisners.patient.Adaptors.TherapistListAdapter;
import com.lisners.patient.ApiModal.APIErrorModel;
import com.lisners.patient.ApiModal.User;
import com.lisners.patient.ApiModal.UserlistModel;
import com.lisners.patient.GlobalData;
import com.lisners.patient.R;
import com.lisners.patient.utils.DProgressbar;
import com.lisners.patient.utils.PostApiHandler;
import com.lisners.patient.utils.URLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchFragment extends Fragment implements View.OnClickListener {
    EditText edit_search;
    RecyclerView rv_list;
    ImageButton btn_filter ,  search_icon ,btn_home_header_left;
    TextView tv_no_result, tv_show_count;
    DProgressbar dProgressbar;
    TherapistListAdapter listAdapter;
    LinearLayoutManager linearLayoutManager;
    ProgressBar pb_loader;
    UserlistModel userlistModel ;
    int page = 1;
    boolean loader = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_search, container, false);
        edit_search = view.findViewById(R.id.etHeaderSearch);
        dProgressbar = new DProgressbar(getContext());

        tv_show_count = view.findViewById(R.id.tv_show_count);
        rv_list = view.findViewById(R.id.rv_search);
        search_icon = view.findViewById(R.id.search_icon);
        pb_loader = view.findViewById(R.id.pb_loader);
        btn_home_header_left = view.findViewById(R.id.btn_home_header_left);
        tv_no_result = view.findViewById(R.id.tv_no_result);
        btn_filter = view.findViewById(R.id.btn_filter);
        page=1;
        linearLayoutManager = new LinearLayoutManager(getContext());
        btn_home_header_left.setOnClickListener(this);
        userlistModel = new UserlistModel();
        rv_list.setLayoutManager(linearLayoutManager);
        listAdapter = new TherapistListAdapter(getContext(), new TherapistListAdapter.OnClickListener() {
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

        rv_list.setAdapter(listAdapter);
        listAdapter.updateFirstList(new ArrayList<>());
        rv_list.addOnScrollListener(recyclerViewOnScrollListener);

        edit_search.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    page = 1;
                    getSearchApi();
                    return true;
                }
                return false;
            }
        });
        search_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page = 1;
                getSearchApi();

            }
        });

        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilter();
            }
        });

        getSearchApi();
        return view;
    }

    private void openFilter()
    {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new AdvanceSearchFragment());
        transaction.addToBackStack("SearchActivity");
        transaction.commit();
    }
    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = linearLayoutManager.getChildCount();
            int totalItemCount = linearLayoutManager.getItemCount();
            int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0 && !loader && page<=userlistModel.getLast_page()
            ) {
                page++;
                getSearchApi();
            }

        }
    };

    private void getSearchApi() {
        String st_search = edit_search.getText().toString();
        String URL = GlobalData.advanceParams!=null?URLs.GET_SEARCH_ADVANCE+"?page="+page: URLs.GET_SEARCH_APP+"?page="+page;

        if (page <= 1)
            dProgressbar.show();
        else if(page<=userlistModel.getLast_page())
            pb_loader.setVisibility(View.VISIBLE);
        loader = true;

        Map<String, String> params = new HashMap<>();
        params.put("search_text", st_search);
        if(GlobalData.advanceParams!=null)
            params =  GlobalData.advanceParams ;

        Log.e("PARAMS",URL+" "+new Gson().toJson(params));
        PostApiHandler handler = new PostApiHandler(getContext(),URL, params, new PostApiHandler.OnClickListener() {
            @Override
            public void onResponse(JSONObject jsonObject) throws JSONException {

                dProgressbar.dismiss();

                pb_loader.setVisibility(View.GONE);
                if (jsonObject.has("data")) {
                    userlistModel = new Gson().fromJson(jsonObject.getString("data"), UserlistModel.class);
                    if (page <= 1)
                        listAdapter.updateFirstList(userlistModel.getData());
                    else if(loader==true) {
                        loader = false;
                        listAdapter.updateList(userlistModel.getData());

                    }

                    if (userlistModel.getTotal() <= 0) {
                        tv_show_count.setText(userlistModel.getTotal() + " Therapist Found");
                        rv_list.setVisibility(View.GONE);
                        tv_no_result.setVisibility(View.VISIBLE);
                    } else {
                        tv_show_count.setText(userlistModel.getTotal() + " Therapist Found");
                        rv_list.setVisibility(View.VISIBLE);
                        tv_no_result.setVisibility(View.GONE);
                    }
                } else if(page <= 1) {
                    tv_show_count.setText(0 + " Therapist Found");
                    rv_list.setVisibility(View.GONE);
                    tv_no_result.setVisibility(View.VISIBLE);
                }
                loader = false;
            }

            @Override
            public void onError(ANError anError) {
                dProgressbar.dismiss();
                if(listAdapter.getItemCount()<=0){
                    pb_loader.setVisibility(View.GONE);
                    rv_list.setVisibility(View.GONE);
                    tv_no_result.setVisibility(View.VISIBLE);
                }else if(anError.getErrorBody()!=null && page==1) {
                    APIErrorModel apiErrorModel = new Gson().fromJson(anError.getErrorBody(),APIErrorModel.class);
                    if(!apiErrorModel.isSuccess()) {
                        rv_list.setVisibility(View.GONE);
                        tv_no_result.setVisibility(View.VISIBLE);
                        tv_show_count.setText(0+ " Therapist Found");
                     }
                        else
                        Toast.makeText(getContext(), apiErrorModel.getMessage(), Toast.LENGTH_SHORT).show();

                }

                loader = false;
            }
        });
        handler.execute();

    }

    @Override
    public void onClick(View v) {
       switch (v.getId())
       {
           case R.id.btn_home_header_left :
               ((HomeActivity) getActivity()).openDrawer();
               break ;
       }
    }
}