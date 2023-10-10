package com.lisners.patient.Activity.Home.HomeStack;

import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lisners.patient.Activity.Home.AppointmentStack.AppointmentProfileFragment;
import com.lisners.patient.Activity.Home.HomeActivity;
import com.lisners.patient.Activity.Home.NotificationFragment;
import com.lisners.patient.Adaptors.TherapistListAdapter;
import com.lisners.patient.ApiModal.APIErrorModel;
import com.lisners.patient.ApiModal.SpacializationMedel;
import com.lisners.patient.ApiModal.User;
import com.lisners.patient.ApiModal.UserlistModel;
import com.lisners.patient.Functions.MySpannable;
import com.lisners.patient.GlobalData;
import com.lisners.patient.R;
import com.lisners.patient.utils.ConstantValues;
import com.lisners.patient.utils.DProgressbar;
import com.lisners.patient.utils.GetApiHandler;
import com.lisners.patient.utils.PostApiHandler;
import com.lisners.patient.utils.StoreData;
import com.lisners.patient.utils.URLs;
import com.lisners.patient.utils.UtilsFunctions;
import com.lisners.patient.zWork.adapter.TherepistListAdapter;
import com.lisners.patient.zWork.base.BasePojo;
import com.lisners.patient.zWork.commens.EndlessNestedScrollListener;
import com.lisners.patient.zWork.restApi.pojo.searchCounselor.SearchCounselorPojo;
import com.lisners.patient.zWork.restApi.repo.AppDataRepo;
import com.lisners.patient.zWork.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import timber.log.Timber;


public class HomeProblemFragment extends Fragment implements View.OnClickListener {

    TextView tvHomeHeaderSubDetail, tvShowMore, tv_sp_name, tv_show_count, tvWalletTextHeader;
    RecyclerView rvHomeProblemSpecific;

    ImageButton btn_home_header_left, btn_home_header_right, btn_filter, ib_search_br;

    ImageView iv_home_problem;
    LinearLayout lv_wallet;
    EditText edit_search;
    TextView tvHeaderUserName;
    NestedScrollView ns_scroll;
    /*UserlistModel userlist ;*/
    LinearLayoutManager linearLayoutManager;

    StoreData storeData;
   /* boolean loader =false ;
    int page = 1 ;*/

    TherepistListAdapter therapistListAdapter;
    EndlessNestedScrollListener scrollListener;
    DProgressbar dProgressbar;
    ProgressBar pb_loader;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_problem, container, false);
        storeData = new StoreData(getContext());
        init(view);

        // Inflate the layout for this fragment
        return view;
    }


    private void init(View view) {
        dProgressbar = new DProgressbar(getContext());
        pb_loader = view.findViewById(R.id.pb_loader);

        tvShowMore = view.findViewById(R.id.tvShowMore);
        btn_filter = view.findViewById(R.id.btn_filter);
        btn_home_header_left = view.findViewById(R.id.btn_home_header_left);
        tv_sp_name = view.findViewById(R.id.tv_sp_name);
        iv_home_problem = view.findViewById(R.id.iv_home_problem);
        btn_home_header_right = view.findViewById(R.id.btn_home_header_right);
        tvWalletTextHeader = view.findViewById(R.id.tvWalletTextHeader);
        tv_show_count = view.findViewById(R.id.tv_show_count);
        tvHomeHeaderSubDetail = view.findViewById(R.id.tvHomeHeaderSubDetail);
        rvHomeProblemSpecific = view.findViewById(R.id.rvHomeProblemSpecific);
        edit_search = view.findViewById(R.id.etHeaderSearch);
        lv_wallet = view.findViewById(R.id.lv_wallet);
        tvHeaderUserName = view.findViewById(R.id.tvHeaderUserName);
        ns_scroll = view.findViewById(R.id.ns_scroll);
        ib_search_br = view.findViewById(R.id.ib_search_br);
        btn_home_header_right.setOnClickListener(this);
        lv_wallet.setOnClickListener(this);
        btn_home_header_left.setOnClickListener(this);
        btn_filter.setOnClickListener(this);
        ib_search_br.setOnClickListener(this);
        tvHomeHeaderSubDetail.setVisibility(View.GONE);
        tvWalletTextHeader.setText(GlobalData.wallet);
        setAdapter();


      /*  page =1;
        userlist= new UserlistModel();*/
        edit_search.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //page = 1;
                    tv_show_count.setText("0" + " Therapist Found");

                    therapistListAdapter.isFooterShow = false;
                    therapistListAdapter.notifyItemChanged(therapistListAdapter.getListSize());
                    scrollListener.resetState();


                    therapistListAdapter.updateFirstList(new ArrayList<>());
                    getData();

                    return true;
                }
                return false;
            }
        });


        if (GlobalData.home_selected_spacialization != null)
            getSpecialistDetails();
        setProfile();
    }


    public void setAdapter() {
        Utils.setNestedScrollingEnabledFalse(rvHomeProblemSpecific);
        linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rvHomeProblemSpecific.setLayoutManager(linearLayoutManager);


        therapistListAdapter = new TherepistListAdapter(new ArrayList<>(), getContext(), new TherepistListAdapter.OnClickListener() {
            @Override
            public void onClick(User user, int pos) {
                GlobalData.bookAppointmentTherapist = user;
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Bundle args = new Bundle();
                args.putParcelable("category", GlobalData.home_selected_spacialization);

                Fragment fragment = new AppointmentProfileFragment();
                fragment.setArguments(args);
                transaction.replace(R.id.fragment_container, fragment);


                transaction.addToBackStack("HomeProblemFragment");
                transaction.commit();
            }

            @Override
            public void onFavourite(User user, int pos) {

            }
        });


        scrollListener = new EndlessNestedScrollListener(ns_scroll, (LinearLayoutManager) rvHomeProblemSpecific.getLayoutManager()) {
            public void onLoadMore(int page, int totalItemsCount, NestedScrollView view) {

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
        ns_scroll.getViewTreeObserver().addOnScrollChangedListener(scrollListener);


        rvHomeProblemSpecific.setAdapter(therapistListAdapter);
        therapistListAdapter.updateFirstList(new ArrayList<>());
    }


    private void setProfile() {
        storeData.getData(ConstantValues.USER_DATA, new StoreData.GetListener() {
            @Override
            public void getOK(String val) {
                if (val != null) {
                    User user = new Gson().fromJson(val, User.class);
                    tvHeaderUserName.setText("Hey " + UtilsFunctions.showFirstName(UtilsFunctions.splitCamelCase(user.getName())));
                }
            }

            @Override
            public void onFail() {

            }
        });
    }


    private void getData() {
        UtilsFunctions.hideKeyboard(getActivity());

        pb_loader.setVisibility(View.VISIBLE);
        //getFragmentBinding().tvNoResult.setVisibility(View.GONE);


        LiveData<BasePojo<SearchCounselorPojo>>
                liveData = AppDataRepo.getAppointments(1, edit_search.getText().toString(), GlobalData.home_selected_spacialization.getId() + "");

        liveData.observe(getViewLifecycleOwner(), response -> {
            pb_loader.setVisibility(View.GONE);
            if (response.getStatus() && response.getData() != null
                    && response.getData().getData() != null && response.getData().getData().size() > 0) {


                if (response.getData().getTotal() <= 0) {
                    tv_show_count.setText(response.getData().getTotal() + " Therapist Found");
                    //getFragmentBinding().tvNoResult.setVisibility(View.VISIBLE);
                } else {
                    tv_show_count.setText(response.getData().getTotal() + " Therapist Found");
                    //getFragmentBinding().tvNoResult.setVisibility(View.GONE);

                    therapistListAdapter.updateFirstList(response.getData().getData());
                    therapistListAdapter.isFooterShow = true;
                    therapistListAdapter.notifyItemChanged(therapistListAdapter.getListSize());
                }


            } else {
                tv_show_count.setText("0" + " Therapist Found");
                //getFragmentBinding().tvNoResult.setVisibility(View.VISIBLE);
            }

        });
    }

    private void getNextData(int page) {
        LiveData<BasePojo<SearchCounselorPojo>>
                liveData = AppDataRepo.getAppointments(page, edit_search.getText().toString(), GlobalData.home_selected_spacialization.getId() + "");


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

    /*private void getApiList() {
        UtilsFunctions.hideKeyboard(getActivity());
        loader = true;
        if (listAdapter == null)
            setAdapter();
        pb_loader.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();
        params.put("search_text", edit_search.getText().toString());
        params.put("specialization_id", GlobalData.home_selected_spacialization.getId() + "");
        PostApiHandler postApiHandler = new PostApiHandler(getContext(), URLs.GET_SEARCH_APP + "?page=" + page, params, new PostApiHandler.OnClickListener() {
            @Override
            public void onResponse(JSONObject jsonObject) throws JSONException {
                pb_loader.setVisibility(View.GONE);
                loader = false;
                if (jsonObject.has("status")) {
                    userlist = new Gson().fromJson(jsonObject.getString("data"), UserlistModel.class);
                    if (page == 1)
                        listAdapter.updateFirstList(userlist.getData());
                    else listAdapter.updateList(userlist.getData());
                    tv_show_count.setText(userlist.getTotal() + " Therapist Found");

                }
                page++;
            }

            @Override
            public void onError(ANError error) {
                loader = false;
                pb_loader.setVisibility(View.GONE);
                if (error.getErrorBody() != null) {
                    APIErrorModel apiErrorModel = new Gson().fromJson(error.getErrorBody(), APIErrorModel.class);
                    if (apiErrorModel.getMessage() != null)
                        Toast.makeText(getContext(), apiErrorModel.getMessage(), Toast.LENGTH_SHORT).show();

                }

            }
        });
        postApiHandler.execute();
    }*/

    private void getSpecialistDetails() {

        dProgressbar.show();
        GetApiHandler apiHandler = new GetApiHandler(getContext(), URLs.GET_USER_SPECIALIZATION + GlobalData.home_selected_spacialization.getId() + "?page=" + 1, new GetApiHandler.OnClickListener() {
            @Override
            public void onResponse(JSONObject jsonObject) throws JSONException {
                dProgressbar.dismiss();
                if (jsonObject.has("status") && jsonObject.has("data")) {

                    JSONObject dataObj = jsonObject.getJSONObject("data");
                    if (dataObj.has("specialization")) {
                        SpacializationMedel medel = new Gson().fromJson(dataObj.getString("specialization"), SpacializationMedel.class);
                        tvShowMore.setText(medel.getDescription());
                        makeTextViewResizable(tvShowMore, 3, "More >>", true);
                        tv_sp_name.setText(UtilsFunctions.splitCamelCase(medel.getTitle()));

                        UtilsFunctions.SetLOGO(getContext(), medel.getImage(), iv_home_problem);
                    }
                   /* if (dataObj.has("users") && loader == false) {
                        loader = true;
                        getApiList();
                    }*/

                    if (dataObj.has("users")) {
                        //page = 1;
                        tv_show_count.setText("0" + " Therapist Found");

                        therapistListAdapter.isFooterShow = false;
                        therapistListAdapter.notifyItemChanged(therapistListAdapter.getListSize());
                        scrollListener.resetState();


                        therapistListAdapter.updateFirstList(new ArrayList<>());
                        getData();
                    }
                }

            }

            @Override
            public void onError() {
                dProgressbar.dismiss();
            }
        });
        apiHandler.execute();
    }


    @Override
    public void onClick(View view) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        switch (view.getId()) {
            case R.id.btn_home_header_left:
                ((HomeActivity) getActivity()).openDrawer();
                break;

            case R.id.btn_home_header_right:
                transaction.replace(R.id.fragment_container, new NotificationFragment());
                transaction.addToBackStack("HomeFragment");
                transaction.commit();
                break;

            case R.id.lv_wallet:
                transaction.replace(R.id.fragment_container, new WalletHistoryFragment());
                transaction.addToBackStack("HomeFragment");
                transaction.commit();
                break;
            case R.id.ib_search_br:
                //page = 1;
                tv_show_count.setText("0" + " Therapist Found");

                therapistListAdapter.isFooterShow = false;
                therapistListAdapter.notifyItemChanged(therapistListAdapter.getListSize());
                scrollListener.resetState();


                therapistListAdapter.updateFirstList(new ArrayList<>());
                getData();
                break;


        }
    }

    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (tv.getLineCount() <= maxLine) {

                } else if (maxLine == 0) {
                    int lineEndIndex = tv.getLayout().getLineEnd(0);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else {
                    int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                }
            }
        });

    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                            final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {
            ssb.setSpan(new MySpannable(false) {
                @Override
                public void onClick(View widget) {
                    if (viewMore) {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, -1, "<< See Less", false);
                    } else {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
//                        tv.setTextColor(Color.parseColor("#FF0000"));
                        tv.invalidate();
                        makeTextViewResizable(tv, 3, "More >>", true);
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

    }


}