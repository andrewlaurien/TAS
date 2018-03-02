package com.payvenue.txtassistant.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.payvenue.txtassistant.Helper.JSONApi;
import com.payvenue.txtassistant.Helper.ListAdapter;
import com.payvenue.txtassistant.MainActivity;
import com.payvenue.txtassistant.Model.Constants;
import com.payvenue.txtassistant.Model.Contacts;
import com.payvenue.txtassistant.Model.Message;
import com.payvenue.txtassistant.R;

import java.util.ArrayList;
import java.util.Calendar;

public class InboxFragment  extends Fragment{

    public static ArrayList<Message> messages = new ArrayList<>();
    public static ListView lv;
    public static ArrayAdapter adapter;
    public static Context context;
    public static TextView noMessage;

    ArrayList<String> folders = new ArrayList<>();
    ArrayAdapter<String> folderAdapter;
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
                if(search.isEmpty()){
                    searchTxt.setError("Please enter message or status");
                    searchTxt.requestFocus();
                }else{
                    Calendar cal = Calendar.getInstance();
                    String url = Constants.url + "start=0&cmd=getMessages&select=INBOX&userID=" + MainActivity.userId + "&accountID=" +
                            MainActivity.accId + "&month=" + (cal.get(Calendar.MONTH)+1) + "&year=" + cal.get(Calendar.YEAR) +
                            "&search=" + search;
                    JSONApi.getInstance(getContext()).getMessages(url, "InboxFragment");
                    searchTxt.setText("");
                }
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                TextView body = (TextView) view.findViewById(R.id.message_body);
                TextView move = (TextView) view.findViewById(R.id.message_move);
                TextView reply = (TextView) view.findViewById(R.id.message_reply);
                RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.message_action);

                if (relativeLayout.getVisibility() == View.VISIBLE) {
                    relativeLayout.setVisibility(View.GONE);

                    if (messages.get(position).messageBody.length() > 25)
                        body.setText(messages.get(position).messageBody.substring(0, 25) + "...");
                } else {
                    relativeLayout.setVisibility(View.VISIBLE);
                    body.setText(messages.get(position).messageBody);
                }

                move.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final View moveDialog = getActivity().getLayoutInflater().inflate(R.layout.move_dialog, null);

                        final Spinner spnrFolderNames = (Spinner) moveDialog.findViewById(R.id.folder_spnr);
                        final Spinner spnrStatus = (Spinner) moveDialog.findViewById(R.id.folder_status);
                        Button moveBtn = (Button) moveDialog.findViewById(R.id.move_btn);

                        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setView(moveDialog);

                        final AlertDialog dialog = builder.create();
                        dialog.show();

                        folders.clear();
                        final Menu menu = MainActivity.menu.findItem(R.id.folder_group).getSubMenu();
                        for (int i = 0; i < menu.size(); i++) {
                            MenuItem item = menu.getItem(i);
                            folders.add(item.getTitle().toString());
                        }

                        final Menu subMenu = MainActivity.menu.findItem(R.id.subfolder_group).getSubMenu();

                        for (int i = 0; i < subMenu.size(); i++) {
                            MenuItem item = subMenu.getItem(i);
                            folders.add(item.getTitle().toString());
                        }

                        folderAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, folders);
                        spnrFolderNames.setAdapter(folderAdapter);

                        moveBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MenuItem item;

                                if(spnrFolderNames.getSelectedItem().toString().contains("/"))
                                    item = subMenu.getItem(spnrFolderNames.getSelectedItemPosition() - menu.size());
                                else
                                    item = menu.getItem(spnrFolderNames.getSelectedItemPosition());

                                String id = item.getItemId() + "";

                                if(!item.getTitle().toString().contains("/")) id += "00";

                                if(spnrStatus.getSelectedItem().toString().equals("OPEN")) id += "01";
                                else id += "00";

                                Calendar cal = Calendar.getInstance();
                                String url = Constants.url + "cmd=moveMessage&messageID="+ messages.get(position).id +
                                        "&userID=" + MainActivity.userId + "&folderID=" + id.substring(2) +
                                        "&accountID=" + MainActivity.accId + "&month=" + (cal.get(Calendar.MONTH)+1) +
                                        "&year=" + cal.get(Calendar.YEAR) ;
                                JSONApi.getInstance(getActivity()).moveMessage(url);
                                messages.remove(position);
                                adapter.notifyDataSetChanged();
                                dialog.dismiss();
//                                Log.e("INBOXFRAG", id);
                            }
                        });
                    }
                });

                reply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Contacts contact = new Contacts(messages.get(position).recipientName, messages.get(position).messsageTo);

                        try{
                            MainActivity.isf.contacts.clear();
                        }catch (Exception e){}

                        MainActivity.fromInboxFrag = true;
                        MainActivity.isf = new IndividualSendFragment();
                        MainActivity.isf.contacts.add(contact);
                        MainActivity.ft = MainActivity.fm.beginTransaction();
                        MainActivity.ft.replace(R.id.fragment_container, MainActivity.isf).addToBackStack("").commit();
                        MainActivity.fabBroadcast.setVisibility(View.GONE);
                        MainActivity.addedFrag++;
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
