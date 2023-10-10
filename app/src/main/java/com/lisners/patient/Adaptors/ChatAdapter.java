package com.lisners.patient.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lisners.patient.ApiModal.ChatResModel;
import com.lisners.patient.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<ChatResModel> chatMsgs;
    private Context context1;

    public ChatAdapter(List<ChatResModel> chatMsg, Context context) {
        chatMsgs = chatMsg;
        context1 = context;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtChatMsg, txtChatMsgRes;

        public ViewHolder(View itemView) {
            super(itemView);

            txtChatMsg = itemView.findViewById(R.id.txtChatMsg);
            txtChatMsgRes = itemView.findViewById(R.id.txtChatMsgRes);
        }
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_chat, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChatAdapter.ViewHolder holder, int position) {
        ChatResModel chatMsgModel = chatMsgs.get(position);

        TextView txtChatMsg = holder.txtChatMsg;
        TextView txtChatMsgRes = holder.txtChatMsgRes;

        if (chatMsgModel.isMsg()){
            txtChatMsgRes.setText(chatMsgModel.getText());
            txtChatMsgRes.setTextColor(context1.getResources().getColor(R.color.black));
            txtChatMsg.setVisibility(View.GONE);
            txtChatMsgRes.setVisibility(View.VISIBLE);
        }else {
            txtChatMsg.setTextColor(context1.getResources().getColor(R.color.white));
            txtChatMsg.setVisibility(View.VISIBLE);
            txtChatMsgRes.setVisibility(View.GONE);
            txtChatMsg.setText(chatMsgModel.getText());
        }
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return chatMsgs.size();
    }
}
