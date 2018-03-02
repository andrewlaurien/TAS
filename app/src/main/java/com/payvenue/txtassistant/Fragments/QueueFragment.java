package com.payvenue.txtassistant.Fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.payvenue.txtassistant.Helper.JSONApi;
import com.payvenue.txtassistant.Helper.ListAdapter;
import com.payvenue.txtassistant.MainActivity;
import com.payvenue.txtassistant.Model.Constants;
import com.payvenue.txtassistant.Model.Message;
import com.payvenue.txtassistant.R;

import java.util.ArrayList;
import java.util.Calendar;

public class QueueFragment extends Fragment {

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
        refreshList();

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = searchTxt.getText().toString().trim();
//                if (search.isEmpty()) {
//                    searchTxt.setError("Please enter message or status");
//                    searchTxt.requestFocus();
//                } else {
                    Calendar cal = Calendar.getInstance();
                    String url = Constants.url + "start=0&cmd=getMessages&select=QUEUE&userID=" + MainActivity.userId + "&accountID=" +
                            MainActivity.accId + "&month=" + (cal.get(Calendar.MONTH) + 1) + "&year=" + cal.get(Calendar.YEAR) +
                            "&search=" + search;
                    JSONApi.getInstance(getContext()).getMessages(url, "QueueFragment");
//                }
                searchTxt.setText("");
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {
                TextView body = (TextView) view.findViewById(R.id.message_body);
                RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.message_queue_action);
                TextView status = (TextView) view.findViewById(R.id.message_status);
                TextView cancelBtn = (TextView) view.findViewById(R.id.message_cancel_queue);

                if (relativeLayout.getVisibility() == View.VISIBLE) {
                    relativeLayout.setVisibility(View.GONE);

                    if (messages.get(position).messageBody.length() > 25)
                        body.setText(messages.get(position).messageBody.substring(0, 25) + "...");
                } else {
                    relativeLayout.setVisibility(View.VISIBLE);
                    body.setText(messages.get(position).messageBody);

                    String stat =  messages.get(position).status;

                    if(stat.equals("FS"))
                        status.setText("Status: For Sending");
                    else if(stat.equals("FPS"))
                        status.setText("Status: For Priority Sending");
                    else {
                        status.setText("Status: Draft Message");
                        cancelBtn.setVisibility(View.GONE);
                    }
                }

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Are you sure you want to cancel sending?");
                        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Calendar cal = Calendar.getInstance();
                                String url = Constants.url + "cmd=cancelMessage&messageID=" + messages.get(position).id +
                                        "&userID=" + MainActivity.userId + "&accountID=" + MainActivity.accId;
                                JSONApi.getInstance(getActivity()).cancelMessage(url, position);
                            }
                        });
                        builder.setNegativeButton("Back", null);
                        builder.show();
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
