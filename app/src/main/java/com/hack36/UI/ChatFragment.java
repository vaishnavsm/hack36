package com.hack36.UI;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hack36.Eliza.Eliza;
import com.hack36.Adapters.MessageRecAdapter;
import com.hack36.R;
import com.hack36.Models.MessageModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatFragment extends Fragment {

    @BindView(R.id.recycle_view) RecyclerView mRecyclerView;
    @BindView(R.id.addBtn) RelativeLayout sendButton;
    @BindView(R.id.message) EditText messageBox;

    Eliza eliza;
    ArrayList<MessageModel> conversation;

    // TODO Store the conversation on cloud

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        ButterKnife.bind(this,rootView);
        eliza = new Eliza(getContext());

        // TODO Read previous chat also
        conversation = new ArrayList<>();

        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(),1);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        final MessageRecAdapter messageRecAdapter = new MessageRecAdapter(conversation);
        mRecyclerView.setAdapter(messageRecAdapter);
        // Button part
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messageBox.getText() == null || messageBox.getText().toString().isEmpty())
                    Toast.makeText(getContext(), "Enter something first", Toast.LENGTH_LONG).show();
                else{
                    conversation.add(new MessageModel( messageBox.getText().toString(),true));
                    conversation.add(new MessageModel( eliza.processInput(messageBox.getText().toString()),false));
                    messageRecAdapter.notifyDataSetChanged();
                    messageBox.setText("");
                }
            }
        });
        return rootView;
    }




}