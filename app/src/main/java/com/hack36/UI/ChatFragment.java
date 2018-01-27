package com.hack36.UI;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.hack36.Eliza.Eliza;
import com.hack36.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatFragment extends Fragment {

    @BindView(R.id.recycleViewEliza)    ListView listView; // TODO I know :)
    @BindView(R.id.sendResponseButtonEliza) ImageButton sendButton;
    @BindView(R.id.editTextEliza) EditText messageBox;

    Eliza eliza;
    List<String> conversation;

    // TODO Store the conversation on cloud

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        ButterKnife.bind(this,rootView);
        eliza = new Eliza(getContext());

        // TODO Read previous chat also
        conversation = new ArrayList<>();

        // RecyclerView Part
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, conversation);
        listView.setAdapter(adapter);


        // Button part
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messageBox.getText() == null || messageBox.getText().toString().isEmpty())
                    Toast.makeText(getContext(), "Enter something first", Toast.LENGTH_LONG).show();
                else{
                    conversation.add(messageBox.getText().toString());
                    conversation.add(eliza.processInput(messageBox.getText().toString()));
                    adapter.notifyDataSetChanged();
                    messageBox.setText("");
                }
            }
        });
        return rootView;
    }
}
