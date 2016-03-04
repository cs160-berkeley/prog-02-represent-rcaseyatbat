package com.example.rcasey.findmyreps;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by joleary and noon on 2/19/16 at very late in the night. (early in the morning?)
 */
public class WatchToPhoneService extends Service implements GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient mWatchApiClient;
    private List<Node> nodes = new ArrayList<>();
    private Service this_service;


    private Intent mIntent;

    @Override
    public void onCreate() {
        super.onCreate();

        this_service = this;


        //initialize the googleAPIClient for message passing
        mWatchApiClient = new GoogleApiClient.Builder( this )
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .build();
        //and actually connect it
        mWatchApiClient.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("T", "Destroying Watch To phone");
        mWatchApiClient.disconnect();
    }


    @Override
    public IBinder onBind(Intent intent) {
        //mIntent = intent;
        return null;
    }

    public int onStartCommand (Intent intent, int flags, int startId) {
        //data=(String) intent.getExtras().get("data");
        mIntent = intent;
        return START_STICKY;
    }

    @Override //alternate method to connecting: no longer create this in a new thread, but as a callback
    public void onConnected(final Bundle bundle) {
        Log.d("T", "in onconnected");
        Wearable.NodeApi.getConnectedNodes(mWatchApiClient)
                .setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                        nodes = getConnectedNodesResult.getNodes();

                        final String message_type = mIntent.getExtras().getString("MESSAGE_TYPE");

                        if (message_type.equals("ZIP")) {
                            Log.d("T", "Sending New Zip Message");
                            Integer new_zip = mIntent.getExtras().getInt("ZIP");
                            sendMessage("/new_zip", new_zip.toString());
                        } else if (message_type.equals("SELECTION")) {
                            Integer selection = mIntent.getExtras().getInt("I");

                            Integer zip = mIntent.getExtras().getInt("ZIP");

                            String finalString = zip.toString() + ":" + selection.toString();
                            sendMessage("/selection", finalString);
                        }

                        this_service.stopSelf();
                    }
                });
    }

    @Override //we need this to implement GoogleApiClient.ConnectionsCallback
    public void onConnectionSuspended(int i) {}



    private void sendMessage(final String path, final String text ) {
        for (Node node : nodes) {
            Wearable.MessageApi.sendMessage(
                    mWatchApiClient, node.getId(), path, text.getBytes());
        }
    }

}
