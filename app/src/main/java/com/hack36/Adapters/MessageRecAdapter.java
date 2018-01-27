package com.hack36.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hack36.Models.MessageModel;
import com.hack36.R;

import java.util.ArrayList;

public class MessageRecAdapter extends
        RecyclerView.Adapter<MessageRecAdapter.MyViewHolder> {

    private ArrayList<MessageModel> messages;
    public MessageRecAdapter (ArrayList<MessageModel> e){
        messages=e;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
    MessageModel messageModel=messages.get(position);
    if(messageModel.getIsUser()){
        holder.mMessageToEliza.setText(messageModel.getMessage());
        holder.mMessageFromEliza.setVisibility(View.GONE);
    }else{
        holder.mMessageFromEliza.setText(messageModel.getMessage());
        holder.mMessageToEliza.setVisibility(View.GONE);

    }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mMessageFromEliza,mMessageToEliza;


        public MyViewHolder(View view) {
            super(view);
            mMessageFromEliza = view.findViewById(R.id.received_msg);
            mMessageToEliza = view.findViewById(R.id.sent_msg);

        }
    }
}
