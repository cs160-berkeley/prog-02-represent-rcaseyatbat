package com.example.rcasey.findmyreps;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * Created by joleary and noon on 2/19/16 at very late in the night. (early in the morning?)
 */
public class PhoneListenerService extends WearableListenerService {

    //WearableListenerServices don't need an iBinder or an onStartCommand: they just need an onMessageReceieved.
    private static final String TOAST = "/send_toast";
    private static final String ZIP = "/new_zip";
    private static final String SELECT = "/selection";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in PhoneListenerService, got: " + messageEvent.getPath());

        if (messageEvent.getPath().equalsIgnoreCase(ZIP)) {

            // Generate a new 5 digit number between 10000 and 99999
            //Random r = new Random();
            //int new_random_zip = r.nextInt(99999 - 10000) + 10000;
            //Log.d("T", "New random zip: " + new_random_zip);
            String stringData = new String(messageEvent.getData());
            String new_random_zip = stringData;

            Log.d("T", "PhoneListener, new zip: " + new_random_zip);
            // Send the intent to the CongressionViewActivity
            Intent sendCongress = new Intent(getBaseContext(), MainActivity.class);
            sendCongress.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            sendCongress.putExtra("ZIP", new_random_zip);
            startActivity(sendCongress);

            //Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
            //sendIntent.putExtra("ZIP", new_random_zip);
            //startService(sendIntent);
        } else if (messageEvent.getPath().equalsIgnoreCase(SELECT)) {

            String stringData = new String(messageEvent.getData());

            //Log.d("T", "Full String data: " + stringData);
            String[] words = stringData.split(":");
            String zip = words[0];
            String selection = words[1];
            //Log.d("T", "New selection: " + selection);

            Intent sendDetailed = new Intent(getBaseContext(), DetailedView.class);
            //sendDetailed.putExtra("Name", "Barbara Boxer");
            sendDetailed.putExtra("Item", selection);
            sendDetailed.putExtra("ZIP", zip);
            sendDetailed.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(sendDetailed);
            // launch the detailed view

        }
        /*
        if( messageEvent.getPath().equalsIgnoreCase(TOAST) ) {

            // Value contains the String we sent over in WatchToPhoneService, "good job"
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);

            // Make a toast with the String
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, value, duration);
            toast.show();

            // so you may notice this crashes the phone because it's
            //''sending message to a Handler on a dead thread''... that's okay. but don't do this.
            // replace sending a toast with, like, starting a new activity or something.
            // who said skeleton code is untouchable? #breakCSconceptions

        } else {
            super.onMessageReceived( messageEvent );
        }
        */

    }
}
