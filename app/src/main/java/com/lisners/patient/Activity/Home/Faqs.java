package com.lisners.patient.Activity.Home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.lisners.patient.Adaptors.FaqAdapter;
import com.lisners.patient.ApiModal.ModelFaq;
import com.lisners.patient.R;
import com.lisners.patient.utils.GetApiHandler;
import com.lisners.patient.utils.URLs;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Faqs extends AppCompatActivity {
    TextView txt_header_txt;
    ImageButton backBtn;
    RecyclerView expListView;
    ProgressBar pb_faq;
    EditText edit_search;
    FaqAdapter faqAdapter;
    ArrayList<ModelFaq> faqlist ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faqs);

        backBtn = findViewById(R.id.btn_header_left);
        backBtn.setImageResource(R.drawable.ic_svg_arrow_right);
        txt_header_txt = findViewById(R.id.tvHeader);
        txt_header_txt.setText("FAQ’s");
        edit_search = findViewById(R.id.edit_search);
        pb_faq = (ProgressBar) findViewById(R.id.pb_faq);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        expListView = findViewById(R.id.rv_faq);

        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s + "");
            }
        });

        getApiFaq();

    }

    private void filter(String text) {
        ArrayList<ModelFaq> temp = new ArrayList<>();
        if (text.isEmpty())
            temp = faqlist;
        else {
            for (ModelFaq d : faqlist) {
                if (d.getFaq_question().toLowerCase().contains(text.toLowerCase())) {
                    temp.add(d);
                }
            }
        }
        faqAdapter.updateList(temp);
    }

    private void getApiFaq() {
        if ( faqlist != null) {
            faqAdapter = new FaqAdapter(this, faqlist);
            LinearLayoutManager llm = new LinearLayoutManager(this);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            expListView.setLayoutManager(llm);
            expListView.setAdapter(faqAdapter);
            pb_faq.setVisibility(View.GONE);

        } else {
            GetApiHandler getApi = new GetApiHandler(this, URLs.URL_FAQ, new GetApiHandler.OnClickListener() {
                @Override
                public void onResponse(JSONObject jsonObject) throws JSONException {
                    if (jsonObject.has("status")) {
                        faqlist = new ArrayList<>();
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            ModelFaq modelFaq = new Gson().fromJson(jsonArray.getString(i), ModelFaq.class);
                            faqlist.add(modelFaq);
                        }
                        getApiFaq();
                    } else {

                        Toast.makeText(getApplicationContext(), jsonObject.getString("type"), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError() {

                }
            });
            getApi.execute();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.
                LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


}