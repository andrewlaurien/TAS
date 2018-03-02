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
import android.widget.TextView;

import com.payvenue.txtassistant.Helper.ListAdapter;
import com.payvenue.txtassistant.Model.Message;
import com.payvenue.txtassistant.R;

import java.util.ArrayList;

public class FolderFragment extends Fragment{

    public static ArrayList<Message> messages = new ArrayList<>();
    public static ListView lv;
    public static ArrayAdapter adapter;
    public static Context context;
    public static TextView noMessage;

    ArrayList<String> folders = new ArrayList<>();
    ArrayAdapter<String> folderAdapter;

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
                TextView move = (TextView) view.findViewById(R.id.message_move);
//                RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.message_action);
//
//                if (relativeLayout.getVisibility() == View.VISIBLE) {
//                    relativeLayout.setVisibility(View.GONE);

                if (messages.get(position).messageBody.length() > 25) {
                    body.setText(messages.get(position).messageBody.substring(0, 25) + "...");
                } else {
//                    relativeLayout.setVisibility(View.VISIBLE);
                    body.setText(messages.get(position).messageBody);
                }

//                move.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        View moveDialog = getActivity().getLayoutInflater().inflate(R.layout.move_dialog, null);
//
//                        final Spinner spnrFolderNames = (Spinner) moveDialog.findViewById(R.id.folder_spnr);
//                        final Spinner spnrStatus = (Spinner) moveDialog.findViewById(R.id.folder_status);
//                        Button moveBtn = (Button) moveDialog.findViewById(R.id.move_btn);
//
//                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                        builder.setView(moveDialog);
//                        builder.show();
//
//                        final Menu menu = MainActivity.menu.findItem(R.id.folder_group).getSubMenu();
//
//                        for (int i = 0; i < menu.size(); i++) {
//                            MenuItem item = menu.getItem(i);
//                            folders.add(item.getTitle().toString());
//                        }
//
//                        folderAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, folders);
//                        spnrFolderNames.setAdapter(folderAdapter);
//
//                        moveBtn.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                MenuItem item = menu.getItem(spnrFolderNames.getSelectedItemPosition());
//                                String id = item.getItemId() + "";
//
//                                if(!item.getTitle().toString().contains("/")) id += "00";
//
//                                if(spnrStatus.getSelectedItem().toString().equals("OPEN")) id += "01";
//                                else id += "00";
//
//                                Calendar cal = Calendar.getInstance();
//                                String url = Constants.url + "cmd=moveMessage&messageID="+ messages.get(position).id +
//                                        "&userID=" + MainActivity.userId + "&folderID=" + id.substring(2) +
//                                        "&accountID=" + MainActivity.accId + "&month=" + (cal.get(Calendar.MONTH)+1) +
//                                        "&year=" + cal.get(Calendar.YEAR);
//                                JSONApi.getInstance(getActivity()).moveMessage(url);
//                                messages.remove(position);
////                                Log.e("INBOXFRAG", id);
//                            }
//                        });
//                    }
//                });

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
