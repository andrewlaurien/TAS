package com.payvenue.txtassistant.Helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.payvenue.txtassistant.Fragments.ConversationFragment;
import com.payvenue.txtassistant.Fragments.GroupSendFragment;
import com.payvenue.txtassistant.Fragments.IndividualSendFragment;
import com.payvenue.txtassistant.Fragments.QueueFragment;
import com.payvenue.txtassistant.MainActivity;
import com.payvenue.txtassistant.Model.Constants;
import com.payvenue.txtassistant.Model.Contacts;
import com.payvenue.txtassistant.Model.Message;
import com.payvenue.txtassistant.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class JSONApi {
    private static JSONApi parser;
    public static Context context;
    RequestQueue mRequestQueue;
    private ImageLoader imageLoader;
    public static int size = 0;
    String TAG = "JSONApi";

    public JSONApi(Context context) {
        this.context = context;
        this.mRequestQueue = getRequestQueue();

        imageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public static synchronized JSONApi getInstance(Context c) {
        context = c;
        if (parser == null) {
            parser = new JSONApi(context);
        }
        return parser;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            Cache cache = new DiskBasedCache(context.getCacheDir(), 10 * 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            mRequestQueue = new RequestQueue(cache, network);
            mRequestQueue.start();
        }
        return mRequestQueue;
    }

    public void login(String url, final String pass, final String spin) {
        url = url.replace(" ", "+");
        Log.e("url", url);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, "",
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.length() != 0) {
                                String userId = response.getString("UserID");
                                String accId = response.getString("AccountID");

                                Intent intent = new Intent(context, MainActivity.class);
                                intent.putExtra("userId", userId);
                                intent.putExtra("accId", accId);
                                intent.putExtra("pass", pass);
                                intent.putExtra("spin", spin);
                                ((Activity) context).startActivity(intent);
                                ((Activity) context).finish();
                            }else{
                                Toast.makeText(context, "Login Failed! Please check credentials.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("LOGIN", error.getMessage());

                if (error instanceof NoConnectionError) {
                    Toast.makeText(context, "No network connection", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(context, "Login Failed! Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
        mRequestQueue.add(jsonObjectRequest);
    }

    public void getMessages(String url, final String fragmentName) {
        url = url.replace(" ", "+");
        Log.e("url", url);
        final String finalUrl = url;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, "",
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            MainActivity.qf.messages.clear();
                            MainActivity.inbf.messages.clear();
                            MainActivity.ff.messages.clear();

                            if (fragmentName.equals("QueueFragment") || fragmentName.equals("InboxFragment")
                                    || fragmentName.equals("SentFragment") || fragmentName.equals("FolderFragment")
                                    || fragmentName.equals("InboxThreadFragment")) {
                                JSONArray array = response.getJSONArray("data");
                                String dateSched = "";

                                for(int i = 0; i < array.length(); i++){
                                    JSONObject object = array.getJSONObject(i);

                                    String id = object.getString("ID");
                                    String dateInit = object.getString("DateTimeInitiated");

                                    if(fragmentName.equals("InboxFragment") || fragmentName.equals("SentFragment")
                                            || fragmentName.equals("FolderFragment") || fragmentName.equals("InboxThreadFragment"))
                                        dateSched = object.getString("DateTimeScheuled");
                                    else
                                        dateSched = object.getString("DateTimeScheduled");

                                    String threadId = object.getString("ThreadID");
                                    String messsageTo = object.getString("To");
                                    String messageBody = object.getString("Message");
                                    String type = object.getString("Type");
                                    String status = object.getString("Status");

                                    String recipientName = messsageTo;

                                    if(!object.getString("Name").equals("null")) recipientName = object.getString("Name");

                                    Message message = new Message(id, dateInit, dateSched, threadId,
                                            messsageTo, messageBody, type, status, recipientName);

                                    if(fragmentName.equals("QueueFragment")) {
                                        MainActivity.qf.messages.add(message);
                                    }else if(fragmentName.equals("InboxFragment")){
                                        MainActivity.inbf.messages.add(message);
                                    }else if(fragmentName.equals("InboxThreadFragment")){
                                        MainActivity.itf.messages.add(message);
                                    }else if(fragmentName.equals("SentFragment")){
                                        MainActivity.sf.messages.add(message);
                                    }else if(fragmentName.equals("FolderFragment")){
                                        MainActivity.ff.messages.add(message);
                                    }

                                    if(i == array.length()-1){
                                        MainActivity.ft = MainActivity.fm.beginTransaction();
                                        if(fragmentName.equals("QueueFragment")) {
                                            MainActivity.ft.replace(R.id.fragment_container, MainActivity.qf).commit();
                                            try{MainActivity.qf.refreshList();}catch (Exception e){};
                                        }else if(fragmentName.equals("InboxFragment")){
                                            MainActivity.ft.replace(R.id.fragment_container, MainActivity.inbf).addToBackStack("").commit();
                                            try{MainActivity.inbf.refreshList();}catch (Exception e){};
                                        }else if(fragmentName.equals("InboxThreadFragment")){
                                            MainActivity.ft.replace(R.id.fragment_container, MainActivity.itf).commit();
                                            try{MainActivity.itf.refreshList();}catch (Exception e){};
                                        }else if(fragmentName.equals("SentFragment")){
                                            MainActivity.ft.replace(R.id.fragment_container, MainActivity.sf).commit();
                                            try{MainActivity.sf.refreshList();}catch (Exception e){};
                                        }else if(fragmentName.equals("FolderFragment")){
                                            MainActivity.ft.replace(R.id.fragment_container, MainActivity.ff).commit();
                                            try{MainActivity.ff.refreshList();}catch (Exception e){};
                                        }

                                        MainActivity.pd.dismiss();
                                    }
                                }

                                Log.e(TAG, array.length() + "");
                                if(array.length() == 0){
                                    MainActivity.ft = MainActivity.fm.beginTransaction();
                                    if(fragmentName.equals("QueueFragment")) {
                                        MainActivity.ft.replace(R.id.fragment_container, MainActivity.qf).commit();
                                        try{
                                            MainActivity.qf.noMessage.setVisibility(View.VISIBLE);
                                            MainActivity.qf.refreshList();
                                        }catch (Exception e){
                                            Log.e(TAG, e.getMessage());
                                        };
                                    }else if(fragmentName.equals("InboxFragment")){
                                        MainActivity.ft.replace(R.id.fragment_container, MainActivity.inbf).commit();
                                        try{
                                            MainActivity.inbf.noMessage.setVisibility(View.VISIBLE);
                                            MainActivity.inbf.refreshList();
                                        }catch (Exception e){
                                            Log.e(TAG, e.getMessage());
                                        };
                                    }else if(fragmentName.equals("InboxThreadFragment")){
                                        MainActivity.ft.replace(R.id.fragment_container, MainActivity.itf).commit();
                                        try{
                                            MainActivity.itf.noMessage.setVisibility(View.VISIBLE);
                                            MainActivity.itf.refreshList();
                                        }catch (Exception e){
                                            Log.e(TAG, e.getMessage());
                                        };
                                    }else if(fragmentName.equals("SentFragment")){
                                        MainActivity.ft.replace(R.id.fragment_container, MainActivity.sf).commit();
                                        try{
                                            MainActivity.sf.noMessage.setVisibility(View.VISIBLE);
                                            MainActivity.sf.refreshList();
                                        }catch (Exception e){
                                            Log.e(TAG, e.getMessage());
                                        };
                                    }else if(fragmentName.equals("FolderFragment")){
                                        MainActivity.ft.replace(R.id.fragment_container, MainActivity.ff).commit();
                                        try{
                                            MainActivity.ff.noMessage.setVisibility(View.VISIBLE);
                                            MainActivity.ff.refreshList();
                                        }catch (Exception e){
                                            Log.e(TAG, e.getMessage());
                                        };
                                    }

                                    MainActivity.pd.dismiss();
                                }else{

                                }
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("GETMESSAGE", error.getMessage());

                if (error instanceof NoConnectionError) {
                    Toast.makeText(context, "No network connection", Toast.LENGTH_SHORT).show();
                    MainActivity.pd.dismiss();
                } else getMessages(finalUrl, fragmentName);
            }
        });
        mRequestQueue.add(jsonObjectRequest);
    }

    public void getFolders(String url) {
        url = url.replace(" ", "+");
        Log.e("url", url);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, "",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        final Calendar cal = Calendar.getInstance();

                        try {
                            Menu menu = MainActivity.menu.findItem(R.id.folder_group).getSubMenu();
                            for(int i = 0; i < response.length(); i++){
                                JSONObject folders = response.getJSONObject(i);

                                final String folderId = "10" + folders.getString("FolderID");
                                String folderName = folders.getString("FolderName");

                                menu.add(R.id.menu_folder, Integer.parseInt(folderId), i, folderName).setIcon(R.drawable.folder);

                                menu.findItem(Integer.parseInt(folderId)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        MainActivity.pd = ProgressDialog.show(context, "Loading", "Please wait...", false, false);
                                        String url = Constants.url + "start=0&cmd=getMessages&select=FOLDER&userID=" + MainActivity.userId + "&accountID=" +
                                                MainActivity.accId + "&month=" + (cal.get(Calendar.MONTH)+1) + "&year=" + cal.get(Calendar.YEAR) +
                                                "&folderID=" + folderId.substring(2) + "0001";
                                        JSONApi.getInstance(context).getMessages(url, "FolderFragment");
                                        return false;
                                    }
                                });

                                JSONArray subFolders = new JSONArray(folders.getString("SubFolders"));

                                if(subFolders.length() > 0){
                                    Menu subMenu =  MainActivity.menu.findItem(R.id.subfolder_group).getSubMenu();

                                    for(int j = 0; j < subFolders.length(); j++) {
                                        JSONObject subFolder = subFolders.getJSONObject(j);
                                        Log.e(TAG, subFolder.getString("Name"));

                                        final String subFolderId = "10" + folderId.substring(2) + subFolder.getString("ID");
                                        String subFolderName = folderName + "/" + subFolder.getString("Name");

                                        subMenu.add(R.id.menu_subfolder, Integer.parseInt(subFolderId), i, subFolderName).setIcon(R.drawable.folder);

                                        subMenu.findItem(Integer.parseInt(subFolderId)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                            @Override
                                            public boolean onMenuItemClick(MenuItem item) {
                                                MainActivity.pd = ProgressDialog.show(context, "Loading", "Please wait...", false, false);
                                                String url = Constants.url + "start=0&cmd=getMessages&select=FOLDER&userID=" + MainActivity.userId + "&accountID=" +
                                                        MainActivity.accId + "&month=" + (cal.get(Calendar.MONTH)+1) + "&year=" + cal.get(Calendar.YEAR) +
                                                        "&folderID=" + subFolderId.substring(2) + "01";
                                                JSONApi.getInstance(context).getMessages(url, "FolderFragment");
                                                return false;
                                            }
                                        });
                                    }

                                }else {

                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("GETFOLDERS", error.getMessage());

                if (error instanceof NoConnectionError) {
                    Toast.makeText(context, "No network connection", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(context, "Login Failed! Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
        mRequestQueue.add(jsonArrayRequest);
    }

    public void getContacts(String url, final String fragmentName) {
        url = url.replace(" ", "+");
        Log.e("url", url);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, "",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            MainActivity.bf.contacts.clear();

                            for(int i = 0; i < response.length(); i++){
                                JSONObject contact = response.getJSONObject(i);

                                if(fragmentName.equals("Individual")) {
                                    String name = contact.getString("FULLNAME");
                                    String number = contact.getString("Extra1");
                                    MainActivity.bf.contacts.add(new Contacts(name, number));
                                }else{
                                    String name = contact.getString("GroupName");
                                    MainActivity.bf.contacts.add(new Contacts(name, ""));
                                }

                                if (i == response.length() - 1) {
                                    try { MainActivity.bf.refreshList();} catch (Exception e) {}

                                    MainActivity.ft = MainActivity.fm.beginTransaction();
                                    MainActivity.ft.replace(R.id.fragment_container, MainActivity.bf).commit();
                                }
                            }

                            if(response.length() == 0){
                                try { MainActivity.bf.refreshList();} catch (Exception e) {}

                                MainActivity.ft = MainActivity.fm.beginTransaction();
                                MainActivity.ft.replace(R.id.fragment_container, MainActivity.bf).commit();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("GETCONTACTS", error.getMessage());

                if (error instanceof NoConnectionError) {
                    Toast.makeText(context, "No network connection", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(context, "Login Failed! Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
        mRequestQueue.add(jsonArrayRequest);
    }

    public void sendMessage(String url) {
        url = url.replace(" ", "+");
        Log.e("url", url);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, "",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        MainActivity.ft = MainActivity.fm.beginTransaction();
                        if(MainActivity.broadcastFragName.equals("Individual")) {
                            MainActivity.isf = new IndividualSendFragment();
                            MainActivity.isf.contacts.clear();
                            MainActivity.ft.replace(R.id.fragment_container, MainActivity.isf).commit();
                        }else if(MainActivity.broadcastFragName.equals("Group")){
                            MainActivity.gsf = new GroupSendFragment();
                            MainActivity.gsf.contacts.clear();
                            MainActivity.ft.replace(R.id.fragment_container, MainActivity.gsf).commit();
                        }else if(MainActivity.broadcastFragName.equals("Reply")){
                            ConversationFragment.messages.add(new Message("", "", "", "", ConversationFragment.phoneNum,
                                    ConversationFragment.messageTxt.getText().toString(), "OUT", "", ""));
                            ConversationFragment.messageTxt.setText("");
                            ConversationFragment.adapter.notifyDataSetChanged();
                        }

                        Toast.makeText(context, "Message is queued for sending.", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("SENDMESSAGE", error.getMessage());

                if (error instanceof NoConnectionError) {
                    Toast.makeText(context, "No network connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mRequestQueue.add(jsonArrayRequest);
    }

    public void createFolder(String url, final String name) {
        url = url.replace(" ", "+");
        Log.e("url", url);
        final String finalUrl = url;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, "",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            String message = response.getJSONObject(1).getString("Message");

                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                            if(message.equals("Folder Successfully added.")) {
                                String folderId = response.getJSONObject(0).getString("FolderID");

                                Menu menu = MainActivity.menu.findItem(R.id.folder_group).getSubMenu();
                                menu.add(R.id.menu_folder, Integer.parseInt("10" + folderId),
                                        Integer.parseInt(folderId), name).setIcon(R.drawable.folder);
                            }else if(message.equals("Label Successfully added.")){
                                String folderId = response.getJSONObject(0).getString("FolderID");
                                String subFolderId = response.getJSONObject(0).getString("SubFolderID");

                                Menu subMenu = MainActivity.menu.findItem(R.id.subfolder_group).getSubMenu();
                                subMenu.add(R.id.menu_subfolder, Integer.parseInt("10" + folderId + subFolderId),
                                        Integer.parseInt(subFolderId), name).setIcon(R.drawable.folder);
                                Log.e(TAG, "Sub add");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("CREATEFOLDER", error.getMessage());

                if (error instanceof NoConnectionError) {
                    Toast.makeText(context, "No network connection", Toast.LENGTH_SHORT).show();
                }else createFolder(finalUrl, name);
            }
        });
        mRequestQueue.add(jsonArrayRequest);
    }

    public void moveMessage(String url) {
        url = url.replace(" ", "+");
        Log.e("url", url);
        final String finalUrl = url;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, "",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Toast.makeText(context, "Message transfered.", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("MOVEMESSAGE", error.getMessage());

                if (error instanceof NoConnectionError) {
                    Toast.makeText(context, "No network connection", Toast.LENGTH_SHORT).show();
                }else moveMessage(finalUrl);
            }
        });
        mRequestQueue.add(jsonArrayRequest);
    }

    public void cancelMessage(String url, final int position) {
        url = url.replace(" ", "+");
        Log.e("url", url);
        final String finalUrl = url;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, "",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Toast.makeText(context, "Message cancelled.", Toast.LENGTH_SHORT).show();
                        QueueFragment.messages.remove(position);
                        QueueFragment.refreshList();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("CANCELMESSAGE", error.getMessage());

                if (error instanceof NoConnectionError) {
                    Toast.makeText(context, "No network connection", Toast.LENGTH_SHORT).show();
                }else cancelMessage(finalUrl, position);
            }
        });
        mRequestQueue.add(jsonArrayRequest);
    }

    public void changeDetails(String url) {
        url = url.replace(" ", "+");
        Log.e("url", url);
        final String finalUrl = url;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, "",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            Toast.makeText(context, response.getJSONObject(0).getString("Message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("CHANGEDETAILS", error.getMessage());

                if (error instanceof NoConnectionError) {
                    Toast.makeText(context, "No network connection", Toast.LENGTH_SHORT).show();
                }else changeDetails(finalUrl);
            }
        });
        mRequestQueue.add(jsonArrayRequest);
    }
}
