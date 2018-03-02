package com.payvenue.txtassistant.Helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionChecker {
    private Context context;

    public ConnectionChecker(Context context) {
        super();
        this.context = context;
    }

    public boolean isConnectedToInternet() {
        boolean ok = false;
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        if (conn != null) {
            NetworkInfo[] info = conn.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        ok = true;
                        break;
                    }
                }
            }
        }

        return ok;

    }
}
