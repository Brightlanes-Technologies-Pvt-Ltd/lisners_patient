package com.lisners.patient.Activity.Home;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lisners.patient.Adaptors.ChatAdapter;
import com.lisners.patient.ApiModal.ChatResModel;
import com.lisners.patient.ApiModal.ChatSendModel;
import com.lisners.patient.ApiModal.Message;
import com.lisners.patient.R;
import com.lisners.patient.apis.RetrofitApi;
import com.lisners.patient.utils.DProgressbar;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView chatRecyclerView;
    private ImageButton sendButton, btn_header_left;
    private EditText messageEditText;
    private TextView tvHeader;
    ArrayList<ChatResModel> chats;
    ChatResModel chatList;
    ChatAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        tvHeader = findViewById(R.id.tvHeader);
        btn_header_left = findViewById(R.id.btn_header_left);
        btn_header_left.setImageResource(R.drawable.ic_svg_arrow_right);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        sendButton = findViewById(R.id.sendButton);
        messageEditText = findViewById(R.id.messageEditText);

        tvHeader.setText("Helpy");
        btn_header_left.setOnClickListener(this);

        sendButton.setOnClickListener(this);

        chats = new ArrayList<>();

        chatList = new ChatResModel();
    }

    void getChatResponse(){

        chatList.setText(messageEditText.getText().toString());
        chatList.setMsg(false);
        chats.add(chatList);

        if (chats.size() == 1) {
            adapter = new ChatAdapter(chats, ChatActivity.this);
            // Attach the adapter to the recyclerview to populate items
            chatRecyclerView.setAdapter(adapter);
            // Set layout manager to position the items
            chatRecyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        }else {
            adapter.notifyItemInserted(chats.size());
        }

        Message msgModel = new Message("user", messageEditText.getText().toString());
        ArrayList<Message> msgList = new ArrayList<>();
        msgList.add(msgModel);

        messageEditText.getText().clear();

        DProgressbar dProgressbar = new DProgressbar(this);
        dProgressbar.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.chatbase.co/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitApi retrofitAPI = retrofit.create(RetrofitApi.class);

        ChatSendModel modal = new ChatSendModel(false, 0, msgList, "XVUzsZtIN6lieX_mcOiPP");

        Call<ChatResModel> call = retrofitAPI.createPost("application/json", "application/json", "Bearer eddd962d-b931-4b3b-aa71-9732f366c121",modal);

        call.enqueue(new Callback<ChatResModel>() {
            @Override
            public void onResponse(Call<ChatResModel> call, Response<ChatResModel> response) {
                dProgressbar.dismiss();

                Log.e("response", response.body().getText());
                chatList.setText(response.body().getText());
                chatList.setMsg(true);
                chats.add(chatList);

                Log.e("response0", ""+chats.get(0));
                Log.e("response1", ""+chats.get(1));
                adapter.notifyItemInserted(chats.size());

            }

            @Override
            public void onFailure(Call<ChatResModel> call, Throwable t) {

                Log.e("error", ""+t);

                dProgressbar.dismiss();
                //responseTV.setText("Error found is : " + t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sendButton:
                getChatResponse();
                break;

            case R.id.btn_header_left:
                finish();
                break;
        }
    }
}
