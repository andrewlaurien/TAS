package com.payvenue.txtassistant.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.payvenue.txtassistant.Helper.JSONApi;
import com.payvenue.txtassistant.Helper.ListAdapter;
import com.payvenue.txtassistant.MainActivity;
import com.payvenue.txtassistant.Model.Constants;
import com.payvenue.txtassistant.Model.Message;
import com.payvenue.txtassistant.R;

import java.util.ArrayList;
import java.util.Calendar;

public class InboxThreadFragment extends Fragment{

    public static ArrayList<Message> messages = new ArrayList<>();
    public static ListView lv;
    public static ArrayAdapter adapter;
    public static Context context;
    public static TextView noMessage;

    EditText searchTxt;
    ImageView searchBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_layout, container, false);

        context = getContext();
        lv = (ListView) view.findViewById(R.id.lvMessages);
        searchTxt = (EditText) view.findViewById(R.id.message_searchTxt);
        searchBtn = (ImageView) view.findViewById(R.id.message_searchBtn);
        noMessage = (TextView) view.findViewById(R.id.noMessage);

        if(messages.size() == 0) noMessage.setVisibility(View.VISIBLE);
        else noMessage.setVisibility(View.GONE);

        refreshList();

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = searchTxt.getText().toString().trim();
                if (search.isEmpty()) {
                    searchTxt.setError("Please enter message or status");
                    searchTxt.requestFocus();
                } else {
                    Calendar cal = Calendar.getInstance();
                    String url = Constants.url + "start=0&cmd=getMessages&select=INBOX&userID=" + MainActivity.userId + "&accountID=" +
                            MainActivity.accId + "&month=" + (cal.get(Calendar.MONTH) + 1) + "&year=" + cal.get(Calendar.YEAR) +
                            "&search=" + search;
                    JSONApi.getInstance(getContext()).getMessages(url, "InboxThreadFragment");
                    searchTxt.setText("");
                }
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                MainActivity.pd = ProgressDialog.show(getContext(), "Loading", "Please wait...", false, false);
                MainActivity.broadcastFragName = "INBOX";
                MainActivity.addedFrag++;

                Calendar cal = Calendar.getInstance();
                String url = Constants.url + "start=0&cmd=getMessages&select=INBOX&userID=" + MainActivity.userId + "&accountID=" +
                        MainActivity.accId + "&month=" + (cal.get(Calendar.MONTH)+1) + "&year=" + cal.get(Calendar.YEAR) + "&thread=" +
                        messages.get(position).messsageTo ;
                JSONApi.getInstance(getContext()).getMessages(url, "InboxFragment");
            }
        });

        return  view;
    }

    public static void refreshList(){
        adapter = new ListAdapter(context, R.layout.list_item_message_thread, messages, null);
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
