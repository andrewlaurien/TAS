package com.payvenue.txtassistant.Helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.payvenue.txtassistant.MainActivity;
import com.payvenue.txtassistant.Model.Contacts;
import com.payvenue.txtassistant.Model.Message;
import com.payvenue.txtassistant.R;

import java.util.List;

public class ListAdapter extends ArrayAdapter{

    Context context;
    List<Message> messages;
    List<Contacts> contacts;
    int layoutId;

    public ListAdapter(Context context, int resource, List messages, List contacts) {
        super(context, resource);

        this.context = context;
        this.messages = messages;
        this.contacts = contacts;
        layoutId = resource;
    }

    @Override
    public int getCount() {
        int size = 0;

        if (messages != null) size = messages.size();
        else if (contacts != null) size = contacts.size();

        return size;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(layoutId, parent, false);

        if(layoutId == R.layout.list_item_message){
            TextView title = (TextView) convertView.findViewById(R.id.message_title);
            TextView time = (TextView) convertView.findViewById(R.id.message_date);
            TextView body = (TextView) convertView.findViewById(R.id.message_body);

            String[] date = messages.get(position).dateInit.split(" ");

//            if(MainActivity.broadcastFragName.equals("INBOX")) title.setText(messages.get(position).threadId);
//            else

//            if(messages.get(position).recipientName.length() > 20)
//                title.setText(messages.get(position).recipientName.substring(0, 20) + "...");
//            else
                title.setText(messages.get(position).recipientName);

            time.setText(date[0]);

            if(messages.get(position).messageBody.length() > 25)
                body.setText(messages.get(position).messageBody.substring(0, 25) + "...");
            else
                body.setText(messages.get(position).messageBody);

        }else if(layoutId == R.layout.list_item_contact){
            TextView image = (TextView) convertView.findViewById(R.id.contact_image);
            TextView name = (TextView) convertView.findViewById(R.id.contact_name);
            //TextView number = (TextView) convertView.findViewById(R.id.contact_number);

            image.setText(contacts.get(position).name.substring(0,1));
            name.setText(contacts.get(position).name);
            //number.setText(contacts.get(position).number);
        }if(layoutId == R.layout.list_item_message_thread){
            TextView name = (TextView) convertView.findViewById(R.id.message_title);
            TextView num = (TextView) convertView.findViewById(R.id.message_body);

            name.setText(messages.get(position).recipientName);
            num.setText(messages.get(position).messsageTo);

        }else if(layoutId == R.layout.fragment_conversation){
            if(messages.get(position).type.equalsIgnoreCase("IN")){
                convertView = LayoutInflater.from(context).inflate(R.layout.message_in_layout, parent, false);
            }else{
                convertView = LayoutInflater.from(context).inflate(R.layout.message_out_layout, parent, false);
                MainActivity.inbf.phoneNum = messages.get(position).messsageTo;
            }

            TextView message = (TextView) convertView.findViewById(R.id.message_body);
            TextView date = (TextView) convertView.findViewById(R.id.message_date);

            message.setText(messages.get(position).messageBody);
            date.setText(messages.get(position).dateSched);
        }

        return convertView;
    }
}
