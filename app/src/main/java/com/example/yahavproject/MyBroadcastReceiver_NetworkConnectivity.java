package com.example.yahavproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.widget.Toast;

public class MyBroadcastReceiver_NetworkConnectivity extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        //Toast.makeText(context, "Intent Detected", Toast.LENGTH_SHORT).show();


/*        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        boolean isConnected = wifi != null && wifi.isConnectedOrConnecting() ||
                mobile != null && mobile.isConnectedOrConnecting();

        if (!isConnected) {
            Toast.makeText(context, "אין לך חיבור לאינטרנט", Toast.LENGTH_LONG).show();
        }*/

        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

            if (noConnectivity) {
                Toast.makeText(context, "Disconnected", Toast.LENGTH_LONG).show();
                LoginActivity.disconnected = true;
            }
            else {
                //Toast.makeText(context, "Connected", Toast.LENGTH_LONG).show();
                LoginActivity.disconnected = false;
            }
        }
    }
}