package com.payvenue.txtassistant.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.payvenue.txtassistant.Helper.ListAdapter;
import com.payvenue.txtassistant.MainActivity;
import com.payvenue.txtassistant.Model.Message;
import com.payvenue.txtassistant.R;

import java.util.ArrayList;

public class SentFragment extends Fragment {

    public static ArrayList<Message> messages = new ArrayList<>();
    public static ListView lv;
    public static ArrayAdapter adapter;
    public static Context context;
    public static TextView noMessage;
    public static String message = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_layout, container, false);

        context = getContext();
        lv = (ListView) view.findViewById(R.id.lvMessages);
        noMessage = (TextView) view.findViewById(R.id.noMessage);

        if(messages.size() == 0) noMessage.setVisibility(View.VISIBLE);
        else noMessage.setVisibility(View.GONE);

        refreshList();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                TextView body = (TextView) view.findViewById(R.id.message_body);
                RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.message_sent_action);
                TextView forwardBtn = (TextView) view.findViewById(R.id.message_forward);

                if (relativeLayout.getVisibility() == View.VISIBLE) {
                    relativeLayout.setVisibility(View.GONE);

                    if (messages.get(position).messageBody.length() > 25)
                        body.setText(messages.get(position).messageBody.substring(0, 25) + "...");
                } else {
                    relativeLayout.setVisibility(View.VISIBLE);
                    body.setText(messages.get(position).messageBody);
                }

                forwardBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.isf = new IndividualSendFragment();
                        MainActivity.ft = MainActivity.fm.beginTransaction();
                        MainActivity.ft.replace(R.id.fragment_container, MainActivity.isf).addToBackStack("").commit();
                        MainActivity.fabBroadcast.setVisibility(View.GONE);
                        MainActivity.addedFrag++;

                        message = messages.get(position).messageBody;
                        MainActivity.fromSentFrag = true;
                    }
                });
            }
        });

        return  view;
    }

    public static void refreshList(){
        adapter = new ListAdapter(context, R.layout.list_item_message, messages, null);
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
