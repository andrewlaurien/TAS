package com.payvenue.txtassistant.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.payvenue.txtassistant.Helper.JSONApi;
import com.payvenue.txtassistant.Helper.ListAdapter;
import com.payvenue.txtassistant.MainActivity;
import com.payvenue.txtassistant.Model.Constants;
import com.payvenue.txtassistant.Model.Message;
import com.payvenue.txtassistant.R;

import java.util.ArrayList;

public class ConversationFragment extends Fragment{

    public static ArrayList<Message> messages = new ArrayList<>();
    public static ListView lv;
    public static ArrayAdapter adapter;
    public static Context context;
    public static TextView noMessage;
    public static String phoneNum = "";
    public static EditText messageTxt;

    ArrayList<String> folders = new ArrayList<>();
    ArrayAdapter<String> folderAdapter;
    Button sendBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        MainActivity.broadcastFragName = "Reply";
        context = getContext();
        lv = (ListView) view.findViewById(R.id.lvMessages);
        noMessage = (TextView) view.findViewById(R.id.noMessage);
        sendBtn = (Button) view.findViewById(R.id.message_send);
        messageTxt = (EditText) view.findViewById(R.id.message_text);

        lv.setDivider(null);
        MainActivity.fabBroadcast.setVisibility(View.GONE);
        if(messages.size() == 0) noMessage.setVisibility(View.VISIBLE);
        else noMessage.setVisibility(View.GONE);

        refreshList();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageTxt.getText().toString().trim();

                if(message.isEmpty()){
                    Toast.makeText(getContext(), "Empty message.", Toast.LENGTH_SHORT).show();
                }else{
                    String url = Constants.url + "individual=1&cmd=Compose&userID=" + MainActivity.userId + "&accountID=" +
                            MainActivity.accId + "&contacts=" + phoneNum + "&message=" + messageTxt.getText().toString().toString() +
                            "&priority=" + "&schedule=";
                    JSONApi.getInstance(getContext()).sendMessage(url);
                }
            }
        });

        return  view;
    }

    public static void refreshList(){
        adapter = new ListAdapter(context, R.layout.fragment_conversation, messages, null);
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
