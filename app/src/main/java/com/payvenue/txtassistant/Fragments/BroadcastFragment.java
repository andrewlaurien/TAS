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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.payvenue.txtassistant.Helper.JSONApi;
import com.payvenue.txtassistant.Helper.ListAdapter;
import com.payvenue.txtassistant.MainActivity;
import com.payvenue.txtassistant.Model.Constants;
import com.payvenue.txtassistant.Model.Contacts;
import com.payvenue.txtassistant.R;

import java.util.ArrayList;

public class BroadcastFragment extends Fragment{

    public static ArrayList<Contacts> contacts = new ArrayList<>();
    public static ArrayAdapter adapter;
    public static ListView lv;
    public static Contacts contact;
    public static EditText searchTxt;
    static Context context;

    ImageView searchBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_broadcast, container, false);

        MainActivity.fabBroadcast.setVisibility(View.GONE);
        context = getContext();
        searchBtn = (ImageView) view.findViewById(R.id.searchBtn);
        searchTxt = (EditText) view.findViewById(R.id.searchTxt);
        lv = (ListView) view.findViewById(R.id.broadcast_lv);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cmd = "";

                if(MainActivity.broadcastFragName.equals("Individual")) cmd = "getContacts";
                else cmd = "getGroups";

                String url = Constants.url + "search=" + searchTxt.getText().toString().trim() +
                        "&cmd="+ cmd +"&userID=" + MainActivity.userId + "&accountID=" +
                        MainActivity.accId;
                JSONApi.getInstance(getContext()).getContacts(url, MainActivity.broadcastFragName);
            }
        });

        refreshList();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                contact = contacts.get(position);
                MainActivity.ft = MainActivity.fm.beginTransaction();
                Fragment fragment;

                if(MainActivity.broadcastFragName.equals("Individual")) {
                    MainActivity.isf.contacts.remove(contact);
                    MainActivity.isf.contacts.add(contact);
                    fragment = MainActivity.isf;
                }else {
                    MainActivity.gsf.contacts.remove(contact);
                    MainActivity.gsf.contacts.add(contact);
                    fragment = MainActivity.gsf;
                }

                if(!MainActivity.fromSendFrag) {
                    MainActivity.ft.replace(R.id.fragment_container, fragment).commit();
                }else{
                    MainActivity.fm.popBackStackImmediate();
                }

                contacts.remove(position);
                adapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    public static void refreshList(){
        adapter = new ListAdapter(context, R.layout.list_item_contact, null, contacts);
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
