package com.example.rcasey.findmyreps;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

/**
 * Created by joleary and noon on 2/19/16 at very late in the night. (early in the morning?)
 */
public class WatchListenerService extends WearableListenerService {
    // In PhoneToWatchService, we passed in a path, either "/FRED" or "/LEXY"
    // These paths serve to differentiate different phone-to-watch messages

    private static final String NEW_ZIP = "/ZIP";


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in WatchListenerService!!, got: " + messageEvent.getPath());
        //use the 'path' field in sendmessage to differentiate use cases
        //Log.d("T", new String( messageEvent.getData() ));
        //Log.d("T", "in WatchListenerService, data is: " + messageEvent.getData().toString());

        if( messageEvent.getPath().equalsIgnoreCase( NEW_ZIP ) ) {

            Log.d("T", "Starting new MainGridView Pager with zip: " + new String( messageEvent.getData() ));

            String new_zip = new String( messageEvent.getData() );

            Intent intent = new Intent(this, MainGridViewPager.class );
            //you need to add this flag since you're starting a new activity from a service
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.putExtra("ZIP", new_zip);
            startActivity(intent);
        }

        else {
            super.onMessageReceived( messageEvent );
        }

    }
}