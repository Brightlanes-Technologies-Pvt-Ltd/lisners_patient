package com.lisners.patient.Activity.Auth;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lisners.patient.R;
import com.lisners.patient.utils.GetApiHandler;

public class TermsAndCondWebView extends AppCompatActivity implements View.OnClickListener {
    private ImageButton back_btn;
    private TextView header_title ;
    private WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_cond_webview);

        header_title = findViewById(R.id.tvHeader);
        back_btn = findViewById(R.id.btn_header_left);
        back_btn.setImageResource(R.drawable.ic_svg_arrow_right);

        webView = findViewById(R.id.webView);

        header_title.setText("Terms And Conditions");

        String webViewString = getIntent().getStringExtra("webViewString");

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(webViewString);

        back_btn.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_header_left:
                finish();
                break;
        }
    }
}
