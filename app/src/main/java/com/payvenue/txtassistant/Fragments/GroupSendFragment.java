package com.payvenue.txtassistant.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.payvenue.txtassistant.Helper.JSONApi;
import com.payvenue.txtassistant.MainActivity;
import com.payvenue.txtassistant.Model.Constants;
import com.payvenue.txtassistant.Model.Contacts;
import com.payvenue.txtassistant.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.Calendar;

public class GroupSendFragment extends Fragment implements DatePickerDialog.OnDateSetListener{

    public static ArrayList<Contacts> contacts = new ArrayList<>();
    FlowLayout contactContainer;
    LayoutInflater inflater;
    String TAG = "GroupSendFragment";

    String groupNames = "";
    ImageView addContactBtn, addNumberBtn;
    EditText messageTxt;
    TextView charCtr;
    Button sendBtn;
    CheckBox priorityCb, schedCb;
    String priority="";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_broadcast_send, container, false);

        this.inflater = inflater;
        contactContainer = (FlowLayout) view.findViewById(R.id.send_to_frame);
        addContactBtn = (ImageView) view.findViewById(R.id.add_contact);
        addNumberBtn = (ImageView) view.findViewById(R.id.add_number);
        messageTxt = (EditText) view.findViewById(R.id.message_text);
        charCtr = (TextView) view.findViewById(R.id.message_charCount);
        sendBtn = (Button) view.findViewById(R.id.message_send);
        priorityCb = (CheckBox) view.findViewById(R.id.message_priority);
        schedCb = (CheckBox) view.findViewById(R.id.message_sched);

        addNumberBtn.setVisibility(View.INVISIBLE);

        if(priorityCb.isChecked()) priority = "FPS";
        else priority = "FS";

        Log.e(TAG, contacts.size()+"");
        groupNames = "";
        for(int i = 0; i<contacts.size(); i++) {
            displayContacts(contacts.get(i));
        }

        messageTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int messagePartCtr = messageTxt.getText().length() / 160 + 1;
                charCtr.setText(messageTxt.getText().length() + "/" + messagePartCtr);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        addContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.fromSendFrag = true;
                MainActivity.ft = MainActivity.fm.beginTransaction();
                MainActivity.ft.replace(R.id.fragment_container, MainActivity.bf).addToBackStack(null).commit();
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messageTxt.getText().toString().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Message is empty", Toast.LENGTH_SHORT).show();
                    messageTxt.requestFocus();
                } else if(groupNames.trim().equals("")){
                    Toast.makeText(getContext(), "Recipient is empty", Toast.LENGTH_SHORT).show();
                } else {
                    if (schedCb.isChecked()) {
                        Calendar now = Calendar.getInstance();
                        DatePickerDialog dpd = DatePickerDialog.newInstance(
                                GroupSendFragment.this,
                                now.get(Calendar.YEAR),
                                now.get(Calendar.MONTH),
                                now.get(Calendar.DAY_OF_MONTH)
                        );
                        dpd.setMinDate(now);
                        dpd.show(getActivity().getFragmentManager(), "Pick Schedule");
                    } else {
                        String url = Constants.url + "individual=0&cmd=Compose&userID=" + MainActivity.userId + "&accountID=" +
                                MainActivity.accId + "&contacts=" + groupNames + "&message=" + messageTxt.getText().toString().toString() +
                                "&priority=" + priority + "&schedule=";
                        //Log.e(TAG, url);
                        JSONApi.getInstance(getContext()).sendMessage(url);
                    }
                }
            }
        });

        return view;
    }

    public void displayContacts(final Contacts contact){
        groupNames += contact.name + ",";

        final View contactView = inflater.inflate(R.layout.contact_item, null);
        contactView.setTag(contact);

        TextView tv = (TextView) contactView.findViewById(R.id.contact_item_name);
        ImageView remove = (ImageView) contactView.findViewById(R.id.contact_item_remove);

        tv.setText(contact.name);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contacts.remove(contactView.getTag());
                contactContainer.removeView(contactView);
                groupNames = groupNames.replace(contact.name + ",", "");
                Log.e(TAG, groupNames);
            }
        });


        contactContainer.addView(contactView);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        String sched = year + "-" + String.format("%02d", monthOfYear + 1) + "-" +
                String.format("%02d", dayOfMonth) + " 00:00:00";

        String url = Constants.url + "individual=0&cmd=Compose&userID=" + MainActivity.userId + "&accountID=" +
                MainActivity.accId + "&contacts=" + groupNames + "&message=" + messageTxt.getText().toString().toString() +
                "&priority=" + priority + "&schedule="+sched;
        //Log.e(TAG, url);
        JSONApi.getInstance(getContext()).sendMessage(url);
    }
}
