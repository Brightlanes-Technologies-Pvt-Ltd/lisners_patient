package com.lisners.patient.Activity.Home.HomeStack;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lisners.patient.Activity.Home.AppointmentStack.SearchNewFragment;
import com.lisners.patient.Adaptors.GenderAdaptor;
import com.lisners.patient.Adaptors.Profession_In_Search_Adaptor;
import com.lisners.patient.Adaptors.Spacialization_New_Adaptor;
import com.lisners.patient.ApiModal.ModelLanguage;
import com.lisners.patient.ApiModal.SpacializationMedel;
import com.lisners.patient.GlobalData;
import com.lisners.patient.R;
import com.lisners.patient.utils.GetApiHandler;
import com.lisners.patient.utils.MultiCheckDropDwon;
import com.lisners.patient.utils.URLs;
import com.lisners.patient.zWork.restApi.pojo.ProfessionDatum;
import com.lisners.patient.zWork.restApi.viewmodel.AdvanceSearchViewModel;
import com.lisners.patient.zWork.utils.ViewModelUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdvanceSearchFragment extends Fragment implements View.OnClickListener {

    RecyclerView rv_spacialization, rv_gender, rv_profession;
    String[] strings;
    TextView tvHeader, tv_language;
    ImageButton btn_header_left;

    Spacialization_New_Adaptor spacializationAdaptor;
    ArrayList<SpacializationMedel> spacializationMedels = new ArrayList<>();

    ArrayList<ProfessionDatum> professionDatumList = new ArrayList<>();
    Profession_In_Search_Adaptor professionAdapter;

    ArrayList<ModelLanguage> modelLanguages = new ArrayList<>();
    MultiCheckDropDwon speci_dropdown;

    ProgressBar pb_loader;
    LinearLayout lv_language, lv_select_all;
    Button btn_book_new, btn_clear;
    EditText etHeaderSearch;

    AdvanceSearchViewModel adSearchVM;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_advance_search, container, false);

        adSearchVM = ViewModelUtils.getViewModel(AdvanceSearchViewModel.class, this);


        tvHeader = view.findViewById(R.id.tvHeader);
        tvHeader.setText("Advance Search");
        btn_header_left = view.findViewById(R.id.btn_header_left);
        btn_header_left.setImageResource(R.drawable.ic_svg_arrow_right);
        lv_language = view.findViewById(R.id.lv_language);
        tv_language = view.findViewById(R.id.tv_language);
        pb_loader = view.findViewById(R.id.pb_loader);
        btn_book_new = view.findViewById(R.id.btn_book_new);
        btn_clear = view.findViewById(R.id.btn_clear);
        etHeaderSearch = view.findViewById(R.id.etHeaderSearch);
        etHeaderSearch.setText(GlobalData.advanceAddress);
//        lv_select_all = view.findViewById(R.id.lv_select_all);
        btn_header_left.setOnClickListener(this);
        lv_language.setOnClickListener(this);
        tv_language.setOnClickListener(this);
        btn_clear.setOnClickListener(this);
        btn_book_new.setOnClickListener(this);
//        lv_select_all.setOnClickListener(this);
        tv_language.setText(GlobalData.st_Language);
        rv_spacialization = view.findViewById(R.id.rv_spacialization);
        rv_profession = view.findViewById(R.id.rv_profession);
        rv_gender = view.findViewById(R.id.rv_gender);

        rv_spacialization.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rv_profession.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rv_gender.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));


        strings = new String[]{"Male", "Female", "Other"};

        GenderAdaptor genderAp = new GenderAdaptor(getContext(), strings);
        rv_gender.setAdapter(genderAp);

        getSpecification();
        getProfessions();

        return view;
    }


    private void getProfessions() {
        if (GlobalData.professionDatumArrayList != null && !GlobalData.professionDatumArrayList.isEmpty()) {
            setProfession(GlobalData.professionDatumArrayList);
        } else {
            adSearchVM


                    .getProfession()
                    .observe(getViewLifecycleOwner(), response -> {


                        if (response.getStatus() && response.getData() != null) {
                            GlobalData.professionDatumArrayList = new ArrayList<>();
                            GlobalData.professionDatumArrayList.addAll(response.getData());
                            setProfession((ArrayList<ProfessionDatum>) response.getData());
                            //showMainLayout();
                        } else {
                            setProfession(new ArrayList<>());
                        }

                    });
        }

    }

    private void setProfession(ArrayList<ProfessionDatum> dataList) {
        professionDatumList.clear();
        professionDatumList.addAll(dataList);
        professionAdapter = new Profession_In_Search_Adaptor(getContext(), professionDatumList);
        rv_profession.setAdapter(professionAdapter);
        professionAdapter.notifyDataSetChanged();


    }

    private void getSpecification() {
        spacializationMedels = new ArrayList<>();
        if (GlobalData.spacializationMedels != null) {
            spacializationMedels.addAll(GlobalData.spacializationMedels);

            spacializationAdaptor = new Spacialization_New_Adaptor(getContext(), spacializationMedels);
            rv_spacialization.setAdapter(spacializationAdaptor);
            spacializationAdaptor.notifyDataSetChanged();
        } else {
            GetApiHandler apiHandler = new GetApiHandler(getContext(), URLs.GET_SPECIALIZATION, new GetApiHandler.OnClickListener() {
                @Override
                public void onResponse(JSONObject jsonObject) throws JSONException {
                    if (jsonObject.has("status") && jsonObject.has("data")) {
                        JSONArray dataArray = jsonObject.getJSONArray("data");
                        GlobalData.spacializationMedels.clear();
                        for (int i = 0; i < dataArray.length(); i++) {
                            SpacializationMedel spacializationMedel = new Gson().fromJson(dataArray.getString(i), SpacializationMedel.class);
                            GlobalData.spacializationMedels.add(spacializationMedel);
                        }
                        spacializationMedels.clear();
                        spacializationMedels.addAll(GlobalData.spacializationMedels);

                        spacializationAdaptor = new Spacialization_New_Adaptor(getContext(), spacializationMedels);
                        rv_spacialization.setAdapter(spacializationAdaptor);
                        spacializationAdaptor.notifyDataSetChanged();

                    }

                }

                @Override
                public void onError() {

                }
            });
            apiHandler.execute();


        }


    }

    public void getLanguage() {

        if (GlobalData.advanceLanguage != null) {
            modelLanguages = new ArrayList<>();
            modelLanguages.addAll(GlobalData.advanceLanguage);
            speci_dropdown = new MultiCheckDropDwon(getContext(), modelLanguages, new MultiCheckDropDwon.OnItemClickListener() {
                @Override
                public void onClick(ArrayList<ModelLanguage> selected_spaci) {
                    GlobalData.advanceLanguage = selected_spaci;
                    String s = "";
                    for (ModelLanguage lag : selected_spaci) {
                        if (lag.isCheck()) {
                            if (s.isEmpty()) s = lag.getName();
                            else s = s + ", " + lag.getName();
                        }
                    }
                    GlobalData.st_Language = s;
                    tv_language.setText(s);
                }
            });
        } else {
            modelLanguages = new ArrayList<>();
            GlobalData.advanceLanguage = new ArrayList<>();
            pb_loader.setVisibility(View.VISIBLE);
            GetApiHandler apiHandler = new GetApiHandler(getContext(), URLs.GET_LANGUAGE, new GetApiHandler.OnClickListener() {
                @Override
                public void onResponse(JSONObject jsonObject) throws JSONException {
                    Log.e("D", new Gson().toJson(jsonObject));
                    pb_loader.setVisibility(View.GONE);
                    if (jsonObject.getBoolean("status")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            ModelLanguage s = new Gson().fromJson(jsonArray.getString(i), ModelLanguage.class);
                            modelLanguages.add(s);
                        }
                        GlobalData.advanceLanguage.addAll(modelLanguages);
                        speci_dropdown = new MultiCheckDropDwon(getContext(), modelLanguages, new MultiCheckDropDwon.OnItemClickListener() {
                            @Override
                            public void onClick(ArrayList<ModelLanguage> selected_spaci) {
                                modelLanguages = selected_spaci;
                                String s = "";
                                for (ModelLanguage spacialization : selected_spaci) {
                                    if (spacialization.isCheck()) {
                                        if (s.isEmpty()) s = spacialization.getName();
                                        else s = s + ", " + spacialization.getName();
                                    }
                                }
                                tv_language.setText(s);
                                GlobalData.st_Language = s;
                            }
                        });
                    }

                }

                @Override
                public void onError() {
                    pb_loader.setVisibility(View.GONE);
                }
            });
            apiHandler.execute();
        }
    }

    public void saveSave() {
        GlobalData.advanceAddress = etHeaderSearch.getText().toString();
        GlobalData.advanceLanguage = modelLanguages;
        GlobalData.spacializationMedels = spacializationMedels;
        GlobalData.professionDatumArrayList = professionDatumList;
        Log.e("advanceGender", GlobalData.advanceGender);
        Map<String, String> params = new HashMap<>();

        if (!etHeaderSearch.getText().toString().isEmpty())
            params.put("location", GlobalData.advanceAddress);
        if (GlobalData.advanceGender != null && !GlobalData.advanceGender.isEmpty())
            params.put("gender", GlobalData.advanceGender.toLowerCase());


        int sp_prop = 0;
        int sp = 0, lnIdx = 0;

        if (GlobalData.professionDatumArrayList != null) {
            for (int i = 0; i < professionDatumList.size(); i++) {
                ProfessionDatum medel = professionDatumList.get(i);
                if (medel.isCheck()) {
                    params.put("profession_id[" + sp_prop + "]", medel.getId() + "");
                    sp_prop++;
                }
            }
        }


        if (GlobalData.spacializationMedels != null) {
            for (int i = 0; i < spacializationMedels.size(); i++) {
                SpacializationMedel medel = spacializationMedels.get(i);
                if (medel.isCheck()) {
                    params.put("specialization_id[" + sp + "]", medel.getId() + "");
                    sp++;
                }
            }
        }


        if (GlobalData.advanceLanguage != null) {
            for (int i = 0; i < modelLanguages.size(); i++) {
                ModelLanguage medel = modelLanguages.get(i);
                if (medel.isCheck()) {
                    params.put("language_id[" + lnIdx + "]", medel.getId() + "");
                    lnIdx++;
                }
            }
        }
        if (params.keySet().size() > 0)
            GlobalData.advanceParams = params;
        showResult();
    }

    private void showResult() {
        List<String> professionID = new ArrayList<>();
        List<String> specializationID = new ArrayList<>();
        List<String> languageID = new ArrayList<>();
        String location = GlobalData.advanceAddress;
        String gender = GlobalData.advanceGender.toLowerCase();

        if (GlobalData.professionDatumArrayList != null) {

            for (int i = 0; i < professionDatumList.size(); i++) {
                ProfessionDatum medel = professionDatumList.get(i);
                if (medel.isCheck()) {
                    professionID.add("" + medel.getId());
                }
            }
        }


        if (GlobalData.spacializationMedels != null) {

            for (int i = 0; i < spacializationMedels.size(); i++) {
                SpacializationMedel medel = spacializationMedels.get(i);
                if (medel.isCheck()) {
                    specializationID.add("" + medel.getId());
                }
            }
        }

        if (GlobalData.advanceLanguage != null) {
            for (int i = 0; i < modelLanguages.size(); i++) {
                ModelLanguage medel = modelLanguages.get(i);
                if (medel.isCheck()) {
                    languageID.add("" + medel.getId());
                }
            }
        }

        SearchNewFragment searchNewFragment = SearchNewFragment.newInstance(professionID, languageID, specializationID, gender, location);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, searchNewFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void onClean() {
        GlobalData.advanceAddress = "";
        etHeaderSearch.setText("");
        GlobalData.advanceLanguage = null;
        for (int i = 0; i < GlobalData.spacializationMedels.size(); i++)
            GlobalData.spacializationMedels.get(i).setCheck(false);
        if (spacializationMedels != null) {
            spacializationMedels.clear();
            spacializationMedels.addAll(GlobalData.spacializationMedels);
            spacializationAdaptor = new Spacialization_New_Adaptor(getContext(), spacializationMedels);
            rv_spacialization.setAdapter(spacializationAdaptor);
        }


        for (int i = 0; i < GlobalData.professionDatumArrayList.size(); i++)
            GlobalData.professionDatumArrayList.get(i).setCheck(false);
        if (professionDatumList != null) {
            professionDatumList.clear();
            professionDatumList.addAll(GlobalData.professionDatumArrayList);


            if (professionAdapter != null) {
                professionAdapter.createNewList(professionDatumList);
                professionAdapter.notifyDataSetChanged();
            }
        }


        for (int i = 0; i < modelLanguages.size(); i++)
            modelLanguages.get(i).setCheck(false);

        if (speci_dropdown != null) {
            speci_dropdown.updateList(modelLanguages);
        }

        tv_language.setText("");


        GlobalData.advanceGender = "";
        GlobalData.advanceParams = null;
        GlobalData.st_Language = "";


        if ((GenderAdaptor) rv_gender.getAdapter() != null) {
            ((GenderAdaptor) rv_gender.getAdapter()).notifyDataSetChanged();
        }

        //showResult();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_header_left:
                showResult();
                break;
            case R.id.tv_language:
            case R.id.lv_language:
                getLanguage();
                break;
            case R.id.btn_book_new:
                saveSave();
                break;
            case R.id.btn_clear:
                onClean();
                break;
//            case R.id.lv_select_all:
//                break;
        }
    }
}