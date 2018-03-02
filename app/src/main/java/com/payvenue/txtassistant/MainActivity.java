package com.payvenue.txtassistant;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.payvenue.txtassistant.Fragments.BroadcastFragment;
import com.payvenue.txtassistant.Fragments.ConversationFragment;
import com.payvenue.txtassistant.Fragments.FolderFragment;
import com.payvenue.txtassistant.Fragments.GroupSendFragment;
import com.payvenue.txtassistant.Fragments.InboxThreadFragment;
import com.payvenue.txtassistant.Fragments.IndividualSendFragment;
import com.payvenue.txtassistant.Fragments.QueueFragment;
import com.payvenue.txtassistant.Fragments.SentFragment;
import com.payvenue.txtassistant.Fragments.SettingFragment;
import com.payvenue.txtassistant.Helper.ConnectionChecker;
import com.payvenue.txtassistant.Helper.JSONApi;
import com.payvenue.txtassistant.Model.Constants;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static ProgressDialog pd;
    public static FragmentManager fm;
    public static FragmentTransaction ft;
    public static Menu menu;
    public static String userId="", accId="", pass="", spin="", broadcastFragName="";
    public static boolean fromSendFrag = false, fromInboxFrag = false, fromSentFrag = false;
    public static int addedFrag=0;

    public static BroadcastFragment bf = new BroadcastFragment();
    public static QueueFragment qf = new QueueFragment();
    public static ConversationFragment inbf = new ConversationFragment();
    public static InboxThreadFragment itf = new InboxThreadFragment();
    public static IndividualSendFragment isf = new IndividualSendFragment();
    public static GroupSendFragment gsf = new GroupSendFragment();
    public static SentFragment sf = new SentFragment();
    public static FolderFragment ff = new FolderFragment();

    public static FloatingActionMenu fabBroadcast;
    FloatingActionButton fabGroup, fabIndividual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle b = getIntent().getExtras();
        userId = b.getString("userId");
        accId = b.getString("accId");
        pass = b.getString("pass");
        spin = b.getString("spin");
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        fabBroadcast = (FloatingActionMenu) findViewById(R.id.fab_broadcast);
        fabGroup = (FloatingActionButton) findViewById(R.id.fab_group);
        fabIndividual = (FloatingActionButton) findViewById(R.id.fab_individual);

        fabBroadcast.setClosedOnTouchOutside(true);

        fabGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MainActivity.bf.searchTxt.setText("");
                } catch (Exception e) {
                }
                gsf.contacts.clear();
                fromSendFrag = false;
                broadcastFragName = "Group";
                String url = Constants.url + "search=&cmd=getGroups&userID=" + userId + "&accountID=" +
                        accId;
                JSONApi.getInstance(MainActivity.this).getContacts(url, "Group");
                fabBroadcast.close(true);
                fromInboxFrag = false;
                fromSentFrag = false;
                addedFrag = 0;
            }
        });

        fabIndividual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MainActivity.bf.searchTxt.setText("");
                } catch (Exception e) {
                }
                isf.contacts.clear();
                fromSendFrag = false;
                broadcastFragName = "Individual";
                String url = Constants.url + "search=&cmd=getContacts&userID=" + userId + "&accountID=" +
                        accId;
                JSONApi.getInstance(MainActivity.this).getContacts(url, "Individual");
                fabBroadcast.close(true);
                fromInboxFrag = false;
                fromSentFrag = false;
                addedFrag = 0;
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();

        menu = navigationView.getMenu();

        pd = ProgressDialog.show(this, "Loading", "Please wait...", false, false);
        Calendar cal = Calendar.getInstance();
        String url = Constants.url + "start=0&cmd=getMessages&select=INBOX&userID=" + MainActivity.userId + "&accountID=" +
                MainActivity.accId + "&month=" + (cal.get(Calendar.MONTH)+1) + "&year=" + cal.get(Calendar.YEAR) ;
        JSONApi.getInstance(this).getMessages(url, "InboxThreadFragment");

        url = Constants.url + "cmd=getFolders&userID=" + userId + "&accountID=" +
                accId;
        JSONApi.getInstance(this).getFolders(url);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if(addedFrag != 0){
            fm.popBackStackImmediate();
            addedFrag--;

            MainActivity.fabBroadcast.setVisibility(View.VISIBLE);
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to exit app?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.super.onBackPressed();
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.broadcast_individual) {
            try{ MainActivity.bf.searchTxt.setText(""); } catch (Exception e){}
            isf.contacts.clear();
            fromSendFrag = false;
            broadcastFragName = "Individual";
            String url = Constants.url + "search=&cmd=getContacts&userID=" + userId + "&accountID=" +
                    accId;
            JSONApi.getInstance(this).getContacts(url, "Individual");
        }else if(id == R.id.broadcast_group){
            try{ MainActivity.bf.searchTxt.setText(""); } catch (Exception e){}
            gsf.contacts.clear();
            fromSendFrag = false;
            broadcastFragName = "Group";
            String url = Constants.url + "search=&cmd=getGroups&userID=" + userId + "&accountID=" +
                    accId;
            JSONApi.getInstance(this).getContacts(url, "Group");
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        fromSentFrag = false;
        fromInboxFrag = false;
        MainActivity.fabBroadcast.setVisibility(View.VISIBLE);
        addedFrag = 0;

        ft = fm.beginTransaction();
        Calendar cal = Calendar.getInstance();

        if (id == R.id.nav_inbox) {
            pd = ProgressDialog.show(this, "Loading", "Please wait...", false, false);
            broadcastFragName = "INBOX";
            String url = Constants.url + "start=0&cmd=getMessages&select=INBOX&userID=" + MainActivity.userId + "&accountID=" +
                    MainActivity.accId + "&month=" + (cal.get(Calendar.MONTH)+1) + "&year=" + cal.get(Calendar.YEAR) ;
            JSONApi.getInstance(this).getMessages(url, "InboxThreadFragment");
        } else if (id == R.id.nav_queue) {
            pd = ProgressDialog.show(this, "Loading", "Please wait...", false, false);
            String url = Constants.url + "start=0&cmd=getMessages&select=QUEUE&userID=" + MainActivity.userId + "&accountID=" +
                    MainActivity.accId + "&month=" + (cal.get(Calendar.MONTH)+1) + "&year=" + cal.get(Calendar.YEAR) ;
            JSONApi.getInstance(this).getMessages(url, "QueueFragment");
        } else if (id == R.id.nav_sent) {
            pd = ProgressDialog.show(this, "Loading", "Please wait...", false, false);
            String url = Constants.url + "start=0&cmd=getMessages&select=OUTBOX&userID=" + MainActivity.userId + "&accountID=" +
                    MainActivity.accId + "&month=" + (cal.get(Calendar.MONTH)+1) + "&year=" + cal.get(Calendar.YEAR) ;
            JSONApi.getInstance(this).getMessages(url, "SentFragment");
        } else if (id == R.id.nav_setting) {
            ft.replace(R.id.fragment_container, new SettingFragment()).commit();
        } else if (id == R.id.nav_add_folder){
            View view = getLayoutInflater().inflate(R.layout.add_folder_layout, null);

            final EditText folderName = (EditText) view.findViewById(R.id.folder_name);
            Button addFolderBtn = (Button) view.findViewById(R.id.add_folderBtn);

            folderName.setFilters(new InputFilter[]{Constants.filter});

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add Folder");
            builder.setView(view);

            final AlertDialog dialog = builder.create();
            dialog.show();

            addFolderBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = folderName.getText().toString().trim();

                    if (name.isEmpty()) {
                        folderName.setError("Required");
                        folderName.requestFocus();
                    } else {
                        if (new ConnectionChecker(MainActivity.this).isConnectedToInternet()) {
                            String url = Constants.url + "cmd=CreateFolder&folderName=" + name +
                                    "&userID=" + MainActivity.userId + "&accountID=" + MainActivity.accId;
                            JSONApi.getInstance(MainActivity.this).createFolder(url, name);

                            dialog.dismiss();
                        }else Toast.makeText(MainActivity.this, "No internet connection.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else if (id == R.id.nav_add_label){
            ArrayList<String> folders = new ArrayList<>();
            ArrayAdapter<String> folderAdapter;

            View view = getLayoutInflater().inflate(R.layout.add_label_layout, null);

            final Spinner spnrFolderNames = (Spinner) view.findViewById(R.id.folder_spnr);
            final EditText folderName = (EditText) view.findViewById(R.id.folder_name);
            Button addFolderBtn = (Button) view.findViewById(R.id.add_folderBtn);

            folderName.setFilters(new InputFilter[]{Constants.filter});

            final Menu menu = MainActivity.menu.findItem(R.id.folder_group).getSubMenu();

            for (int i = 0; i < menu.size(); i++) {
                MenuItem menuItem = menu.getItem(i);
                folders.add(menuItem.getTitle().toString());
            }

            folderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, folders);
            spnrFolderNames.setAdapter(folderAdapter);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add SubFolder");
            builder.setView(view);

            final AlertDialog dialog = builder.create();
            dialog.show();

            addFolderBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MenuItem selectedItem = menu.getItem(spnrFolderNames.getSelectedItemPosition());
                    String id = selectedItem.getItemId() + "";
                    String name = folderName.getText().toString().trim();

                    if (name.isEmpty()) {
                        folderName.setError("Required");
                        folderName.requestFocus();
                    } else {
                        if (new ConnectionChecker(MainActivity.this).isConnectedToInternet()) {
                            String url = Constants.url + "cmd=CreateSubFolder&subfolderName=" + name + "&folderID=" + id.substring(2) +
                                    "&userID=" + MainActivity.userId + "&accountID=" + MainActivity.accId;
                            JSONApi.getInstance(MainActivity.this).createFolder(url, spnrFolderNames.getSelectedItem().toString() + "/" +name);

                            dialog.dismiss();
                        }else Toast.makeText(MainActivity.this, "No internet connection.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
